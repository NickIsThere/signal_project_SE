package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class FileDataReader implements DataReader{

    /**
     * Reads data from a specified file and stores it in the provided DataStorage object.
     *
     * This method assumes that the file contains data in the following format:
     * - patientId: An integer identifier for the patient (e.g., 1)
     * - measurementValue: A numeric value representing the health metric (e.g., 72.5)
     * - recordType: A string describing the type of health record (e.g., "HeartRate", "BloodPressure")
     * - timestamp: A long representing the time of the measurement in milliseconds since Unix epoch
     *
     * @param dataStorage the storage where parsed data will be saved
     * @param args the command-line arguments passed to the program
     * @throws IOException if there is an error reading the file
     */
    @Override
    public void readData(DataStorage dataStorage, String[] args) throws IOException {

        // Get the file path from the command-line arguments (e.g., --output file:<output_dir>)
        String outputFilePath = getFilePathFromArgs(args);  // Call a method to get the file path

        // Create a File object to access the file
        File file = new File(outputFilePath);

        // Use BufferedReader to read the file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            // Read each line of the file until the end
            while ((line = reader.readLine()) != null) {

                // Split the line into parts using a comma as the delimiter (CSV format)
                String[] parts = line.split(",");

                // Ensure that the line has exactly 4 parts: patientId, measurementValue, recordType, timestamp
                if (parts.length == 4) {
                    try {
                        // Parse the components into the appropriate types
                        int patientId = Integer.parseInt(parts[0].trim());            // Parse patientId as Integer
                        double measurementValue = Double.parseDouble(parts[1].trim()); // Parse measurementValue as Double
                        String recordType = parts[2].trim();                           // Record type as String (e.g., "HeartRate")
                        long timestamp = Long.parseLong(parts[3].trim());             // Timestamp as Long (in milliseconds)

                        // Store the parsed data into DataStorage
                        // The addPatientData method will add or update the patient's record in the DataStorage object
                        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

                    } catch (NumberFormatException e) {
                        // If there’s an issue parsing the numbers, print the error and skip this line
                        System.err.println("Error parsing line: " + line);
                        e.printStackTrace(); // Print the error details for debugging
                    }
                } else {
                    // If the line doesn't have exactly 4 parts, it's considered malformed, so we skip it
                    System.err.println("Malformed line (skipping): " + line);
                }
            }

        } catch (IOException e) {
            // If there’s an issue reading the file (e.g., file not found), throw an IOException
            throw new IOException("Error reading file: " + outputFilePath, e);
        }
    }

    /**
     * Retrieves the file path from the command-line arguments passed to the program.
     *
     * @param args the command-line arguments passed to the program
     * @return the path to the output file specified by the user via command-line arguments
     * @throws IllegalArgumentException if no valid output file path argument is found
     */
    private String getFilePathFromArgs(String[] args) {
        // Check if any arguments were passed
        if (args.length == 0) {
            throw new IllegalArgumentException("No arguments provided. Expected --output file:<output_dir>");
        }

        // Iterate over the command-line arguments to find the one that specifies the file path
        for (String arg : args) {
            if (arg.startsWith("--output file:")) {
                // Extract the file path after the "--output file:" prefix
                return arg.substring("--output file:".length());
            }
        }

        // If the required argument is not found, throw an exception
        throw new IllegalArgumentException("Invalid argument format. Expected --output file:<output_dir>");
    }

    /**
     * Main method to run the program.
     *
     * @param args command-line arguments passed to the program
     */
    public static void main(String[] args) {
        // Create an instance of DataStorage where the parsed data will be stored
        DataStorage dataStorage = new DataStorage();

        // Create an instance of FileDataReader to read and process the file
        FileDataReader fileDataReader = new FileDataReader();

        try {
            // Call readData method to read the file and store the data into DataStorage
            fileDataReader.readData(dataStorage, args);

            // Optionally, you can print or process the stored data here
            System.out.println("Data has been successfully read and stored!");

        } catch (IOException e) {
            // Handle any IO exceptions that occur (e.g., file not found)
            e.printStackTrace();
        }
    }

}
