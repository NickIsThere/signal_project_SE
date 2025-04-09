package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface representing a data generator for a patient.
 */
public interface PatientDataGenerator {
    /**
     * Generates health data for a specific patient and outputs it using the provided strategy.
     *
     * @param patientId       The ID of the patient to generate data for.
     * @param outputStrategy  The strategy to use for data output.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
