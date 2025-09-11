package core;

public class Token {
    TokenType type;
    String value;
    public int line;
    public int column;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}

