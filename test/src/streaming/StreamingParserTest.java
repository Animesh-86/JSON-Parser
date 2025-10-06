package streaming;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamingParserTest {

    @Test
    void testSimpleJsonEvents() throws Exception{
        String json = "{\"name\":\"Animesh\",\"skills\":[\"Java\",\"ML\"]}";
        List<JsonEvent> events = new ArrayList<>();

        JsonEventHandler handler = events::add; // store all events
        JsonStreamParser parser = new JsonStreamParser(new StringReader(json), handler, null);
        parser.parse();

        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.START_OBJECT));
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.END_OBJECT));

        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.KEY && e.getValue().equals("name")));
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.KEY && e.getValue().equals("skills")));

        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.VALUE && e.getValue().equals("Animesh")));
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.VALUE && e.getValue().equals("Java")));
    }
}
