package com.alerts.Strategy;

import com.alerts.Alert;
import com.alerts.AlertUtils;
import com.alerts.alert_decorator.AlertComponent;
import com.alerts.alert_factories.AlertFactory;
import com.alerts.alert_factories.BloodPressureAlertFactory;
import com.alerts.decorator.PriorityAlertDecorator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {

    private final AlertFactory bloodPressureAlertFactory;

    public BloodPressureStrategy() {
        this.bloodPressureAlertFactory = new BloodPressureAlertFactory();
    }
    @Override
    public void checkAlert(Patient patient, DataStorage dataStorage) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        String patientId = String.valueOf(patient.getPatientId());

        List<PatientRecord> systolic = new ArrayList<>();
        List<PatientRecord> diastolic = new ArrayList<>();
        List<PatientRecord> spo2 = new ArrayList<>();

        // Categorize records
        for (PatientRecord r : records) {
            switch (r.getRecordType()) {
                case "Systolic":
                    systolic.add(r);
                    break;
                case "Diastolic":
                    diastolic.add(r);
                    break;
                case "sp02":
                    spo2.add(r);
                    break;
            }
        }

        Comparator<PatientRecord> byTime = Comparator.comparingLong(PatientRecord::getTimestamp);
        systolic.sort(byTime);
        diastolic.sort(byTime);
        spo2.sort(byTime);

        // Check critical thresholds
        checkCriticalThresholds(systolic, diastolic, patientId, dataStorage);

        // Detect trends
        detectTrend(systolic, patientId, "Systolic Trend", dataStorage);
        detectTrend(diastolic, patientId, "Diastolic Trend", dataStorage);

        // Hypotensive Hypoxemia check
        checkHypotensiveHypoxemia(systolic, spo2, patientId, dataStorage);
    }

    private void checkCriticalThresholds(List<PatientRecord> systolic,
                                         List<PatientRecord> diastolic,
                                         String patientId,
                                         DataStorage dataStorage) {
        for (PatientRecord s : systolic) {
            double val = s.getMeasurementValue();
            if (val >= 180 || val <= 90) {
                Alert alert = bloodPressureAlertFactory.createAlert(
                        patientId,
                        "Critical Systolic BP",
                        s.getTimestamp());
                dataStorage.saveAlertInLog(alert);
                AlertUtils.fireWithPriority(
                        alert,
                        dataStorage,
                        PriorityAlertDecorator.Priority.HIGH,
                        2,
                        30_000L
                );

            }
        }
        for (PatientRecord d : diastolic) {
            double val = d.getMeasurementValue();
            if (val >= 120 || val <= 60) {
                Alert alert = bloodPressureAlertFactory.createAlert(
                        patientId,
                        "Critical Diastolic BP",
                        d.getTimestamp());
                dataStorage.saveAlertInLog(alert);
                AlertUtils.fireWithPriority(
                        alert,
                        dataStorage,
                        PriorityAlertDecorator.Priority.HIGH,
                        2,
                        30_000L
                );

            }
        }
    }


    private void detectTrend(List<PatientRecord> records, String patientId, String label, DataStorage dataStorage) {
        for (int i = 0; i < records.size() - 2; i++) {
            double v1 = records.get(i).getMeasurementValue();
            double v2 = records.get(i + 1).getMeasurementValue();
            double v3 = records.get(i + 2).getMeasurementValue();
            if ((v2 - v1 > 10 && v3 - v2 > 10) || (v1 - v2 > 10 && v2 - v3 > 10)) {
                Alert alert = bloodPressureAlertFactory.createAlert(patientId, label + " Alert", records.get(i + 2).getTimestamp());
                dataStorage.saveAlertInLog(alert);
                AlertUtils.fireWithPriority(
                        alert,
                        dataStorage,
                        PriorityAlertDecorator.Priority.MEDIUM,
                        2,
                        30_000L
                );
            }
        }
    }

    private void checkHypotensiveHypoxemia(List<PatientRecord> systolic, List<PatientRecord> spo2, String patientId, DataStorage dataStorage) {
        for (PatientRecord s : systolic) {
            if (s.getMeasurementValue() >= 90) continue;
            for (PatientRecord o : spo2) {
                if (Math.abs(s.getTimestamp() - o.getTimestamp()) <= 300_000 && o.getMeasurementValue() < 92) {
                    Alert alert = bloodPressureAlertFactory.createAlert(patientId, "Hypotensive Hypoxemia", Math.max(s.getTimestamp(), o.getTimestamp()));
                    dataStorage.saveAlertInLog(alert);
                    AlertUtils.fireWithPriority(
                            alert,
                            dataStorage,
                            PriorityAlertDecorator.Priority.CRITICAL,
                            8,
                            20_000L
                    );
                }
            }
        }
    }


}
