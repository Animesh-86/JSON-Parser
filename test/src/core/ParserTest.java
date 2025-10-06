package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    void testParserSimpleObject() {
        String json = "{\"name\":\"Animesh\",\"age\":20}";
        Parser parser = new Parser(json);
        JsonValue result = parser.parse();
        assertTrue(result instanceof JsonObject);
        JsonObject obj = (JsonObject) result;
        assertEquals("Animesh", ((JsonString) obj.get("name")).value);
        assertEquals("20", ((JsonNumber) obj.get("age")).toString());
    }

    @Test
    void testParseNestedObject() {
        String json = "{\"person\":{\"name\":\"Animesh\",\"active\":true}}";
        Parser parser = new Parser(json);
        JsonObject obj = (JsonObject) parser.parse();
        JsonObject person = (JsonObject) obj.get("person");
        assertEquals("Animesh", ((JsonString) person.get("name")).value);
        assertEquals("true", ((JsonBoolean) person.get("active")).toJson(0, 0));
    }

    @Test
    void testParseArray() {
        String json = "[1,2,3]";
        Parser parser = new Parser(json);
        JsonArray arr = (JsonArray) parser.parse();
        assertEquals(3, arr.size());
        assertEquals("1", ((JsonNumber) arr.get(0)).toString());
    }

    @Test
    void testInvalidJsonThrows() {
        String json = "{\"name\":\"Animesh\"";
        Parser parser = new Parser(json);
        assertThrows(RuntimeException.class, parser::parse);
    }
}
