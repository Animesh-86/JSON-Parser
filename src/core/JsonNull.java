package core;


public class JsonNull extends JsonValue {

    public static final JsonNull INSTANCE = new JsonNull();

    JsonNull() {}

    @Override
    public JsonValue get(String key){
        return null;
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel){
        return "null";
    }
}