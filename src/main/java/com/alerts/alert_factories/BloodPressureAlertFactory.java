package com.alerts.alert_factories;

import com.alerts.Alert;

/**
 * Blood Pressure Alert class implementing the factory design pattern
 */
public class BloodPressureAlertFactory extends AlertFactory{

    /**
     * Creates a Blood Pressure-specific alert for a patient.
     * This method formats the alert message by prefixing the condition with "BloodPressure - "
     * to clearly indicate the type of alert, and attaches the timestamp.
     *
     * @param patientId the ID of the patient
     * @param condition the condition triggering the alert
     * @param timestamp the time at which the condition was detected
     * @return a new Alert instance with formatted Blood Pressure alert details
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "BloodPressure - " + condition, timestamp);
    }
}
