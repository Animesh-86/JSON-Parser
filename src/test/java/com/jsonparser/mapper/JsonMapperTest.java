package com.jsonparser.mapper;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonMapperTest {

    static class User {
        String name;
        int age;
        
        @JsonName("is_active")
        boolean active;
        
        @JsonIgnore
        String hidden;
        
        List<String> skills;
    }

    @Test
    void testMapper() {
        String json = "{\n" +
                "  \"name\": \"Animesh\",\n" +
                "  \"age\": 20,\n" +
                "  \"is_active\": true,\n" +
                "  \"hidden\": \"secret\",\n" +
                "  \"skills\": [\"Java\", \"ML\"]\n" +
                "}";

        JsonMapper mapper = new JsonMapper();
        User user = mapper.readValue(json, User.class);

        assertEquals("Animesh", user.name);
        assertEquals(20, user.age);
        assertTrue(user.active);
        assertNull(user.hidden);
        
        assertNotNull(user.skills);
        assertEquals(2, user.skills.size());
        assertEquals("Java", user.skills.get(0));
        assertEquals("ML", user.skills.get(1));
    }
}
