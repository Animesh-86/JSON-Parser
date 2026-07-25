package com.jsonparser.index;

import com.jsonparser.core.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonIndexerTest {

    @Test
    void testIndexAndGet() {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString("Animesh"));
        
        JsonArray arr = new JsonArray();
        arr.add(new JsonNumber("1"));
        arr.add(new JsonNumber("2"));
        obj.put("list", arr);

        JsonIndexer indexer = new JsonIndexer(obj);
        
        assertTrue(indexer.contains("$.name"));
        assertTrue(indexer.contains("$.list[0]"));
        assertFalse(indexer.contains("$.missing"));

        assertEquals("Animesh", ((JsonString) indexer.get("$.name")).getValue());
        assertEquals("1", ((JsonNumber) indexer.get("$.list[0]")).toString());
        
        assertEquals(5, indexer.size()); // root, name, list, list[0], list[1]
    }
}
