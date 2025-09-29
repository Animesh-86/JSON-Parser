package streaming;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StreamingParserTest {

    public static void main(String[] args) {
        try (FileReader fr = new FileReader("src/streaming/sample.json")) {

//            // Create event handler that prints events
//            JsonEventHandler handler = new PrintEventHandler();
//
//            // Create streaming parser
//            JsonStreamParser parser = new JsonStreamParser(fr, handler);

            Set<String> keys = new HashSet<>();
            keys.add("name");
            keys.add("skills");

            JsonEventHandler handler = new PrintEventHandler();
            JsonStreamParser parser = new JsonStreamParser(new FileReader("src/streaming/sample.json"), handler, keys);
            parser.parse();


            // Start parsing
            parser.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
