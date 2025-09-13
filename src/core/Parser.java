package core;

import exception.JsonParseException;

public class Parser {
    private Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }

    private void eat(TokenType type) {
        if (currentToken.type == type) {
            currentToken = lexer.nextToken();
        } else {
//            throw new RuntimeException("Expected " + type + " but found " + currentToken.type);
            throw new JsonParseException(
                    "Expected " + type + " but found " + currentToken.type,
                    currentToken.line,
                    currentToken.column
            );
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

        while (currentToken.type != TokenType.END_OBJECT) {
            // Key must be a STRING
            if (currentToken.type != TokenType.STRING) {
                throw new JsonParseException("Expected STRING as object key",
                        currentToken.line, currentToken.column);
            }
            String key = currentToken.value;
            eat(TokenType.STRING);

            // After key, must be COLON
            if (currentToken.type != TokenType.COLON) {
                throw new JsonParseException("Missing ':' after key \"" + key + "\"",
                        currentToken.line, currentToken.column);
            }
            eat(TokenType.COLON);

            // Parse the value
            JsonValue value = parseValue();
            obj.put(key, value);

            // After value, expect COMMA or END_OBJECT
            if (currentToken.type == TokenType.COMMA) {
                eat(TokenType.COMMA);
            } else if (currentToken.type != TokenType.END_OBJECT) {
                throw new JsonParseException("Expected ',' or '}' after value for key \"" + key + "\"",
                        currentToken.line, currentToken.column);
            }
        }
        eat(TokenType.END_OBJECT);
        return obj;
    }


    private JsonArray parseArray() {
        eat(TokenType.BEGIN_ARRAY);
        JsonArray arr = new JsonArray();

        while (currentToken.type != TokenType.END_ARRAY) {
            JsonValue value = parseValue();
            arr.add(value);

            if (currentToken.type == TokenType.COMMA) {
                eat(TokenType.COMMA);
            } else if (currentToken.type != TokenType.END_ARRAY) {
                throw new JsonParseException("Expected ',' or ']' in array",
                        currentToken.line, currentToken.column);
            }
        }
        eat(TokenType.END_ARRAY);
        return arr;
    }

}
