package com;

import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        if (args.length < 2 || !"--ws".equals(args[0])) {
            System.err.println("Usage: Main --ws ws://<host>:<port>");
            System.exit(1);
        }
        URI uri = new URI(args[1]);
        DataStorage storage = DataStorage.getInstance();
        WebSocketDataReader reader = new WebSocketDataReader();
        reader.readContinuousData(uri, storage);
        Thread.currentThread().join();
    }
}



