package com.alerts.alert_decorator;


import com.data_management.DataStorage;

/**
 * Decorator that tags an alert with a priority level.
 * This class extends the AlertDecorator and adds functionality
 * to prepend a priority tag to the alert condition and log the priority
 * when the alert is triggered.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    private final Priority priority;

    /**
     * Constructs a new PriorityAlertDecorator.
     *
     * @param wrapped  the original AlertComponent to decorate
     * @param priority the priority level to associate with this alert
     */
    public PriorityAlertDecorator(AlertComponent wrapped, Priority priority) {
        super(wrapped);
        this.priority = priority;
    }

    /**
     * Returns the condition string for this alert, including the priority level.
     *
     * @return a string representing the alert condition, prefixed with the priority
     */
    @Override
    public String getCondition() {
        return String.format("[%s] %s", priority.name(), super.getCondition());
    }

    /**
     * Triggers the alert, logs the priority level, and delegates
     * the trigger call to the wrapped alert component.
     *
     * @param dataStorage the DataStorage instance providing the data needed to evaluate and trigger the alert
     */
    @Override
    public void trigger(DataStorage dataStorage) {

        System.out.println("Triggering priority: " + priority);
        wrapped.trigger(dataStorage);
    }
}