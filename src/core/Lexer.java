package core;

public class Lexer {
    private String input;
    private int pos = 0;
    private int line = 1;
    private int column = 0;

    // Existing constructor
    public Lexer(String input, int pos) {
        this.input = input;
        this.pos = pos;
    }

    // New convenience constructor
    public Lexer(String input) {
        this(input, 0); // delegates to the two-argument constructor
    }

    private char currentChar() {
        return pos < input.length() ? input.charAt(pos) : '\0';
    }

    private void advance() {
        if (currentChar() == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }
        pos++;
    }

    private void skipWhiteSpace() {
        while (Character.isWhitespace(currentChar())) advance();
    }

    public Token nextToken() {
        skipWhiteSpace();
        char c = currentChar();
        int tokenLine = line;
        int tokenColumn = column;

        switch (c) {
            case '{': advance(); return new Token(TokenType.BEGIN_OBJECT, null, tokenLine, tokenColumn);
            case '}': advance(); return new Token(TokenType.END_OBJECT, null, tokenLine, tokenColumn);
            case '[': advance(); return new Token(TokenType.BEGIN_ARRAY, null, tokenLine, tokenColumn);
            case ']': advance(); return new Token(TokenType.END_ARRAY, null, tokenLine, tokenColumn);
            case ':': advance(); return new Token(TokenType.COLON, null, tokenLine, tokenColumn);
            case ',': advance(); return new Token(TokenType.COMMA, null, tokenLine, tokenColumn);
            case '"': return stringToken();
            case '\0': return new Token(TokenType.EOF, null, tokenLine, tokenColumn);
            default:
                if (Character.isDigit(c) || c == '-') return numberToken();
                if (c == 't' || c == 'f') return booleanToken();
                if (c == 'n') return nullToken();
                throw new RuntimeException("Unexpected character '" + c + "' at line " + line + ", column " + column);
        }
    }

    private Token nullToken() {
        int tokenLine = line;
        int tokenColumn = column;
        if (input.startsWith("null", pos)) {
            pos += 4;
            column += 4;
            return new Token(TokenType.NULL, null, tokenLine, tokenColumn);
        }
        throw new RuntimeException("Invalid null at line " + line + ", column " + column);
    }

    private Token booleanToken() {
        int tokenLine = line;
        int tokenColumn = column;
        if (input.startsWith("true", pos)) {
            pos += 4; column += 4;
            return new Token(TokenType.BOOLEAN, "true", tokenLine, tokenColumn);
        }
        if (input.startsWith("false", pos)) {
            pos += 5; column += 5;
            return new Token(TokenType.BOOLEAN, "false", tokenLine, tokenColumn);
        }
        throw new RuntimeException("Invalid boolean at line " + line + ", column " + column);
    }

    private Token numberToken() {
        int tokenLine = line;
        int tokenColumn = column;
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(currentChar()) || currentChar() == '.' || currentChar() == '-') {
            sb.append(currentChar());
            advance();
        }
        return new Token(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn);
    }

    private Token stringToken() {
        int tokenLine = line;
        int tokenColumn = column;
        advance(); // skip opening quote
        StringBuilder sb = new StringBuilder();
        while (currentChar() != '"' && currentChar() != '\0') {
            sb.append(currentChar());
            advance();
        }
        if (currentChar() == '\0') {
            throw new RuntimeException("Unterminated string at line " + tokenLine + ", column " + tokenColumn);
        }
        advance(); // skip closing quote
        return new Token(TokenType.STRING, sb.toString(), tokenLine, tokenColumn);
    }
}
