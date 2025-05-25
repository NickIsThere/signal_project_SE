package data_management;


import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileDataReaderTest {

    private final DataStorage storage = DataStorage.getInstance();

    @BeforeEach
    void resetStorage() {
        storage.clearDataForTesting();
    }

    @AfterEach
    void tearDown() {
        storage.clearDataForTesting();
    }

    @Test
    void readDataParsesValidLines() throws Exception {
        // prepare temp file
        File temp = File.createTempFile("data", ".csv");
        try (FileWriter w = new FileWriter(temp)) {
            w.write("1, 23.5, Temp, 1000\n");
            w.write("2, 45.0, HR, 2000\n");
        }
        String[] args = new String[] { "--output file:" + temp.getAbsolutePath() };
        FileDataReader reader = new FileDataReader(args);

        reader.readData(storage);

        List<PatientRecord> recs1 = storage.getRecords(1,0,5000);
        List<PatientRecord> recs2 = storage.getRecords(2,0,5000);
        assertEquals(1, recs1.size());
        assertEquals(1, recs2.size());
    }

    @Test
    void readDataThrowsOnMissingArgs() {
        FileDataReader reader = new FileDataReader(new String[0]);
        assertThrows(IllegalArgumentException.class, () -> reader.readData(storage));
    }
}