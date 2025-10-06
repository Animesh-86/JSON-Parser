package schema;

import core.JsonNumber;
import core.JsonObject;
import core.JsonString;
import exception.JsonParseException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonValidatorTest {

    @Test
    void testValidatorJsonObject() {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString("Animesh"));
        obj.put("age", new JsonNumber("20"));

        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "string");
        schema.put("age", "number");

        assertDoesNotThrow(() -> JsonValidator.validate(obj, schema));
    }

    @Test
    void testInvalidJsonThrows() {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonNumber("123"));

        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "string");

        assertThrows(JsonParseException.class, () -> JsonValidator.validate(obj, schema));
    }
}
