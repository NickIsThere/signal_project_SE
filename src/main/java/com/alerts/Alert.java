package com.alerts;

import com.alerts.alert_decorator.AlertComponent;
import com.data_management.DataStorage;

/**
 * Represents a basic alert for a specific patient and condition.
 */
public class Alert implements AlertComponent {
    private String patientId;
    private String condition;
    private long timestamp;

    /**
     * Constructs a new Alert instance.
     *
     * @param patientId the unique identifier for the patient
     * @param condition the medical condition that triggered the alert
     * @param timestamp the time at which the alert was generated (in milliseconds)
     */
    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Triggers the alert by printing its details and logging it into the provided data storage.
     *
     * @param dataStorage the DataStorage instance responsible for saving the alert
     */
    @Override
    public void trigger(DataStorage dataStorage) {
        // core trigger behavior
        System.out.println(this);
        dataStorage.saveAlertInLog(this);
    }

    @Override
    public String toString() {
        return String.format("Alert[patient=%s, condition=%s, time=%d]", patientId, condition, timestamp);
    }
}
