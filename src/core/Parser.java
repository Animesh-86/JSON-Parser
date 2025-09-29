package core;

import exception.JsonParseException;

public class Parser {
    private Lexer lexer;
    private Token currentToken;

    public Parser(String input) {
        this.lexer = new Lexer(input);   // internally create the lexer
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
            case BEGIN_OBJECT:
                return parseObject(); // returns JsonObject
            case BEGIN_ARRAY:
                return parseArray(); // returns JsonArray
            case STRING: {
                String value = currentToken.getValue();
                eat(TokenType.STRING);
                return new JsonString(value); // returns JsonString
            }
            case NUMBER: {
                String num = currentToken.getValue();
                eat(TokenType.NUMBER);
                return new JsonNumber(num); // returns JsonNumber
            }
            case BOOLEAN: {
                String bool = currentToken.getValue();
                eat(TokenType.BOOLEAN);
                return new JsonBoolean(Boolean.parseBoolean(bool)); // returns JsonBoolean
            }
            case NULL:
                eat(TokenType.NULL);
                return new JsonNull(); // returns JsonNull
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

            JsonValue value = parseValue(); // recursively parse value
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
            JsonValue value = parseValue(); // recursively parse value
            arr.add(value);

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
