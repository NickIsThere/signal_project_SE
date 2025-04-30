package com.alerts.alert_decorator;

import com.alerts.Alert;
import com.data_management.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepeatedAlertDecoratorTest {

    static class CountingAlert extends Alert {
        private int triggerCount = 0;

        public CountingAlert() {
            super("test002", "Pulse irregular", System.currentTimeMillis());
        }

        @Override
        public void trigger(DataStorage dataStorage) {
            triggerCount++;
            dataStorage.saveAlertInLog(this);  // ✅ safe, this is a true Alert
        }

        public int getTriggerCount() {
            return triggerCount;
        }
    }

    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clearDataForTesting();
    }

    @Test
    void testTriggerRepeatsCorrectNumberOfTimes() {
        DataStorage storage = DataStorage.getInstance();
        CountingAlert counting = new CountingAlert();
        AlertComponent repeated = new RepeatedAlertDecorator(counting, 3, 0); // no delay

        repeated.trigger(storage);

        assertEquals(3, counting.getTriggerCount());
        assertEquals(3, storage.getAlertLog().size());
        assertEquals("test002", storage.getAlertLog().get(0).getPatientId());
    }
}
