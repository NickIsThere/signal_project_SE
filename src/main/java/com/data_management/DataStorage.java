package com.data_management;

import java.util.*;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.alert_decorator.AlertComponent;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {

    private static DataStorage instance;
    private Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.
    private List<Alert> alertLog;

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    private DataStorage() {
        this.patientMap = new HashMap<>();
        this.alertLog = new ArrayList<Alert>();
    }

    /**
     * Getter for the static instance for a singleton design pattern
     * @return instance
     */
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        patientMap.compute(patientId, (id, patient) -> {
            if (patient == null) {
                patient = new Patient(patientId);
            }
            patient.addOrUpdateRecord(measurementValue, recordType, timestamp);
            return patient;
        });
        System.out.println("[DataStorage] Added Patient. PatientId: " + patientId + " MeasurementValue: " + measurementValue + " RecordType: " + recordType + " Timestamp: " + timestamp);
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        return (patient != null)
                ? patient.getRecords(startTime, endTime)
                : Collections.emptyList();
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        DataStorage storage =  DataStorage.getInstance();;

        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);
        for (PatientRecord record : records) {
            System.out.println("Record for Patient ID: " + record.getPatientId() +
                    ", Type: " + record.getRecordType() +
                    ", Data: " + record.getMeasurementValue() +
                    ", Timestamp: " + record.getTimestamp());
        }

        AlertGenerator alertGenerator = new AlertGenerator(storage);

        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }
    public void saveAlertInLog(AlertComponent alert){
        this.alertLog.add((Alert) alert);
    }
    // method for testing
    public void clearDataForTesting() {
        patientMap.clear();
        alertLog.clear();
    }

    public List<Alert> getAlertLog() {
        // Return an unmodifiable view so nobody else can edit it
        return Collections.unmodifiableList(alertLog);
    }
}
