import core.*;
import exception.JsonParseException;

public class Main {
    public static void main(String[] args) {
        String json = "{\"name\":\"Animesh\",\"age\":21,\"skills\":[\"Java\",\"Spring\"]}";

        Lexer lexer = new Lexer(json);
        Parser parser = new Parser(lexer);

        try {
            JsonValue result = parser.parse();

            System.out.println("=== Parsed JSON ===");

            // Compact mode (default toString)
            System.out.println("\nCompact JSON:");
            System.out.println(result);

            // Pretty mode (4 spaces indent)
            System.out.println("\nPretty JSON:");
            System.out.println(result.toJson(4));

        } catch (JsonParseException e) {
            System.err.println("Parse error: " + e.getMessage());
        }
    }
}
