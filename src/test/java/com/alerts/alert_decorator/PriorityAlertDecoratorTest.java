package com.alerts.alert_decorator;

import com.alerts.Alert;
import com.data_management.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityAlertDecoratorTest {

    static class DummyAlert extends Alert {
        public DummyAlert() {
            super("patient123", "Low oxygen", System.currentTimeMillis());
        }

        @Override
        public void trigger(DataStorage dataStorage) {
            System.out.println("DummyAlert triggered");
            super.trigger(dataStorage); // calls saveAlertInLog
        }
    }

    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clearDataForTesting();
    }

    @Test
    void testPriorityPrefixInCondition() {
        AlertComponent base = new DummyAlert();
        PriorityAlertDecorator decorated = new PriorityAlertDecorator(base, PriorityAlertDecorator.Priority.CRITICAL);

        String result = decorated.getCondition();
        assertTrue(result.startsWith("[CRITICAL]"));
        assertTrue(result.contains("Low oxygen"));
    }

    @Test
    void testTriggerDelegatesAndLogs() {
        DataStorage storage = DataStorage.getInstance();
        AlertComponent decorated = new PriorityAlertDecorator(new DummyAlert(), PriorityAlertDecorator.Priority.MEDIUM);

        decorated.trigger(storage);

        assertEquals(1, storage.getAlertLog().size());
        assertEquals("patient123", storage.getAlertLog().get(0).getPatientId());
    }
}
