package query;

import core.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonQueryTest {

    @Test
    void testQueryNestedObject() {
        JsonObject obj = new JsonObject();
        JsonObject person = new JsonObject();
        person.put("name", new JsonString("Animesh"));
        person.put("age", new JsonNumber("20"));
        obj.put("person", person);

        JsonQuery query = new JsonQuery(obj);
        assertEquals("Animesh", ((JsonString) query.query("$.person.name")).value);
        assertEquals("20", ((JsonNumber) query.query("$.person.age")).toString());
    }

    @Test
    void testQueryArrayAccess() {
        JsonObject obj = new JsonObject();
        JsonArray skills = new JsonArray();
        skills.add(new JsonString("Java"));
        skills.add(new JsonString("ML"));
        obj.put("Skills", skills);

        JsonQuery query = new JsonQuery(obj);
        assertEquals("Java", ((JsonString) query.query("$.skills[0]")).value);
        assertEquals("ML", ((JsonString) query.query("$.skills[1]")).value);
    }
}
