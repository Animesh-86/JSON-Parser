package com.jsonparser.core;

import com.jsonparser.exception.JsonParseException;

public class Lexer {
    private String input;
    private int pos = 0;
    private int line = 1;
    private int column = 0;

    private ParserConfig config;

    public Lexer(String input, int pos, ParserConfig config) {
        this.input = input;
        this.pos = pos;
        this.config = config != null ? config : ParserConfig.strict();
    }

    public Lexer(String input, ParserConfig config) {
        this(input, 0, config);
    }

    public Lexer(String input) {
        this(input, 0, ParserConfig.strict());
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
        while (true) {
            if (Character.isWhitespace(currentChar())) {
                advance();
            } else if (config.isAllowComments() && currentChar() == '/') {
                int peekPos = pos + 1;
                char nextC = peekPos < input.length() ? input.charAt(peekPos) : '\0';
                if (nextC == '/') {
                    // Single line comment
                    while (currentChar() != '\n' && currentChar() != '\0') {
                        advance();
                    }
                } else if (nextC == '*') {
                    // Multi-line comment
                    advance(); advance(); // skip /*
                    while (currentChar() != '\0') {
                        if (currentChar() == '*' && (pos + 1 < input.length() && input.charAt(pos + 1) == '/')) {
                            advance(); advance(); // skip */
                            break;
                        }
                        advance();
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    public Token nextToken() {
        skipWhiteSpace();
        char c = currentChar();
        int tokenLine = line;
        int tokenColumn = column;

        switch (c) {
            case '{':
                advance();
                return new Token(TokenType.BEGIN_OBJECT, null, tokenLine, tokenColumn);
            case '}':
                advance();
                return new Token(TokenType.END_OBJECT, null, tokenLine, tokenColumn);
            case '[':
                advance();
                return new Token(TokenType.BEGIN_ARRAY, null, tokenLine, tokenColumn);
            case ']':
                advance();
                return new Token(TokenType.END_ARRAY, null, tokenLine, tokenColumn);
            case ':':
                advance();
                return new Token(TokenType.COLON, null, tokenLine, tokenColumn);
            case ',':
                advance();
                return new Token(TokenType.COMMA, null, tokenLine, tokenColumn);
            case '"':
                return stringToken('"');
            case '\'':
                if (config.isAllowSingleQuotes()) return stringToken('\'');
                throw new JsonParseException("Single quotes not allowed at line " + line + ", column " + column);
            case '\0':
                return new Token(TokenType.EOF, null, tokenLine, tokenColumn);
            default:
                if (c == '-' || c == '+' || Character.isDigit(c) || c == '.') return numberToken();
                if (c == 't' || c == 'f') return booleanToken();
                if (c == 'n') return nullToken();
                if (c == 'I' || c == 'N') return numberToken(); // Infinity, NaN
                if (config.isAllowUnquotedKeys() && isIdentifierStart(c)) return unquotedKeyToken();
                throw new JsonParseException("Unexpected character '" + c + "' at line " + line + ", column " + column);
        }
    }
    
    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_' || c == '$';
    }
    
    private boolean isIdentifierPart(char c) {
        return isIdentifierStart(c) || Character.isDigit(c);
    }
    
    private Token unquotedKeyToken() {
        int tokenLine = line;
        int tokenColumn = column;
        StringBuilder sb = new StringBuilder();
        while (isIdentifierPart(currentChar())) {
            sb.append(currentChar());
            advance();
        }
        return new Token(TokenType.STRING, sb.toString(), tokenLine, tokenColumn);
    }

    private Token nullToken() {
        int tokenLine = line;
        int tokenColumn = column;
        if (input.startsWith("null", pos)) {
            pos += 4;
            column += 4;
            return new Token(TokenType.NULL, null, tokenLine, tokenColumn);
        }
        throw new JsonParseException("Invalid null at line " + line + ", column " + column);
    }

    private Token booleanToken() {
        int tokenLine = line;
        int tokenColumn = column;
        if (input.startsWith("true", pos)) {
            pos += 4;
            column += 4;
            return new Token(TokenType.BOOLEAN, "true", tokenLine, tokenColumn);
        }
        if (input.startsWith("false", pos)) {
            pos += 5;
            column += 5;
            return new Token(TokenType.BOOLEAN, "false", tokenLine, tokenColumn);
        }
        throw new JsonParseException("Invalid boolean at line " + line + ", column " + column);
    }

    private Token numberToken() {
        int tokenLine = line;
        int tokenColumn = column;
        
        if (config.isAllowJson5Numbers()) {
            if (input.startsWith("Infinity", pos)) {
                pos += 8; column += 8;
                return new Token(TokenType.NUMBER, "Infinity", tokenLine, tokenColumn);
            }
            if (input.startsWith("-Infinity", pos)) {
                pos += 9; column += 9;
                return new Token(TokenType.NUMBER, "-Infinity", tokenLine, tokenColumn);
            }
            if (input.startsWith("+Infinity", pos)) {
                pos += 9; column += 9;
                return new Token(TokenType.NUMBER, "Infinity", tokenLine, tokenColumn);
            }
            if (input.startsWith("NaN", pos)) {
                pos += 3; column += 3;
                return new Token(TokenType.NUMBER, "NaN", tokenLine, tokenColumn);
            }
            if (currentChar() == '+' || currentChar() == '-') {
                advance(); // JSON5 supports + and - for NaN and Infinity, though NaN with sign is rare
                if (input.startsWith("NaN", pos - 1)) { /* ... */ } // Handled loosely for now
            }
        }
        
        StringBuilder sb = new StringBuilder();
        
        if (currentChar() == '-' || (config.isAllowJson5Numbers() && currentChar() == '+')) {
            sb.append(currentChar());
            advance();
        }
        
        if (config.isAllowJson5Numbers() && currentChar() == '0' && (pos + 1 < input.length() && (input.charAt(pos + 1) == 'x' || input.charAt(pos + 1) == 'X'))) {
            sb.append("0x");
            advance(); advance();
            while (isHexDigit(currentChar())) {
                sb.append(currentChar());
                advance();
            }
            return new Token(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn);
        }
        
        if (currentChar() == '0') {
            sb.append(currentChar());
            advance();
        } else if (Character.isDigit(currentChar())) {
            while (Character.isDigit(currentChar())) {
                sb.append(currentChar());
                advance();
            }
        } else if (!(config.isAllowJson5Numbers() && currentChar() == '.')) { // JSON5 allows numbers starting with .
            throw new JsonParseException("Invalid number format at line " + tokenLine + ", column " + tokenColumn);
        }
        
        if (currentChar() == '.') {
            sb.append(currentChar());
            advance();
            // JSON5 allows trailing dot (e.g., 1.)
            if (!Character.isDigit(currentChar()) && !config.isAllowJson5Numbers()) {
                throw new JsonParseException("Invalid fraction in number at line " + tokenLine + ", column " + tokenColumn);
            }
            while (Character.isDigit(currentChar())) {
                sb.append(currentChar());
                advance();
            }
        }
        
        if (currentChar() == 'e' || currentChar() == 'E') {
            sb.append(currentChar());
            advance();
            if (currentChar() == '+' || currentChar() == '-') {
                sb.append(currentChar());
                advance();
            }
            if (!Character.isDigit(currentChar())) {
                throw new JsonParseException("Invalid exponent in number at line " + tokenLine + ", column " + tokenColumn);
            }
            while (Character.isDigit(currentChar())) {
                sb.append(currentChar());
                advance();
            }
        }
        
        return new Token(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn);
    }
    
    private boolean isHexDigit(char c) {
        return Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private Token stringToken(char quoteChar) {
        int tokenLine = line;
        int tokenColumn = column;
        advance(); // skip opening quote
        StringBuilder sb = new StringBuilder();
        while (currentChar() != quoteChar && currentChar() != '\0') {
            if (currentChar() == '\\') {
                advance(); // skip backslash
                switch (currentChar()) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u': sb.append(readUnicodeEscape()); continue;
                    default:
                        throw new JsonParseException("Invalid escape: \\" + currentChar() + " at line " + line + ", column " + column);
                }
                advance();
            } else {
                if (currentChar() < 0x20) {
                    throw new JsonParseException("Unescaped control character at line " + line + ", column " + column);
                }
                sb.append(currentChar());
                advance();
            }
        }
        if (currentChar() == '\0') {
            throw new JsonParseException("Unterminated string at line " + tokenLine + ", column " + tokenColumn);
        }
        advance(); // skip closing quote
        return new Token(TokenType.STRING, sb.toString(), tokenLine, tokenColumn);
    }
    
    private char readUnicodeEscape() {
        advance(); // skip 'u'
        char[] hex = new char[4];
        for (int i = 0; i < 4; i++) {
            if (currentChar() == '\0') {
                throw new JsonParseException("Unterminated unicode escape at line " + line + ", column " + column);
            }
            hex[i] = currentChar();
            advance();
        }
        try {
            return (char) Integer.parseInt(new String(hex), 16);
        } catch (NumberFormatException e) {
            throw new JsonParseException("Invalid unicode escape: \\u" + new String(hex) + " at line " + line + ", column " + column);
        }
    }
}
