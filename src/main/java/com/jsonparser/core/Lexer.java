package com.jsonparser.core;

import com.jsonparser.exception.JsonParseException;

public class Lexer {
    private String input;
    private int pos = 0;
    private int line = 1;
    private int column = 0;

    public Lexer(String input, int pos) {
        this.input = input;
        this.pos = pos;
    }

    public Lexer(String input) {
        this(input, 0);
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
                return stringToken();
            case '\0':
                return new Token(TokenType.EOF, null, tokenLine, tokenColumn);
            default:
                if (c == '-' || Character.isDigit(c)) return numberToken();
                if (c == 't' || c == 'f') return booleanToken();
                if (c == 'n') return nullToken();
                throw new JsonParseException("Unexpected character '" + c + "' at line " + line + ", column " + column);
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
        StringBuilder sb = new StringBuilder();
        
        if (currentChar() == '-') {
            sb.append(currentChar());
            advance();
        }
        
        if (currentChar() == '0') {
            sb.append(currentChar());
            advance();
        } else if (Character.isDigit(currentChar())) {
            while (Character.isDigit(currentChar())) {
                sb.append(currentChar());
                advance();
            }
        } else {
            throw new JsonParseException("Invalid number format at line " + tokenLine + ", column " + tokenColumn);
        }
        
        if (currentChar() == '.') {
            sb.append(currentChar());
            advance();
            if (!Character.isDigit(currentChar())) {
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

    private Token stringToken() {
        int tokenLine = line;
        int tokenColumn = column;
        advance(); // skip opening quote
        StringBuilder sb = new StringBuilder();
        while (currentChar() != '"' && currentChar() != '\0') {
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
