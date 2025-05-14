package com.data_management;

import java.io.IOException;
import java.net.URI;

public interface DataReader {

    /**
     * Reads data from a specified source and stores it in the data storage.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to the given WebSocket URI and continuously receives data messages,
     * parsing and storing them into the provided DataStorage.
     *
     * @param websocketUri the URI of the WebSocket server
     * @param dataStorage  the storage where incoming data will be stored
     * @throws IOException if there is an error during the connection or data transmission
     */
    void readContinuousData(URI websocketUri, DataStorage dataStorage) throws IOException;

    /**
     * Closes any active connections or resources associated with continuous data reading.
     *
     * @throws IOException if there is an error closing the connection
     */
    void close() throws IOException;
}
