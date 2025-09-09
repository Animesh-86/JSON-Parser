package core;

public class Tokenizer {
    private String input;
    private int pos;

    public Tokenizer(String input, int pos) {
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
                if(c == 't' || c == 'f') return boolenToken();
                if(c == 'n') return nullToken();
                throw new RuntimeException("Unexpected chraracter: " + c);
        }
    }

    private Token nullToken() {
    }

    private Token boolenToken() {
    }

    private Token numberToken() {
    }

    private Token stringToken() {
        
    }

}
