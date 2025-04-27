package com.alerts.alert_factories;

import com.alerts.Alert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BloodPressureAlertFactoryTest {

    @Test
    void createAlert() {
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();

        String patientId = "123";
        String condition = "Critical Systolic BP";
        long timestamp = 1714376789000L;

        Alert alert = factory.createAlert(patientId, condition, timestamp);

        assertNotNull(alert);
        assertEquals(patientId, alert.getPatientId());
        assertEquals("BloodPressure - " + condition, alert.getCondition());
        assertEquals(timestamp, alert.getTimestamp());
    }
}