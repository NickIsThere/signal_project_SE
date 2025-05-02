package com.alerts.alert_factories;

import com.alerts.Alert;

/**
 * Blood Pressure Alert class implementing the factory design pattern
 */
public class BloodPressureAlertFactory extends AlertFactory{

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "BloodPressure - " + condition, timestamp);
    }
}
