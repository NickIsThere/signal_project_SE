package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of DataReader for real-time WebSocket data streams.
 */
public class WebSocketDataReader implements DataReader {
    private WebSocketClient client;
    private final int maxRetries = 5;
    private final long baseDelayMs = 1000;

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        throw new UnsupportedOperationException("Batch read not supported by WebSocketDataReader");
    }

    @Override
    public void readContinuousData(URI uri, DataStorage storage) throws IOException {
        connectWithRetry(uri, storage, 0);
    }

    private void connectWithRetry(URI uri, DataStorage storage, int attempt) throws IOException {
        if (attempt > maxRetries) {
            throw new IOException("Max reconnect attempts reached for " + uri);
        }

        client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to WebSocket server: " + uri);
            }

            @Override
            public void onMessage(String message) {
                // Quick sanity check
                if (message == null || !message.startsWith("{") || !message.endsWith("}")) {
                    System.err.println("Corrupted message (skipped): " + message);
                    return;
                }

                try {
                    // Parse simple JSON object into a Map<String,String>
                    Map<String, String> map = Arrays.stream(
                                    message.substring(1, message.length() - 1).split(","))
                            .map(s -> s.split(":", 2))
                            .collect(Collectors.toMap(
                                    a -> a[0].replaceAll("\"", "").trim(),
                                    a -> a[1].replaceAll("\"", "").trim()
                            ));

                    int    patientId  = Integer.parseInt(map.get("patientId"));
                    long   timestamp  = Long.parseLong(map.get("timestamp"));
                    String recordType = map.get("recordType");
                    String rawValue   = map.get("measurementValue");

                    // Handle Alert messages separately
                    if ("Alert".equalsIgnoreCase(recordType)) {
                        // Build and fire an Alert
                        com.alerts.Alert alert = new com.alerts.Alert(
                                String.valueOf(patientId),
                                rawValue,    // "triggered" or "resolved"
                                timestamp
                        );
                        com.alerts.AlertUtils.fireWithPriority(
                                alert,
                                storage,
                                com.alerts.alert_decorator.PriorityAlertDecorator.Priority.MEDIUM,
                                1,
                                0L
                        );
                        return;
                    }

                    // Otherwise treat as numeric measurement (e.g. "95.0%", "120.0", etc.)
                    String cleaned = rawValue.replaceAll("[^0-9.]+", "");
                    if (cleaned.isEmpty()) {
                        System.err.println("Invalid measurement value in message: " + message);
                        return;
                    }
                    double measurement = Double.parseDouble(cleaned);
                    storage.addPatientData(patientId, measurement, recordType, timestamp);

                } catch (Exception e) {
                    System.err.println("Failed to parse/store message: " + message);
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.err.println("WebSocket closed (" + code + "): " + reason);
                long delay = baseDelayMs * (1L << attempt);
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                    connectWithRetry(uri, storage, attempt + 1);
                } catch (Exception e) {
                    System.err.println("Reconnect attempt failed: " + e.getMessage());
                }
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("WebSocket error:");
                ex.printStackTrace();
            }
        };

        try {
            client.connectBlocking();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while connecting", ie);
        }
    }

    @Override
    public void close() throws IOException {
        if (client != null && !client.isClosed()) {
            client.close();
        }
    }
}
