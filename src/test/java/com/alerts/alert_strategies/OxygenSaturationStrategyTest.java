package com.alerts.alert_strategies;

import com.alerts.Alert;
import com.alerts.Strategy.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OxygenSaturationStrategyTest {

    private DataStorage storage;

    @BeforeEach
    void setup() {
        storage = DataStorage.getInstance();
        storage.clearDataForTesting();
    }

    @Test
    void lowSpO2SingleRecordTriggersAlert() throws Exception {
        Patient patient = new Patient(2);
        patient.addRecord(90.0, "sp02", 1000L);

        new OxygenSaturationStrategy().checkAlert(patient, storage);

        List<Alert> alerts = storage.getAlertLog();
        assertEquals(1, alerts.size());
        Alert alert = alerts.get(0);
        assertEquals("2", alert.getPatientId());
        assertEquals("BloodOxygen - Low SpO2", alert.getCondition());
    }

    @Test
    void noLowSpO2AlertWhenAboveThreshold() throws Exception {
        Patient patient = new Patient(3);
        patient.addRecord(95.0, "sp02", 1000L);

        new OxygenSaturationStrategy().checkAlert(patient, storage);

        List<Alert> alerts = storage.getAlertLog();
        assertTrue(alerts.isEmpty());
    }

    @Test
    void rapidSpO2DropWithinWindowTriggersAlert() throws Exception {
        Patient patient = new Patient(4);
        patient.addRecord(95.0, "sp02", 1000L);
        patient.addRecord(89.0, "sp02", 2000L); // drop of 6 within 1s

        new OxygenSaturationStrategy().checkAlert(patient, storage);

        List<Alert> alerts = storage.getAlertLog();
        assertEquals(2, alerts.size());
        Alert alert = alerts.get(0);
        assertEquals("4", alert.getPatientId());
        assertEquals("BloodOxygen - Low SpO2", alert.getCondition());
    }


}
