package core;


public class JsonNull extends JsonValue {

    public static final JsonNull INSTANCE = new JsonNull();

    JsonNull() {}

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        return "null";
    }
}