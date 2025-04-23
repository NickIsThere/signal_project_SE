package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {

        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        String patientId = String.valueOf(patient.getPatientId());

        // sorting for ease of use later
        List<PatientRecord> systolic = new ArrayList<>();
        List<PatientRecord> diastolic = new ArrayList<>();
        List<PatientRecord> spo2 = new ArrayList<>();
        List<PatientRecord> ecg = new ArrayList<>();

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
                case "ECG":
                    ecg.add(r);
                    break;
            }
        }
        Comparator<PatientRecord> byTime = Comparator.comparingLong(PatientRecord::getTimestamp);
        systolic.sort(byTime);
        diastolic.sort(byTime);
        spo2.sort(byTime);
        ecg.sort(byTime);


        // Bloodpressue maxima and minima
        for (PatientRecord s : systolic) {
            double val = s.getMeasurementValue();
            if (val > 180 || val < 90) {
                triggerAlert(new Alert(patientId, "Critical Systolic BP", s.getTimestamp()));
            }
        }
        for (PatientRecord d : diastolic) {
            double val = d.getMeasurementValue();
            if (val > 120 || val < 60) {
                triggerAlert(new Alert(patientId, "Critical Diastolic BP", d.getTimestamp()));
            }
        }

        // seeing if there is a trend in the bloodpressure
        detectTrend(systolic, patientId, "Systolic Trend");
        detectTrend(diastolic, patientId, "Diastolic Trend");

        // acute oxygen sat alerts
        for (int i = 0; i < spo2.size(); i++) {
            PatientRecord current = spo2.get(i);
            double val = current.getMeasurementValue();
            if (spo2.size() == 1 && val < 92) {
                triggerAlert(new Alert(patientId, "Low SpO2", current.getTimestamp()));
            }
            for (int j = i + 1; j < spo2.size(); j++) {
                long deltaTime = spo2.get(j).getTimestamp() - current.getTimestamp();
                if (deltaTime > 600_000) break; // 10 minutes in milliseconds
                if (current.getMeasurementValue() - spo2.get(j).getMeasurementValue() >= 5) {
                    triggerAlert(new Alert(patientId, "Rapid SpO2 Drop", spo2.get(j).getTimestamp()));
                }
            }
        }
        //Hypotensive Hypoxemia check
        for (PatientRecord s : systolic) {
            if (s.getMeasurementValue() >= 90) continue;
            for (PatientRecord o : spo2) {
                if (Math.abs(s.getTimestamp() - o.getTimestamp()) <= 300_000 && o.getMeasurementValue() < 92) {
                    triggerAlert(new Alert(patientId, "Hypotensive Hypoxemia", Math.max(s.getTimestamp(), o.getTimestamp())));
                }
            }
        }

        // ECG average
        final int window = 5;
        for (int i = window; i < ecg.size(); i++) {
            double sum = 0;
            for (int j = i - window; j < i; j++) {
                sum += ecg.get(j).getMeasurementValue();
            }
            double avg = sum / window;
            double currentVal = ecg.get(i).getMeasurementValue();
            if (currentVal > avg * 1.5) {
                triggerAlert(new Alert(patientId, "Abnormal ECG Peak", ecg.get(i).getTimestamp()));
            }
        }
    }

    /**
     * finds trends in the list over 3 timestamps
     *
     * @param records patientrecords to have access to measurements
     * @param patientId the identification num of the patient in a string format in complience with the alert class
     * @param label a description of the trend e.g systolic
     */
    private void detectTrend(List<PatientRecord> records, String patientId, String label) {
        for (int i = 0; i < records.size() - 2; i++) {
            double v1 = records.get(i).getMeasurementValue();
            double v2 = records.get(i + 1).getMeasurementValue();
            double v3 = records.get(i + 2).getMeasurementValue();
            if ((v2 - v1 > 10 && v3 - v2 > 10) || (v1 - v2 > 10 && v2 - v3 > 10)) {
                triggerAlert(new Alert(patientId, label + " Alert", records.get(i + 2).getTimestamp()));
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        System.out.println(alert);
        dataStorage.saveAlertInLog(alert);
    }
}
