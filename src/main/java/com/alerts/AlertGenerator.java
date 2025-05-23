package com.alerts;

import com.alerts.Strategy.AlertStrategy;
import com.alerts.Strategy.BloodPressureStrategy;
import com.alerts.Strategy.HeartRateStrategy;
import com.alerts.Strategy.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * The AlertGenerator class is responsible for monitoring patient data
 * and generating alerts when some predefined conditions are met. This class
 * relies on a DataStorage instance to access patient data and evaluates it
 * based on specific criteria.
 */
public class AlertGenerator {
    private final DataStorage dataStorage;
    private final List<AlertStrategy> alertStrategies;

    /**
     * Constructs an AlertGenerator with a specified DataStorage.
     * The DataStorage is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient's data
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
