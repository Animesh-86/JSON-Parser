import core.JsonValue;
import core.Lexer;
import core.Parser;

public class Main {
    public static void main(String[] args) {
        String json = "{\"name\":\"Animesh\",\"age\":21,\"skills\":[\"Java\",\"Spring\"]}";
        Lexer lexer = new Lexer(json, 0);
        Parser parser = new Parser(lexer);

        JsonValue result = parser.parse();

        System.out.println("Parsed JSON successfully: " + result);
    }
}
