// src/test/java/com/data_management/PatientRecordTest.java
package data_management;

import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientRecordTest {

    @Test
    void gettersReturnConstructorValues() {
        PatientRecord record = new PatientRecord(42, 98.6, "ECG", 1622548800000L);
        assertEquals(42, record.getPatientId());
        assertEquals(98.6, record.getMeasurementValue());
        assertEquals("ECG", record.getRecordType());
        assertEquals(1622548800000L, record.getTimestamp());
    }

    @Test
    void setMeasurementValueUpdatesValue() {
        PatientRecord record = new PatientRecord(1, 50.0, "BP", 1000L);
        record.setMeasurementValue(75.5);
        assertEquals(75.5, record.getMeasurementValue());
    }
}
