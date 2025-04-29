package com.alerts.alert_strategies;

import com.alerts.Alert;
import com.alerts.Strategy.BloodPressureStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BloodPressureStrategyTest {

    private DataStorage storage;

    @BeforeEach
    void setup() {
        storage = DataStorage.getInstance();
        storage.clearDataForTesting();
    }

    @Test
    void criticalThresholdsTriggerAlerts() throws Exception {
        Patient patient = new Patient(6);
        List<Alert> log = storage.getAlertLog();
        // systolic too high and too low
        patient.addRecord(190.0, "Systolic", 1L);
        patient.addRecord(80.0, "Systolic", 2L);
        // diastolic too high and too low
        patient.addRecord(130.0, "Diastolic", 3L);
        patient.addRecord(50.0, "Diastolic", 4L);

        new BloodPressureStrategy().checkAlert(patient, storage);

        List<Alert> alerts = storage.getAlertLog();

        assertEquals(4, alerts.size());

    }

    @Test
    void upwardTrendTriggersAlert() throws Exception {
        Patient patient = new Patient(7);
        patient.addRecord(100.0, "Systolic", 10L);
        patient.addRecord(115.0, "Systolic", 20L);
        patient.addRecord(130.0, "Systolic", 30L);

        new BloodPressureStrategy().checkAlert(patient, storage);

        List<Alert> alerts = storage.getAlertLog();

        assertEquals(1, alerts.size());
        assertEquals("BloodPressure - Systolic Trend Alert", alerts.get(0).getCondition());
    }

    @Test
    void hypotensiveHypoxemiaTriggersAlert() throws Exception {
        Patient patient = new Patient(8);
        patient.addRecord(85.0, "Systolic", 1000L);
        patient.addRecord(90.0, "sp02",    1200L);

        new BloodPressureStrategy().checkAlert(patient, storage);

        List<Alert> alerts = storage.getAlertLog();
        assertFalse(alerts.isEmpty(), "Expected at least one alert");
    }

}
