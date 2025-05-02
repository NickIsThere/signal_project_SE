package com.alerts.alert_factories;

import com.alerts.Alert;

/**
 * Abstract class for factory design pattern implementation
 */
public abstract class AlertFactory {
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}
