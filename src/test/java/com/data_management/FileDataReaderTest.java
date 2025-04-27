package com.data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileDataReaderTest {

    private DataStorage dataStorage;
    private FileDataReader fileDataReader;

    @BeforeEach
    void setUp() {
        dataStorage =  DataStorage.getInstance();;
    }

    @Test
    void testReadData_ValidFile() {
        // Simulating passing a valid file path
        String[] args = {"--output file:./test_data/mockfile.csv"};
        fileDataReader = new FileDataReader(args);

        try {
            // Read data into DataStorage
            fileDataReader.readData(dataStorage);
            assertTrue(dataStorage.getAllPatients().size() > 0, "Patients should be added to DataStorage");
        } catch (IOException e) {
            fail("IOException should not have been thrown for valid file.");
        }
    }

    @Test
    void testReadData_WrongFormatFile() {
        // Simulating passing a wrongly formated file path
        String[] args = {"--output file:./test_data/malformedfile.csv"};
        fileDataReader = new FileDataReader(args);

        try {
            fileDataReader.readData(dataStorage);
            assertTrue(dataStorage.getAllPatients().size() > 0, "Data should be added despite malformed lines");
        } catch (IOException e) {
            fail("IOException should not have been thrown for malformed file.");
        }
    }

    @Test
    void testReadData_EmptyFile() {
        // Simulating an empty file
        String[] args = {"--output file:./test_data/emptyfile.csv"};
        fileDataReader = new FileDataReader(args);

        try {
            fileDataReader.readData(dataStorage);
            assertTrue(dataStorage.getAllPatients().isEmpty(), "No data should be added for empty file");
        } catch (IOException e) {
            fail("IOException should not have been thrown for empty file.");
        }
    }

    @Test
    void testReadData_FileNotFound() {
        // Simulating a file not found scenario
        String[] args = {"--output file:/invalid/path/to/nonexistentfile.csv"};
        fileDataReader = new FileDataReader(args);

        try {
            fileDataReader.readData(dataStorage);
            fail("IOException should have been thrown for non-existent file.");
        } catch (IOException e) {
            // Expected exception, test passes
        }
    }

    @Test
    void testReadData_InvalidArgument() {
        // Simulate missing or invalid argument
        String[] args = {}; // Empty argument list
        fileDataReader = new FileDataReader(args);

        try {
            fileDataReader.readData(dataStorage);
            fail("IllegalArgumentException should have been thrown due to missing file path argument.");
        } catch (IllegalArgumentException | IOException e) {
            // Expected exception, test passes
        }
    }



}