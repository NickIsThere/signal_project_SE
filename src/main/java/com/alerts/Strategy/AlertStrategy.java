package com.alerts.Strategy;

import com.data_management.DataStorage;
import com.data_management.Patient;

public interface AlertStrategy {

    void checkAlert(Patient patient, DataStorage dataStorage);
}
