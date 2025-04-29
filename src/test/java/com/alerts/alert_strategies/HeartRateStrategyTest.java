// src/test/java/com/alerts/Strategy/HeartRateStrategyTest.java
package com.alerts.alert_strategies;

import com.alerts.Alert;
import com.alerts.Strategy.HeartRateStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeartRateStrategyTest {

    private DataStorage storage;

    @BeforeEach
    void setup() {
        storage = DataStorage.getInstance();
        storage.clearDataForTesting();
    }

    @Test
    void noAlertWhenNoAbnormalPeak() throws Exception {
        Patient patient = new Patient(1);
        new HeartRateStrategy().checkAlert(patient, storage);

        List<Alert> alerts = getAlerts();
        assertTrue(alerts.isEmpty(), "Expected no alerts when there are no ECG records");
    }

    @Test
    void alertGeneratedWhenPeakAboveThreshold() throws Exception {
        Patient patient = new Patient(1);
        // five baseline ECG readings at 60 bpm
        for (int i = 1; i <= 5; i++) {
            patient.addRecord(60.0, "ECG", i);
        }
        // one spike at 200 bpm
        patient.addRecord(200.0, "ECG", 6);

        new HeartRateStrategy().checkAlert(patient, storage);

        List<Alert> alerts = getAlerts();
        assertEquals(1, alerts.size(), "Expected exactly one abnormal-peak alert");
        Alert alert = alerts.get(0);
        assertEquals("1", alert.getPatientId());
        assertEquals("ECG - Abnormal ECG Peak", alert.getCondition());
    }

    @SuppressWarnings("unchecked")
    private List<Alert> getAlerts() throws Exception {
        Field f = DataStorage.class.getDeclaredField("alertLog");
        f.setAccessible(true);
        return (List<Alert>) f.get(storage);
    }
}
