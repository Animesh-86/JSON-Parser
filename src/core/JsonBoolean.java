package core;


public class JsonBoolean extends JsonValue {
    private final boolean value;
    public JsonBoolean(boolean value){
        this.value = value;
    }

    @Override
    public JsonValue get(String key) {
        return null;
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        return Boolean.toString(value);
    }
}

