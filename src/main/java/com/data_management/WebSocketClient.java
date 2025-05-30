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
     * @param serverUri the endpoint
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
        String[] parts = message.split(",");
        int patientId = Integer.parseInt(parts[0]);
        long timestamp = Long.parseLong(parts[1]);
        String label = parts[2];
        if (label.equalsIgnoreCase("Alert")) return;
        double measurementValue = Double.parseDouble(parts[3]);
        dataStorage.addPatientData(patientId, measurementValue, label, timestamp);
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
