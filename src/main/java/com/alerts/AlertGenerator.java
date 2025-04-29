package com.alerts;

import com.alerts.Strategy.AlertStrategy;
import com.alerts.Strategy.BloodPressureStrategy;
import com.alerts.Strategy.HeartRateStrategy;
import com.alerts.Strategy.OxygenSaturationStrategy;
import com.alerts.alert_factories.AlertFactory;
import com.alerts.alert_factories.BloodOxygenAlertFactory;
import com.alerts.alert_factories.BloodPressureAlertFactory;
import com.alerts.alert_factories.ECGAlertFactory;
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
    private final DataStorage dataStorage;
    private final List<AlertStrategy> alertStrategies;

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
        this.alertStrategies = new ArrayList<>();

        // Add all available alert strategies
        alertStrategies.add(new BloodPressureStrategy());
        alertStrategies.add(new OxygenSaturationStrategy());
        alertStrategies.add(new HeartRateStrategy());
    }

    /**
     * Evaluates the specified patient's data using all alert strategies.
     *
     * @param patient the patient data to evaluate
     */
    public void evaluateData(Patient patient) {
        for (AlertStrategy strategy : alertStrategies) {
            strategy.checkAlert(patient, dataStorage);
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
    void triggerAlert(Alert alert) {
        System.out.println(alert);
        dataStorage.saveAlertInLog(alert);
    }
}
