package core;

public class Lexer {
    private String input;
    private int pos = 0;
    private int line = 1;
    private int column = 0;

    public Lexer(String input, int pos) {
        this.input = input;
        this.pos = pos;
    }
    
    private char currentChar(){
        return pos < input.length() ? input.charAt(pos) : '\0';
    }
    
    private void skipWhiteSpace(){
        while(Character.isWhitespace(currentChar())) pos++;
    }
    
    public Token nextToken(){
        skipWhiteSpace();
        char c = currentChar();
        
        switch(c){
            case '{': pos++; return new Token(TokenType.BEGIN_OBJECT, null);
            case '}': pos++; return new Token(TokenType.END_OBJECT, null);
            case '[': pos++; return new Token(TokenType.BEGIN_ARRAY, null);
            case ']': pos++; return new Token(TokenType.END_ARRAY, null);
            case ':': pos++; return new Token(TokenType.COLON, null);
            case ',': pos++; return new Token(TokenType.COMMA, null);
            case '"': return stringToken();
            case '\0': return new Token(TokenType.EOF, null);
            default:
                if(Character.isDigit(c) || c == '-') return numberToken();
                if(c == 't' || c == 'f') return booleanToken();
                if(c == 'n') return nullToken();
                throw new RuntimeException("Unexpected chraracter: " + c);
        }
    }

    private Token nullToken() {
        if(input.startsWith("null", pos)){
            pos += 4;
            return new Token(TokenType.NULL, null);
        }
        throw new RuntimeException("Invalid null");
    }

    private Token booleanToken() {
        if(input.startsWith("true", pos)){
            pos += 4;
            return new Token(TokenType.BOOLEAN, "true");
        }

        if(input.startsWith("false", pos)) {
            pos += 5;
            return new Token(TokenType.BOOLEAN, "false");
        }
        throw new RuntimeException("Invalid boolean");
    }

    private Token numberToken() {
        StringBuilder sb = new StringBuilder();
        while(Character.isDigit(currentChar()) || currentChar() == '.' || currentChar() == '-'){
            sb.append(currentChar());
            pos++;
        }
        return new Token(TokenType.NUMBER, sb.toString());
    }

    private Token stringToken() {
        pos++;
        StringBuilder sb = new StringBuilder();
        while(currentChar() != '"'){
            sb.append(currentChar());
            pos++;
        }
        pos++;
        return new Token(TokenType.STRING, sb.toString());
    }
}
