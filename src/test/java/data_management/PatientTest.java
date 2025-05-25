package data_management;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient(7);
    }

    @Test
    void addRecordThenRetrieveWithinRange() {
        patient.addRecord(120.0, "BloodPressure", 1_000L);
        patient.addRecord(80.0, "HeartRate", 2_000L);
        List<PatientRecord> records = patient.getRecords(500L, 1_500L);
        assertEquals(1, records.size());
        assertEquals("BloodPressure", records.get(0).getRecordType());
    }

    @Test
    void addOrUpdateRecordUpdatesExisting() {
        long ts = 5_000L;
        patient.addRecord(60.0, "HeartRate", ts);
        patient.addOrUpdateRecord(65.0, "HeartRate", ts);
        List<PatientRecord> records = patient.getRecords(0L, 10_000L);
        assertEquals(1, records.size());
        assertEquals(65.0, records.get(0).getMeasurementValue());
    }

    @Test
    void getPatientIdReturnsConstructorValue() {
        assertEquals(7, patient.getPatientId());
    }
}
