package com.data_management;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient and manages their medical records.
 * This class stores patient-specific data, allowing for the addition and
 * retrieval
 * of medical records based on specified criteria.
 */
public class Patient {
    private int patientId;
    private List<PatientRecord> patientRecords;

    /**
     * Constructs a new Patient with a specified ID.
     * Initializes an empty list of patient records.
     *
     * @param patientId the unique identifier for the patient
     */
    public Patient(int patientId) {
        this.patientId = patientId;
        this.patientRecords = new ArrayList<>();
    }

    /**
     * Adds a new record to this patient's list of medical records.
     * The record is created with the specified measurement value, record type, and
     * timestamp.
     *
     * @param measurementValue the measurement value to store in the record
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since UNIX epoch
     */
    public void addRecord(double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(this.patientId, measurementValue, recordType, timestamp);
        this.patientRecords.add(record);
    }

    /**
     * Retrieves a list of PatientRecord objects for this patient that fall within a specified time range.
     * Filters records based on the start and end times provided.
     *
     * @param startTime the start time of the range (milliseconds since Unix epoch)
     * @param endTime the end time of the range (milliseconds since Unix epoch)
     * @return a list of PatientRecord objects that fall within the specified time range
     */
    public List<PatientRecord> getRecords(long startTime, long endTime) {
        List<PatientRecord> filteredRecords = new ArrayList<>();

        // Loop through all patient records and add those within the time range
        for (PatientRecord record : patientRecords) {
            long recordTimestamp = record.getTimestamp();
            if (recordTimestamp >= startTime && recordTimestamp <= endTime) {
                filteredRecords.add(record);  // Add to the list if within the time range
            }
        }

        return filteredRecords;
    }

    /**
     * If a record with the same type & timestamp exists, update its value;
     * otherwise append a new record.
     */
    public void addOrUpdateRecord(double measurementValue, String recordType, long timestamp) {
        for (PatientRecord rec : patientRecords) {
            if (rec.getRecordType().equals(recordType) && rec.getTimestamp() == timestamp) {
                rec.setMeasurementValue(measurementValue);
                return;
            }
        }
        patientRecords.add(new PatientRecord(patientId, measurementValue, recordType, timestamp));
    }

    /**
     * Retrieves the patients ID
     *
     * @return an int of the patient ID
     */
    public int getPatientId() {
        return patientId;
    }
}
