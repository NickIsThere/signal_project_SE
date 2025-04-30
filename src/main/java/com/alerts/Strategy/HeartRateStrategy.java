package com.alerts.Strategy;

import com.alerts.Alert;
import com.alerts.AlertUtils;
import com.alerts.alert_factories.AlertFactory;
import com.alerts.alert_factories.ECGAlertFactory;
import com.alerts.alert_decorator.PriorityAlertDecorator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {

    private final int window = 5;
    private final AlertFactory heartRateAlertFactory;
    private List<PatientRecord> ecg;
    private String patientId;

    public HeartRateStrategy() {
        this.heartRateAlertFactory = new ECGAlertFactory();
        this.ecg = new ArrayList<>();

    }
    @Override
    public void checkAlert(Patient patient, DataStorage dataStorage) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        for (PatientRecord r : records) {
            if (r.getRecordType() == "ECG"){
                this.ecg.add(r);
            }
        }
        Comparator<PatientRecord> byTime = Comparator.comparingLong(PatientRecord::getTimestamp);
        this.ecg.sort(byTime);
        patientId = String.valueOf(patient.getPatientId());
        ECG_window(dataStorage);
    }

    public void ECG_window(DataStorage dataStorage){

        for (int i = window; i < ecg.size(); i++) {
            double sum = 0;
            for (int j = i - window; j < i; j++) {
                sum += ecg.get(j).getMeasurementValue();
            }
            double avg = sum / window;
            double currentVal = ecg.get(i).getMeasurementValue();
            if (currentVal > avg * 1.5) {
                Alert alert = heartRateAlertFactory.createAlert(patientId,  "Abnormal ECG Peak", System.currentTimeMillis());
                dataStorage.saveAlertInLog(alert);
                AlertUtils.fireWithPriority(
                        alert,
                        dataStorage,
                        PriorityAlertDecorator.Priority.MEDIUM,
                        3,
                        15_000L
                );

            }
        }
    }

}
