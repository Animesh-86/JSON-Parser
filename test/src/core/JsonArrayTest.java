package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonArrayTest {

    @Test
    public void testAddAndGet() {
        JsonArray arr = new JsonArray();
        JsonObject obj = new JsonObject();
        arr.add(obj);

        assertEquals(1, arr.size(), "size should be one after adding one element");
        assertEquals(obj, arr.get(0), "The first element should match the added object");

    }

    @Test
    public void testAddNull() {
        JsonArray arr = new JsonArray();
        arr.add(null);

        assertEquals(1, arr.size());
        assertTrue(arr.get(0) instanceof JsonNull, "Adding null should store JsonNull instance");
    }
}
