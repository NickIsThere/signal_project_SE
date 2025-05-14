package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;

    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        String message;
        try {
            // Re format as JSON
            message = String.format(
                    "{" + "\"patientId\":%d," + "\"timestamp\":%d," + "\"recordType\":\"%s\"," + "\"measurementValue\":\"%s\"" + "}",
                    patientId, timestamp, label, data
            );
        } catch (Exception e) {
            System.err.println("Error building JSON message for patient " + patientId);
            e.printStackTrace();
            return;
        }

        // Broadcast to all active WebSocket connections
        for (WebSocket conn : server.getConnections()) {
            try {
                conn.send(message);
            } catch (Exception sendEx) {
                System.err.println("Failed to send message to " + conn.getRemoteSocketAddress());
                sendEx.printStackTrace();
            }
        }
    }


    private static class SimpleWebSocketServer extends WebSocketServer {

        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }
    }
}
