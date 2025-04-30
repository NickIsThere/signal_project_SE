package com.alerts.alert_strategies;

import com.alerts.Strategy.BloodPressureStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BloodPressureStrategyTest {

    private DataStorage storage;
    private Patient patient;

    @BeforeEach
    void setup() {
        storage = DataStorage.getInstance();
        storage.clearDataForTesting();

        patient = new Patient(1);
        long now = System.currentTimeMillis();

        // Critical systolic
        patient.addRecord(190, "Systolic", now - 30000);
        // Critical diastolic
        patient.addRecord(130, "Diastolic", now - 25000);
        // Trend systolic
        patient.addRecord(100, "Systolic", now - 20000);
        patient.addRecord(115, "Systolic", now - 15000);
        patient.addRecord(130, "Systolic", now - 10000);
        // Hypoxemia
        patient.addRecord(85, "Systolic", now - 1000);
        patient.addRecord(91, "sp02", now - 500);
    }

    @Test
    void testCheckAlertGeneratesExpectedAlerts() {
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        strategy.checkAlert(patient, storage);

        assertFalse(storage.getAlertLog().isEmpty());
        assertTrue(storage.getAlertLog().stream().anyMatch(a -> a.getCondition().contains("Critical Systolic BP")));
        assertTrue(storage.getAlertLog().stream().anyMatch(a -> a.getCondition().contains("Critical Diastolic BP")));
        assertTrue(storage.getAlertLog().stream().anyMatch(a -> a.getCondition().contains("Systolic Trend")));
        assertTrue(storage.getAlertLog().stream().anyMatch(a -> a.getCondition().contains("Hypotensive Hypoxemia")));
    }
}
