package com.alerts.alert_factories;

import com.alerts.Alert;

/**
 * ECG Alert class implementing the factory design pattern
 */
public class ECGAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "ECG - " + condition, timestamp);
    }
}
