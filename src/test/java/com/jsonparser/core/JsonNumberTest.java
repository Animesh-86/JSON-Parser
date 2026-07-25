package com.jsonparser.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonNumberTest {

    @Test
    void testEquality() {
        JsonNumber n1 = new JsonNumber("123.00");
        JsonNumber n2 = new JsonNumber("123");
        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    void testValues() {
        JsonNumber n = new JsonNumber("-45.67");
        assertEquals("-45.67", n.getValue());
        assertEquals(-45, n.intValue());
        assertEquals(-45.67, n.doubleValue());
    }
}
