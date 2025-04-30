package com.alerts;



import com.alerts.alert_decorator.PriorityAlertDecorator;
import com.alerts.alert_decorator.AlertComponent;
import com.alerts.alert_decorator.RepeatedAlertDecorator;
import com.data_management.DataStorage;

public class AlertUtils {
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

