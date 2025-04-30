package com.alerts.alert_strategies;

import com.alerts.Strategy.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OxygenSaturationStrategyTest {

    private DataStorage storage;
    private Patient patient;

    @BeforeEach
    void setup() {
        storage = DataStorage.getInstance();
        storage.clearDataForTesting();

        patient = new Patient(2);
        long now = System.currentTimeMillis();

        patient.addRecord(91, "SpO2", now - 20000); // low
        patient.addRecord(96, "SpO2", now - 10000);
        patient.addRecord(88, "SpO2", now); // drop
    }

    @Test
    void testCheckAlertGeneratesLowAndDropSpO2() {
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        strategy.checkAlert(patient, storage);

        assertFalse(storage.getAlertLog().isEmpty());
        assertTrue(storage.getAlertLog().stream().anyMatch(a -> a.getCondition().contains("Low SpO2")));
        assertTrue(storage.getAlertLog().stream().anyMatch(a -> a.getCondition().contains("Rapid SpO2 Drop")));
    }
}
