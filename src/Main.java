import core.*;
import exception.JsonParseException;

public class Main {
    public static void main(String[] args) {
        // ❌ Invalid JSON (missing colon after "name")
        // String json = "{\"name\"\"Animesh\",\"age\":21,\"skills\":[\"Java\",\"Spring\"]}";

        // ✅ Valid JSON
        String json = "{\"name\":\"Animesh\",\"age\":21,\"skills\":[\"Java\",\"Spring\"]}";

        try {
            Lexer lexer = new Lexer(json, 0);
            Parser parser = new Parser(lexer);

            JsonValue result = parser.parse();

            System.out.println("Parsed JSON:");
            System.out.println(result);

        } catch (JsonParseException e) {
            // Custom parse error with line/column
            System.err.println("Parse error: " + e.getMessage());
        } catch (Exception e) {
            // Catch-all for unexpected runtime issues
            e.printStackTrace();
        }
    }
}
