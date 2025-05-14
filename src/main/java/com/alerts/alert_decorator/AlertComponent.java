package com.alerts.alert_decorator;

import com.data_management.DataStorage;

/**
 * Common interface for all alert components.
 */
public interface AlertComponent {
    String getPatientId();
    String getCondition();
    long getTimestamp();

    /**
     * Triggers the alert: logs, notifies, etc.
     *
     * @param dataStorage data from dataStorage
     */
    void trigger(DataStorage dataStorage);
}