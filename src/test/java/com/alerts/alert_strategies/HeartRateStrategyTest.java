package com.alerts.alert_strategies;

import com.alerts.Strategy.HeartRateStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeartRateStrategyTest {

    private DataStorage storage;
    private Patient patient;

    @BeforeEach
    void setup() {
        storage = DataStorage.getInstance();
        storage.clearDataForTesting();

        patient = new Patient(3);
        long now = System.currentTimeMillis();

        // 5 baseline ECGs
        patient.addRecord(70, "ECG", now - 50000);
        patient.addRecord(72, "ECG", now - 40000);
        patient.addRecord(71, "ECG", now - 30000);
        patient.addRecord(70, "ECG", now - 20000);
        patient.addRecord(74, "ECG", now - 10000);

        // spike
        patient.addRecord(120, "ECG", now);
    }

    @Test
    void testCheckAlertDetectsAbnormalECG() {
        HeartRateStrategy strategy = new HeartRateStrategy();
        strategy.checkAlert(patient, storage);

        assertFalse(storage.getAlertLog().isEmpty());
        assertTrue(storage.getAlertLog().stream().anyMatch(a -> a.getCondition().contains("Abnormal ECG Peak")));
    }
}
