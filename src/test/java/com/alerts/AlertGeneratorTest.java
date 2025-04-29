package com.alerts;


import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorTest {

    private DataStorage storage;

    @BeforeEach
    void setup() {
        storage = DataStorage.getInstance();
        storage.clearDataForTesting();
    }

    @Test
    void evaluateDataTriggersAllRelevantAlerts() throws Exception {
        Patient patient = new Patient(9);
        long t = 1000L;
        // spO2 low
        patient.addRecord(90.0, "sp02", t);
        // critical BP
        patient.addRecord(200.0, "Systolic", t + 1);
        patient.addRecord(130.0, "Diastolic", t + 2);
        // ECG abnormal peak: baseline + spike
        for (int i = 0; i < 5; i++) {
            patient.addRecord(50.0, "ECG", t + 10 + i);
        }
        patient.addRecord(150.0, "ECG", t + 16);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(patient);

        List<Alert> alerts = storage.getAlertLog();
        assertEquals(3, alerts.size());

    }

}
