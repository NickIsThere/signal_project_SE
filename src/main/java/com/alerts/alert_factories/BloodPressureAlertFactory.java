package com.alerts.alert_factories;

import com.alerts.Alert;

public class BloodPressureAlertFactory extends AlertFactory{

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "BloodPressure - " + condition, timestamp);
    }
}
