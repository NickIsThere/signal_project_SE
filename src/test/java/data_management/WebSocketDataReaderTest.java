package data_management;

import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;


import static org.junit.jupiter.api.Assertions.*;

class WebSocketDataReaderTest {

    @Test
    void readDataThrowsUnsupported() {
        WebSocketDataReader reader = new WebSocketDataReader();
        assertThrows(UnsupportedOperationException.class, () -> reader.readData(DataStorage.getInstance()));
    }

    @Test
    void closeWithoutConnectionDoesNotThrow() throws IOException {
        WebSocketDataReader reader = new WebSocketDataReader();
        reader.close();
    }
}
