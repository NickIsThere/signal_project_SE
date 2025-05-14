package com.data_management;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {


    @Test
    void addRecord() {

        //Arrange
        Patient patient = new Patient(1);  // Create a new patient with ID 1

        patient.addRecord(72.5, "HeartRate", 1682603452000L);  // Timestamp: 1682603452000

        // Defining a time range that includes the added record
        long startTime = 1682603400000L;  // Start time: before the record's timestamp
        long endTime = 1682603500000L;    // End time: after the record's timestamp

        // Act
        List<PatientRecord> records = patient.getRecords(startTime, endTime);  // Retrieve records within the time range

        // Assert
        assertEquals(1, records.size(), "The record should be added to the patient's list.");
        assertEquals(72.5, records.get(0).getMeasurementValue(), "The measurement value should match.");
        assertEquals("HeartRate", records.get(0).getRecordType(), "The record type should match.");
        assertEquals(1682603452000L, records.get(0).getTimestamp(), "The timestamp should match.");
    }

    @Test
    void getRecords() {
        // Arrange
        Patient patient = new Patient(1);
        patient.addRecord(72.5, "HeartRate", 1682603452000L); // Timestamp: 1682603452000
        patient.addRecord(120.5, "BloodPressure", 1682603552000L); // Timestamp: 1682603552000
        patient.addRecord(75.0, "HeartRate", 1682603652000L); // Timestamp: 1682603652000

        // Defining a time range that includes the first two records but not the third one
        long startTime = 1682603400000L;  // 1682603400000 <= timestamp <= 1682603550000
        long endTime = 1682603600000L;    // End time: 1682603600000 (this will exclude the third record)

        // Act
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        // Assert
        assertEquals(2, records.size(), "The time range should include exactly 2 records.");
        assertEquals("HeartRate", records.get(0).getRecordType(), "The first record's type should be 'HeartRate'.");
        assertEquals("BloodPressure", records.get(1).getRecordType(), "The second record's type should be 'BloodPressure'.");
    }

    @Test
    void getPatientId() {
        // Arrange
        Patient patient = new Patient(1);  // Create a patient with ID 1

        // Act & Assert
        assertEquals(1, patient.getPatientId(), "The patient ID should be 1.");
    }
}