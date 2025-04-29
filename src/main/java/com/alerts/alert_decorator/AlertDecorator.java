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

    @Override
    public void trigger(DataStorage dataStorage) {
        wrapped.trigger(dataStorage);
    }
}