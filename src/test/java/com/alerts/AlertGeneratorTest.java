package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorTest {

    private List<Alert> savedAlerts;
    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

    @BeforeEach
    void setUp() {
        // Stub DataStorage that captures saved alerts
        savedAlerts = new ArrayList<>();
        dataStorage = new DataStorage() {
            @Override
            public void saveAlertInLog(Alert alert) {
                savedAlerts.add(alert);
            }
        };
        alertGenerator = new AlertGenerator(dataStorage);
    }

    // --- Positive Alert Tests ---

    @Test
    void evaluateData_triggersCriticalSystolicBP() {
        Patient patient = new Patient(1);
        patient.addRecord(200.0, "Systolic", 1000L);

        alertGenerator.evaluateData(patient);

        assertEquals(1, savedAlerts.size(), "Expected one alert to be saved");
        Alert alert = savedAlerts.get(0);
        assertEquals("1", alert.getPatientId());
        assertEquals("Critical Systolic BP", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }

    @Test
    void evaluateData_triggersCriticalDiastolicBP() {
        Patient patient = new Patient(2);
        patient.addRecord(50.0, "Diastolic", 2000L);

        alertGenerator.evaluateData(patient);

        assertEquals(1, savedAlerts.size(), "Expected one alert to be saved");
        Alert alert = savedAlerts.get(0);
        assertEquals("2", alert.getPatientId());
        assertEquals("Critical Diastolic BP", alert.getCondition());
        assertEquals(2000L, alert.getTimestamp());
    }

    @Test
    void evaluateData_detectsSystolicTrend() {
        Patient patient = new Patient(3);
        patient.addRecord(100.0, "Systolic", 1000L);
        patient.addRecord(115.0, "Systolic", 2000L);
        patient.addRecord(130.0, "Systolic", 3000L);

        alertGenerator.evaluateData(patient);

        boolean found = savedAlerts.stream().anyMatch(a ->
                "3".equals(a.getPatientId()) &&
                        "Systolic Trend Alert".equals(a.getCondition()) &&
                        a.getTimestamp() == 3000L
        );
        assertTrue(found, "Expected a Systolic Trend Alert for patient 3 at timestamp 3000L");
    }

    @Test
    void evaluateData_triggersLowSpO2() {
        Patient patient = new Patient(4);
        patient.addRecord(90.0, "sp02", 4000L);

        alertGenerator.evaluateData(patient);

        assertEquals(1, savedAlerts.size(), "Expected one alert to be saved");
        Alert alert = savedAlerts.get(0);
        assertEquals("4", alert.getPatientId());
        assertEquals("Low SpO2", alert.getCondition());
        assertEquals(4000L, alert.getTimestamp());
    }

    @Test
    void evaluateData_triggersRapidSpO2Drop() {
        Patient patient = new Patient(5);
        patient.addRecord(98.0, "sp02", 5000L);
        patient.addRecord(90.0, "sp02", 305000L); // drop of 8 within ~5 min

        alertGenerator.evaluateData(patient);

        boolean found = savedAlerts.stream().anyMatch(a ->
                "5".equals(a.getPatientId()) &&
                        "Rapid SpO2 Drop".equals(a.getCondition()) &&
                        a.getTimestamp() == 305000L
        );
        assertTrue(found, "Expected a Rapid SpO2 Drop alert for patient 5 at timestamp 305000L");
    }

    @Test
    void evaluateData_triggersHypotensiveHypoxemia() {
        Patient patient = new Patient(6);
        patient.addRecord(85.0, "Systolic", 6000L);
        patient.addRecord(90.0, "sp02", 106000L);

        alertGenerator.evaluateData(patient);

        boolean found = savedAlerts.stream().anyMatch(a ->
                "6".equals(a.getPatientId()) &&
                        "Hypotensive Hypoxemia".equals(a.getCondition()) &&
                        a.getTimestamp() == 106000L
        );
        assertTrue(found, "Expected a Hypotensive Hypoxemia alert for patient 6 at timestamp 106000L");
    }

    @Test
    void evaluateData_triggersAbnormalECGPeak() {
        Patient patient = new Patient(7);
        for (int i = 0; i < 5; i++) {
            patient.addRecord(10.0, "ECG", 7000L + i * 1000L);
        }
        patient.addRecord(16.0, "ECG", 13000L);

        alertGenerator.evaluateData(patient);

        assertEquals(1, savedAlerts.size(), "Expected one ECG alert");
        Alert alert = savedAlerts.get(0);
        assertEquals("7", alert.getPatientId());
        assertEquals("Abnormal ECG Peak", alert.getCondition());
        assertEquals(13000L, alert.getTimestamp());
    }

    @Test
    void triggerAlert_savesAlertViaDataStorage() throws Exception {
        Alert testAlert = new Alert("8", "Direct Alert", 8000L);
        Method trigger = AlertGenerator.class.getDeclaredMethod("triggerAlert", Alert.class);
        trigger.setAccessible(true);

        trigger.invoke(alertGenerator, testAlert);

        assertEquals(1, savedAlerts.size(), "Expected one alert saved via triggerAlert");
        assertSame(testAlert, savedAlerts.get(0), "Saved instance should match");
    }

    // --- Negative & Boundary Tests ---

    @Test
    void evaluateData_noAlertsForNormalBP() {
        Patient patient = new Patient(9);
        patient.addRecord(120.0, "Systolic", 1000L);
        patient.addRecord(75.0, "Diastolic", 2000L);
        patient.addRecord(125.0, "Systolic", 3000L);
        patient.addRecord(78.0, "Diastolic", 4000L);

        alertGenerator.evaluateData(patient);

        assertTrue(savedAlerts.isEmpty(), "Expected no alerts for normal BP readings");
    }

    @Test
    void evaluateData_detectsDiastolicDecreasingTrend() {
        Patient patient = new Patient(10);
        patient.addRecord(100.0, "Diastolic", 1000L);
        patient.addRecord(85.0, "Diastolic", 2000L);
        patient.addRecord(70.0, "Diastolic", 3000L);

        alertGenerator.evaluateData(patient);

        boolean found = savedAlerts.stream().anyMatch(a ->
                "10".equals(a.getPatientId()) &&
                        "Diastolic Trend Alert".equals(a.getCondition()) &&
                        a.getTimestamp() == 3000L
        );
        assertTrue(found, "Expected a Diastolic Trend Alert for patient 10 at timestamp 3000L");
    }

    @Test
    void evaluateData_noAlertAtExactThresholds() {
        Patient bpPatient = new Patient(11);
        bpPatient.addRecord(180.0, "Systolic", 11000L);
        bpPatient.addRecord(120.0, "Diastolic", 12000L);

        Patient spPatient = new Patient(12);
        spPatient.addRecord(92.0, "sp02", 13000L);
        spPatient.addRecord(90.0, "sp02", 15000L); // drop of 2 over >10 min

        alertGenerator.evaluateData(bpPatient);
        alertGenerator.evaluateData(spPatient);

        assertTrue(savedAlerts.stream().noneMatch(a -> a.getPatientId().equals("11")),
                "Expected no alert at exact BP thresholds");
        assertTrue(savedAlerts.stream().noneMatch(a -> a.getPatientId().equals("12")),
                "Expected no rapid-drop alert for small drop over long interval");
    }

    @Test
    void evaluateData_noHypotensiveHypoxemiaWhenOnlyOneCondition() {
        Patient patientBPonly = new Patient(13);
        patientBPonly.addRecord(85.0, "Systolic", 14000L);

        Patient patientSpOnly = new Patient(14);
        patientSpOnly.addRecord(90.0, "sp02", 15000L);

        alertGenerator.evaluateData(patientBPonly);
        alertGenerator.evaluateData(patientSpOnly);

        assertTrue(savedAlerts.stream().noneMatch(a -> a.getPatientId().equals("13") &&
                        "Hypotensive Hypoxemia".equals(a.getCondition())),
                "Expected no combined alert when only BP is low");
        assertTrue(savedAlerts.stream().noneMatch(a -> a.getPatientId().equals("14") &&
                        "Hypotensive Hypoxemia".equals(a.getCondition())),
                "Expected no combined alert when only SpO2 is low");
    }

    @Test
    void evaluateData_noAbnormalECGWithoutPeak() {
        Patient patient = new Patient(15);
        for (int i = 0; i < 10; i++) {
            patient.addRecord(10.0 + i % 2, "ECG", 16000L + i * 1000L);
        }

        alertGenerator.evaluateData(patient);

        assertTrue(savedAlerts.stream().noneMatch(a -> a.getPatientId().equals("15") &&
                        "Abnormal ECG Peak".equals(a.getCondition())),
                "Expected no ECG alert when no peak detected");
    }

    @Test
    void evaluateData_noAlertsWithEmptyRecords() {
        Patient patient = new Patient(16);
        // no records added

        alertGenerator.evaluateData(patient);

        assertTrue(savedAlerts.isEmpty(), "Expected no alerts when patient has no records");
    }
}
