package core;


public class JsonString extends JsonValue {
    public String value;
    public JsonString(String value) {
        this.value = value;
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel){
        return escapeString(value);
    }

    @Override
    public String toString(){
        return value;
    }
}



