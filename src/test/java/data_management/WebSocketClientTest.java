package data_management;

import com.data_management.DataStorage;
import com.data_management.WebSocketClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class WebSocketClientTest {

    @AfterEach
    void tearDown() {
        DataStorage.getInstance().clearDataForTesting();
    }

    @Test
    void onMessageParsesAndStoresData() throws Exception {
        WebSocketClient client = new WebSocketClient("ws://localhost:8080");
        // simulate incoming CSV message: id,timestamp,label,value
        client.onMessage("3,10000,HeartRate,72.5");
        assertFalse(DataStorage.getInstance().getRecords(3, 0, 20000).isEmpty());
    }

    @Test
    void onMessageIgnoresAlertLabel() throws Exception {
        WebSocketClient client = new WebSocketClient("ws://localhost:8080");
        client.onMessage("4,20000,Alert,ignored");
        assertTrue(DataStorage.getInstance().getRecords(4,0,30000).isEmpty());
    }
}
