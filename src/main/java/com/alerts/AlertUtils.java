package com.alerts;



import com.alerts.alert_decorator.PriorityAlertDecorator;
import com.alerts.alert_decorator.AlertComponent;
import com.alerts.alert_decorator.RepeatedAlertDecorator;
import com.data_management.DataStorage;

/**
 * Utility class for performing operations on alerts.
 *
 * Provides helper methods to apply multiple decorators
 * and trigger the alert with the specified configurations.
 */
public class AlertUtils {
    /**
     * Puts together a base alert with a PriorityAlertDecorator and a RepeatedAlertDecorator,
     * then triggers it using the specified DataStorage.
     *
     * @param base the base AlertComponent to decorate
     * @param ds the DataStorage instance used for logging or processing the alert
     * @param prio the PriorityAlertDecorator.  Priority level to apply
     * @param repeats the number of times the alert should be repeated
     * @param interval the interval in milliseconds between each repeat
     */
    public static void fireWithPriority(
            AlertComponent base,
            DataStorage ds,
            com.alerts.alert_decorator.PriorityAlertDecorator.Priority prio,
            int repeats,
            long interval
    ) {
        AlertComponent a = new PriorityAlertDecorator(base, prio);
        a = new RepeatedAlertDecorator(a, repeats, interval);
        a.trigger(ds);
    }
}

