package com.jsonparser.streaming;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZeroAllocStreamTest {

    @Test
    void testZeroAllocParsing() throws Exception {
        String json = "{\"key\": \"value\", \"num\": 123}";
        List<String> values = new ArrayList<>();

        try (ZeroAllocStreamParser parser = new ZeroAllocStreamParser(new StringReader(json), (type, buf, start, len) -> {
            if (type == JsonEventType.VALUE) {
                values.add(new String(buf, start, len));
            }
        })) {
            parser.parse();
        }

        assertEquals(4, values.size());
        assertEquals("key", values.get(0));
        assertEquals("value", values.get(1));
        assertEquals("num", values.get(2));
        assertEquals("123", values.get(3));
    }
}
