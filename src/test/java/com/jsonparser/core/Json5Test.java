package com.jsonparser.core;

import com.jsonparser.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Json5Test {

    @Test
    void testStrictRejectsJson5() {
        String json = "{ // comment\n \"key\": 1 }";
        Parser parser = new Parser(json);
        assertThrows(JsonParseException.class, parser::parse);
    }

    @Test
    void testJson5AcceptsComments() {
        String json = "{ /* multi\nline */ \"key\": 1, // single line \n \"key2\": 2 }";
        Parser parser = new Parser(json, ParserConfig.json5());
        JsonObject obj = (JsonObject) parser.parse();
        assertEquals("1", ((JsonNumber) obj.get("key")).toString());
        assertEquals("2", ((JsonNumber) obj.get("key2")).toString());
    }

    @Test
    void testJson5AcceptsSingleQuotes() {
        String json = "{ 'key': 'value' }";
        Parser parser = new Parser(json, ParserConfig.json5());
        JsonObject obj = (JsonObject) parser.parse();
        assertEquals("value", ((JsonString) obj.get("key")).getValue());
    }

    @Test
    void testJson5AcceptsUnquotedKeys() {
        String json = "{ myKey_123: \"value\" }";
        Parser parser = new Parser(json, ParserConfig.json5());
        JsonObject obj = (JsonObject) parser.parse();
        assertEquals("value", ((JsonString) obj.get("myKey_123")).getValue());
    }

    @Test
    void testJson5AcceptsNumbers() {
        String json = "{ \"a\": +1, \"b\": Infinity, \"c\": -Infinity, \"d\": NaN, \"e\": 0x1A }";
        Parser parser = new Parser(json, ParserConfig.json5());
        JsonObject obj = (JsonObject) parser.parse();
        assertEquals("1", ((JsonNumber) obj.get("a")).toString());
        assertEquals("Infinity", ((JsonNumber) obj.get("b")).toString());
        assertEquals("-Infinity", ((JsonNumber) obj.get("c")).toString());
        assertEquals("NaN", ((JsonNumber) obj.get("d")).toString());
        assertEquals("0x1A", ((JsonNumber) obj.get("e")).toString());
    }
}
