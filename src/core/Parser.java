package core;

public class Parser {
    private Lexer lexer;
    private Token currentToken;

    Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }

    private void eat(TokenType type) {
        if (currentToken.type == type) {
            currentToken = lexer.nextToken();
        } else {
            throw new RuntimeException("Expected " + type + " but found " + currentToken.type);
        }
    }

    public JsonValue parse() {
        return parseValue();
    }

    private JsonValue parseValue() {
        switch (currentToken.type) {
            case BEGIN_OBJECT:
                return parseObject();
            case BEGIN_ARRAY:
                return parseArray();
            case STRING:
                String s = currentToken.value;
                eat(TokenType.STRING);
                return new JsonString(s);
            case NUMBER:
                long n = Long.parseLong(currentToken.value);
                eat(TokenType.NUMBER);
                return new JsonNumber(n);
            case BOOLEAN:
                boolean b = Boolean.parseBoolean(currentToken.value);
                eat(TokenType.BOOLEAN);
                return new JsonBoolean(b);
            case NULL:
                eat(TokenType.NULL);
                return new JsonNull();
            case EOF:
                return null;
            default:
                throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

    private JsonObject parseObject() {
        eat(TokenType.BEGIN_OBJECT);
        JsonObject obj = new JsonObject();

        if (currentToken.type != TokenType.END_OBJECT) {
            do {
                String key = currentToken.value;
                eat(TokenType.STRING);
                eat(TokenType.COLON);
                JsonValue value = parseValue();
                obj.map.put(key, value);

                if (currentToken.type == TokenType.COMMA) {
                    eat(TokenType.COMMA);
                } else {
                    break;
                }
            } while (true);
        }

        eat(TokenType.END_OBJECT);
        return obj;
    }

    private JsonArray parseArray() {
        eat(TokenType.BEGIN_ARRAY);
        JsonArray arr = new JsonArray();

        if (currentToken.type != TokenType.END_ARRAY) {
            do {
                arr.list.add(parseValue());
                if (currentToken.type == TokenType.COMMA) {
                    eat(TokenType.COMMA);
                } else {
                    break;
                }
            } while (true);
        }

        eat(TokenType.END_ARRAY);
        return arr;
    }
}
