package exception;

public class JsonParseException extends RuntimeException {
    public JsonParseException(String message, int line, int column){
        super("Error at line " + line + ", column " + column + ": " + message);
    }
}
