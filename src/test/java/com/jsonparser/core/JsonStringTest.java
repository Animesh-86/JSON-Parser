package com.jsonparser.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStringTest {

    @Test
    void testEquality() {
        JsonString s1 = new JsonString("hello");
        JsonString s2 = new JsonString("hello");
        JsonString s3 = new JsonString("world");
        
        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testToJson() {
        JsonString s = new JsonString("a \"quote\"");
        assertEquals("\"a \\\"quote\\\"\"", s.toJson(0));
    }
}
