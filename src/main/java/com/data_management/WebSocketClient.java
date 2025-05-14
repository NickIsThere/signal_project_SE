package com.data_management;

import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * PatientWebSocketClient connects to a WebSocket server URI,
 * parses incoming CSV-formatted patient data, and stores it via DataStorage.
 */
public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private final DataStorage dataStorage;

    /**
     * Constructs the WebSocket client with the given server URI.
     *
     * @param serverUri the ws:// or wss:// endpoint
     * @throws URISyntaxException if the URI string is invalid
     */
    public WebSocketClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
        this.dataStorage = DataStorage.getInstance();
    }

    /**
     * Starts the connection synchronously.
     * @throws InterruptedException if interrupted while connecting
     */
    public void start() throws InterruptedException {
        connectBlocking();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server: " + getURI());
    }

    @Override
    public void onMessage(String message) {
        // Expected CSV: patientId,measurementValue,recordType,timestamp
        String[] parts = message.split(",");
        if (parts.length != 4) {
            System.err.println("Malformed message: " + message);
            return;
        }
        try {
            int patientId= Integer.parseInt(parts[0].trim());
            double measurementValue = Double.parseDouble(parts[1].trim());
            String recordType = parts[2].trim();
            long timestamp = Long.parseLong(parts[3].trim());

            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing message: " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.printf("WebSocket closed by %s. Code=%d Reason=%s%n",
                remote ? "server" : "client", code, reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error:");
        ex.printStackTrace();
    }
    
}
