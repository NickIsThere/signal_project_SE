package com.alerts;

import com.alerts.alert_decorator.AlertComponent;
import com.data_management.DataStorage;

// Represents an alert
public class Alert implements AlertComponent {
    private String patientId;
    private String condition;
    private long timestamp;

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
