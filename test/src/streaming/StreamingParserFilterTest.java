package streaming;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamingParserFilterTest {

    @Test
    void testJsonEventsWithFilter() throws Exception{
        String json = "{\"name\":\"Animesh\",\"age\":20,\"skills\":[\"Java\",\"ML\"]}";
        List<JsonEvent> events = new ArrayList<>();

        // Only interested in "name" and "skills"
        Set<String> filterKeys = new HashSet<>();
        filterKeys.add("name");
        filterKeys.add("skills");

        JsonEventHandler handler = events::add; // store all events
        JsonStreamParser parser = new JsonStreamParser(new StringReader(json), handler, filterKeys);
        parser.parse();

        // Check that start/end object events are still emitted
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.START_OBJECT));
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.END_OBJECT));

        // Check that only filtered keys appear
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.KEY && e.getValue().equals("name")));
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.KEY && e.getValue().equals("skills")));
        assertFalse(events.stream().anyMatch(e -> e.getType() == JsonEventType.KEY && e.getValue().equals("age")));

        // Check that values for filtered keys are present
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.VALUE && e.getValue().equals("Animesh")));
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.VALUE && e.getValue().equals("Java")));
        assertTrue(events.stream().anyMatch(e -> e.getType() == JsonEventType.VALUE && e.getValue().equals("ML")));

        // Ensure age value is NOT included
        assertFalse(events.stream().anyMatch(e -> e.getType() == JsonEventType.VALUE && e.getValue().equals("20")));
    }
}
