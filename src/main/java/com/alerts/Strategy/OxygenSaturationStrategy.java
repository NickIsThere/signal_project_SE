package com.alerts.Strategy;

import com.alerts.Alert;
import com.alerts.AlertUtils;
import com.alerts.alert_factories.AlertFactory;
import com.alerts.alert_factories.BloodOxygenAlertFactory;
import com.alerts.alert_decorator.PriorityAlertDecorator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {

    private final AlertFactory bloodOxygenAlertFactory;

    public OxygenSaturationStrategy() {
        this.bloodOxygenAlertFactory = new BloodOxygenAlertFactory();
    }

    @Override
    public void checkAlert(Patient patient, DataStorage dataStorage) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        String patientId = String.valueOf(patient.getPatientId());

        List<PatientRecord> spo2 = new ArrayList<>();
        for (PatientRecord r : records) {
            if ("SpO2".equalsIgnoreCase(r.getRecordType())) {
                spo2.add(r);
            }
        }
        Comparator<PatientRecord> byTime = Comparator.comparingLong(PatientRecord::getTimestamp);
        spo2.sort(byTime);

        checkLowSpO2(spo2, patientId, dataStorage);
        checkRapidSpO2Drop(spo2, patientId, dataStorage);
    }

    private void checkLowSpO2(List<PatientRecord> spo2, String patientId, DataStorage dataStorage) {
        for (PatientRecord rec : spo2) {
            if (rec.getMeasurementValue() < 92) {
                Alert alert = bloodOxygenAlertFactory.createAlert(patientId, "Low SpO2", rec.getTimestamp());
                dataStorage.saveAlertInLog(alert);
                AlertUtils.fireWithPriority(
                        alert,
                        dataStorage,
                        PriorityAlertDecorator.Priority.HIGH,
                        1,
                        0L
                );
            }
        }
    }

    private void checkRapidSpO2Drop(List<PatientRecord> spo2, String patientId, DataStorage dataStorage) {
        for (int i = 0; i < spo2.size(); i++) {
            PatientRecord current = spo2.get(i);
            for (int j = i + 1; j < spo2.size(); j++) {
                long deltaTime = spo2.get(j).getTimestamp() - current.getTimestamp();
                if (deltaTime > 600_000) break; // 10 minutes
                if (current.getMeasurementValue() - spo2.get(j).getMeasurementValue() >= 5) {
                   Alert alert = bloodOxygenAlertFactory.createAlert(patientId, "Rapid SpO2 Drop", spo2.get(j).getTimestamp());
                   dataStorage.saveAlertInLog(alert);
                    AlertUtils.fireWithPriority(
                            alert,
                            dataStorage,
                            PriorityAlertDecorator.Priority.CRITICAL,
                            5,
                            20_000L
                    );
                }
            }
        }
    }

}
