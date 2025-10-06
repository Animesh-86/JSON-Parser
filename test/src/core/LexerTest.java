package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {

    @Test
    public void testStringToken() {
        Lexer lexer = new Lexer("\"hello\"");
        Token token = lexer.nextToken();
        assertEquals(TokenType.STRING, token.getType());
        assertEquals("hello", token.getValue());
    }

    @Test
    public void testNumberToken() {
        Lexer lexer = new Lexer("123.45");
        Token token = lexer.nextToken();
        assertEquals(TokenType.NUMBER, token.getType());
        assertEquals("123.45", token.getValue());
    }

    @Test
    public void testBooleanTokenTrue() {
        Lexer lexer = new Lexer("true");
        Token token = lexer.nextToken();
        assertEquals(TokenType.BOOLEAN, token.getType());
        assertEquals("true", token.getValue());
    }

    @Test
    public void testNUllToken() {
        Lexer lexer = new Lexer("null");
        Token token = lexer.nextToken();
        assertEquals(TokenType.NULL, token.getType());
    }
}
