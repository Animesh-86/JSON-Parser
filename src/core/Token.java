package core;

enum TokenType{
    BEGIN_OBJECT, END_OBJECT,
    BEGIN_ARRAY, END_ARRAY,
    COLON, COMMA,
    STRING,
    NUMBER,
    BOOLEAN,
    NULL,
    EOF
}

public class Token {
    TokenType type;
    String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}

