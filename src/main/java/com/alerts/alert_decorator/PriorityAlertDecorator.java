package com.alerts.alert_decorator;


import com.data_management.DataStorage;

/**
 * Decorator that tags an alert with a priority level.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    private final Priority priority;

    public PriorityAlertDecorator(AlertComponent wrapped, Priority priority) {
        super(wrapped);
        this.priority = priority;
    }

    @Override
    public String getCondition() {
        return String.format("[%s] %s", priority.name(), super.getCondition());
    }

    @Override
    public void trigger(DataStorage dataStorage) {

        System.out.println("Triggering priority: " + priority);
        wrapped.trigger(dataStorage);
    }
}