package com.alerts.alert_factories;

import com.alerts.Alert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BloodOxygenAlertFactoryTest {

    @Test
    void createAlert() {
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

        String patientId = "456";
        String condition = "Low SpO2";
        long timestamp = 1714376789001L;

        Alert alert = factory.createAlert(patientId, condition, timestamp);

        assertNotNull(alert);
        assertEquals(patientId, alert.getPatientId());
        assertEquals("BloodOxygen - " + condition, alert.getCondition());
        assertEquals(timestamp, alert.getTimestamp());
    }
}