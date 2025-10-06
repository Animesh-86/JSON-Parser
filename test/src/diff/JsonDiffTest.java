package diff;

import core.JsonNull;
import core.JsonNumber;
import core.JsonObject;
import core.JsonString;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonDiffTest {

    @Test
    void testDiffAddReplaceRemove() {
        JsonObject a = new JsonObject();
        a.put("name", new JsonString("Animesh"));
        a.put("age", new JsonNumber("20"));

        JsonObject b = new JsonObject();
        b.put("name", new JsonString("Animesh"));
        b.put("age", new JsonNumber("21"));
        b.put("city", new JsonString("Vadodara"));

        JsonDiff.DiffResult result = JsonDiff.diff(a, b);
        List<JsonDiff.DiffEntry> entries = result.entries();
        assertEquals(2, entries.size());

        assertEquals(JsonDiff.DiffOp.REPLACE, entries.get(0).op);
        assertEquals("/age", entries.get(0).path);
        assertEquals("20", entries.get(0).oldValue.toString());
        assertEquals("21", entries.get(0).newValue.toString());

        assertEquals(JsonDiff.DiffOp.ADD, entries.get(1).op);
        assertEquals("/city", entries.get(1).path);
        assertEquals(JsonNull.INSTANCE, entries.get(1).oldValue);
        assertEquals("Indore", entries.get(1).newValue.toString());
    }
}
