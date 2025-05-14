package com.cardio_generator.outputs;

/**
 * This class defines how the data is outputted to the console
 */
public class ConsoleOutputStrategy implements OutputStrategy {

    /**
     * Defines the way that how the data is printed in the console
     *
     * @param patientId  The ID of the patient
     * @param timestamp  The timestamp of when the data is being generated
     * @param label      A label declaring what kind of data is being outputted
     * @param data       The data itself
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        System.out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
    }
}
