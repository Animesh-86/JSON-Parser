package core;

import exception.JsonParseException;

public class Parser {
    private Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken(); // initialize
    }

    private void eat(TokenType expected) {
        if (currentToken.getType() == expected) {
            currentToken = lexer.nextToken();
        } else {
            throw error("Expected " + expected + " but found " + currentToken.getType());
        }
    }

    private JsonParseException error(String message) {
        return new JsonParseException(
                message + " at line " + currentToken.getLine() + ", column " + currentToken.getColumn()
        );
    }

    public JsonValue parse() {
        return parseValue();
    }

    private JsonValue parseValue() {
        switch (currentToken.getType()) {
            case BEGIN_OBJECT: return parseObject();
            case BEGIN_ARRAY:  return parseArray();
            case STRING: {
                String value = currentToken.getValue();
                eat(TokenType.STRING);
                return new JsonString(value);
            }
            case NUMBER: {
                String num = currentToken.getValue();
                eat(TokenType.NUMBER);
                return new JsonNumber(num);
            }
            case BOOLEAN: {
                String bool = currentToken.getValue();
                eat(TokenType.BOOLEAN);
                return new JsonBoolean(Boolean.parseBoolean(bool));
            }
            case NULL: {
                eat(TokenType.NULL);
                return new JsonNull();
            }
            default:
                throw error("Unexpected token " + currentToken.getType());
        }
    }

    private JsonObject parseObject() {
        JsonObject obj = new JsonObject();
        eat(TokenType.BEGIN_OBJECT);

        while (currentToken.getType() != TokenType.END_OBJECT) {
            if (currentToken.getType() != TokenType.STRING) {
                throw error("Expected STRING key in object");
            }
            String key = currentToken.getValue();
            eat(TokenType.STRING);

            if (currentToken.getType() != TokenType.COLON) {
                throw error("Expected COLON after key");
            }
            eat(TokenType.COLON);

            JsonValue value = parseValue();
            obj.put(key, value);

            if (currentToken.getType() == TokenType.COMMA) {
                eat(TokenType.COMMA);
            } else if (currentToken.getType() != TokenType.END_OBJECT) {
                throw error("Expected COMMA or END_OBJECT");
            }
        }

        eat(TokenType.END_OBJECT);
        return obj;
    }

    private JsonArray parseArray() {
        JsonArray arr = new JsonArray();
        eat(TokenType.BEGIN_ARRAY);

        while (currentToken.getType() != TokenType.END_ARRAY) {
            arr.add(parseValue());

            if (currentToken.getType() == TokenType.COMMA) {
                eat(TokenType.COMMA);
            } else if (currentToken.getType() != TokenType.END_ARRAY) {
                throw error("Expected COMMA or END_ARRAY");
            }
        }

        eat(TokenType.END_ARRAY);
        return arr;
    }
}
