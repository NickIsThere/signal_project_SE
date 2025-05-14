package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class FileDataReader implements DataReader{

    private String[] args; // Store the command-line arguments

    // Constructor to pass and store args[] when creating the object
    public FileDataReader(String[] args) {
        this.args = args;
    }

    /**
     * Reads data from a specified file and stores it in the provided DataStorage object.
     *
     * @param dataStorage the storage where parsed data will be saved
     * @throws IOException if there is an error reading the file
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        String outputFilePath = getFilePathFromArgs(this.args);

        File file = new File(outputFilePath);

        // Reading the file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");

                // Ensuring that the line has exactly 4 parts: patientId, measurementValue, recordType, timestamp
                if (parts.length == 4) {
                    try {
                        // Parsing the components into the appropriate types
                        int patientId = Integer.parseInt(parts[0].trim());
                        double measurementValue = Double.parseDouble(parts[1].trim());
                        String recordType = parts[2].trim();
                        long timestamp = Long.parseLong(parts[3].trim());

                        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

                    } catch (NumberFormatException e) {
                        // Error for issues with parsing numbers
                        System.err.println("Error parsing line: " + line);
                        e.printStackTrace();
                    }
                } else {
                    //Error when the line doesn't have exactly 4 parts
                    System.err.println("Malformed line (skipping): " + line);
                }
            }

        } catch (IOException e) {
            // Error for issues with reading the file
            throw new IOException("Error reading file: " + outputFilePath, e);
        }
    }

    /**
     * Retrieves the file path from the command-line arguments.
     *
     * @param args the command-line arguments passed to the program
     * @return the path to the output file specified by the user by command-line arguments
     * @throws IllegalArgumentException if no valid output file path argument is found
     */
    private String getFilePathFromArgs(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No arguments provided. Expected --output file:<output_dir>");
        }
        for (String arg : args) {
            if (arg.startsWith("--output file:")) {
                return arg.substring("--output file:".length());
            }
        }
        throw new IllegalArgumentException("Invalid argument format. Expected --output file:<output_dir>");
    }

    public static void main(String[] args) {
        DataStorage dataStorage =  DataStorage.getInstance();

        FileDataReader fileDataReader = new FileDataReader(args);

        try {
            fileDataReader.readData(dataStorage);
            System.out.println("Data has been successfully read and stored");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
