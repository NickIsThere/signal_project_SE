package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

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

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        throw new UnsupportedOperationException("Batch read not supported by WebSocketDataReader");
    }

    @Override
    public void readContinuousData(URI websocketUri, DataStorage dataStorage) throws IOException {
        client = new WebSocketClient(websocketUri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to WebSocket server: " + websocketUri);
            }

            @Override
            public void onMessage(String message) {
                if (!message.startsWith("{") || !message.endsWith("}")) {
                    System.err.println("Corrupted message (skipped): " + message);
                    return;
                }

                try {
                    Map<String, String> map = Arrays.stream(
                                    message.replaceAll("[\\{\\}\"]", "").split(",")
                            )
                            .map(s -> s.split(":", 2))
                            .collect(Collectors.toMap(
                                    a -> a[0].trim(),
                                    a -> a.length > 1 ? a[1].trim() : "" // Avoid IndexOutOfBoundsException
                            ));

                    // Extract fields with validation
                    String patientIdStr = map.get("patientId");
                    String timestampStr = map.get("timestamp");
                    String recordType = map.get("recordType");
                    String rawValue = map.get("measurementValue");

                    if (patientIdStr == null || timestampStr == null || recordType == null || rawValue == null) {
                        System.err.println("Missing required fields in message: " + message);
                        return;
                    }


                    int patientId = Integer.parseInt(patientIdStr);
                    long timestamp = Long.parseLong(timestampStr);

                    // Clean and validate rawValue
                    String cleaned = rawValue.replaceAll("[^0-9.]+", ""); // Remove everything but digits and dot
                    if (cleaned.isEmpty()) {
                        System.err.println("Invalid measurement value in message: " + message);
                        return;
                    }

                    double measurement = Double.parseDouble(cleaned);

                    // Store data
                    dataStorage.addPatientData(patientId, measurement, recordType, timestamp);
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse numeric fields in message: " + message);
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Unexpected error while parsing/storing message: " + message);
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.err.println("WebSocket closed (" + code + "): " + reason);
                // Maybe still implement reconnect func @Milena?
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
            throw new IOException("Interrupted while connecting to WebSocket", ie);
        }
    }

    @Override
    public void close() throws IOException {
        if (client != null && !client.isClosed()) {
            client.close();
        }
    }
}