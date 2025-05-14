package com.alerts.alert_decorator;

import com.data_management.DataStorage;

/**
 * Base decorator: delegates to wrapped AlertComponent.
 */
public abstract class AlertDecorator implements AlertComponent {
    protected final AlertComponent wrapped;

    public AlertDecorator(AlertComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getPatientId() {
        return wrapped.getPatientId();
    }

    @Override
    public String getCondition() {
        return wrapped.getCondition();
    }

    @Override
    public long getTimestamp() {
        return wrapped.getTimestamp();
    }

    /**
     * Triggering an alert: logs, notifies, etc.
     *
     * @param dataStorage data from dataStorage
     */
    @Override
    public void trigger(DataStorage dataStorage) {
        wrapped.trigger(dataStorage);
    }
}