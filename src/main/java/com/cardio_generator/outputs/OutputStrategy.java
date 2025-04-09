package com.cardio_generator.outputs;

/**
 * Interface for defining strategies to output generated patient data.
 */
public interface OutputStrategy {
    /**
     * Outputs the generated data for a specific patient.
     *
     * @param patientId  The ID of the patient whose data is to be generated.
     * @param timestamp  The timestamp of when the data is being generated.
     * @param label      A label declaring what kind of data is being outputted.
     * @param data       The data itself to be output.
     */
    void output(int patientId, long timestamp, String label, String data);
}
