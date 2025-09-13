package core;

public class Token {
    public TokenType type;
    protected String value;
    public int line;
    public int column;

    public Token(TokenType type, String value) {
        this(type, value, -1, -1); // default line/col if not provided
    }

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, %s, line=%d, col=%d)",
                type, value, line, column);
    }
}
