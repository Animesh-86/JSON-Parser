package core;


public class JsonBoolean extends JsonValue {
    private final boolean value;
    public JsonBoolean(boolean value){
        this.value = value;
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        return Boolean.toString(value);
    }
}

