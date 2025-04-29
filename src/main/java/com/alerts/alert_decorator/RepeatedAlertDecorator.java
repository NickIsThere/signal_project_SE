package com.alerts.alert_decorator;



import com.data_management.DataStorage;

/**
 * Decorator that repeats an alert a specified number of times at a given interval.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    private final int repeatCount;
    private final long intervalMillis;

    /**
     * @param wrapped Underlying alert
     * @param repeatCount Number of times to repeat
     * @param intervalMillis Milliseconds between repeats
     */
    public RepeatedAlertDecorator(AlertComponent wrapped, int repeatCount, long intervalMillis) {
        super(wrapped);
        this.repeatCount = repeatCount;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void trigger(DataStorage dataStorage) {
        for (int i = 0; i < repeatCount; i++) {
            wrapped.trigger(dataStorage);
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
