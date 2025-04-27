package com.alerts.alert_factories;

import com.alerts.Alert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ECGAlertFactoryTest {

    @Test
    void createAlert() {
        ECGAlertFactory factory = new ECGAlertFactory();

        String patientId = "789";
        String condition = "Abnormal ECG Peak";
        long timestamp = 1714376789002L;

        Alert alert = factory.createAlert(patientId, condition, timestamp);

        assertNotNull(alert);
        assertEquals(patientId, alert.getPatientId());
        assertEquals("ECG - " + condition, alert.getCondition());
        assertEquals(timestamp, alert.getTimestamp());
    }
}