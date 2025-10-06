package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonObjectTest {

    @Test
    void testPutAndGet() {
        JsonObject obj = new JsonObject();
        obj.put("key1", new JsonString("value1"));
        obj.put("key2", new JsonNumber("123"));
        assertEquals("value1", ((JsonString) obj.get("key1")).value);
        assertEquals("123", ((JsonNumber) obj.get("key2")).toString());
    }

    @Test
    void testToJson() {
        JsonObject obj = new JsonObject();
        obj.put("a", new JsonNumber("1"));
        obj.put("b", new JsonBoolean(true));
        String json = obj.toJson(0);
        assertTrue(json.contains("\"a\":1"));
        assertTrue(json.contains("\"b\":true"));
    }
}
