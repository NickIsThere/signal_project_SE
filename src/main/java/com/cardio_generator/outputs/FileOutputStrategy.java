package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
/**
 * An implementation of the OutputStrategy interface that writes patient data to files.
 *
 */
public class FileOutputStrategy implements OutputStrategy {

    // corrected file_map to fileMap + moved to top
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();
    // changed to lower camel case
    private String baseDirectory;

    /**
     * Constructor of FileOutputStrategy.
     * @param baseDirectory The base directory where output files will be saved.
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    /**
     * creates a new file with patient data and creates a file path based on its label
     *
     * @param patientId  The ID of the patient for whom data is generated.
     * @param timestamp  The timestamp of the data.
     * @param label      The type or label of the data (e.g., "ECG", "Alert").
     * @param data       The actual data content to be written.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // corrected all instances of FileMap to fileMap
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e){
            System.err.println("Error writing to file " +filePath + ": " + e.getMessage());
        }
    }
}