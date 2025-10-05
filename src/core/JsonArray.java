package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonArray extends JsonValue{
    private final List<JsonValue> values = new ArrayList<>();

    private final Map<String, JsonValue> idIndex = new HashMap<>();

    public void add(JsonValue v) {
        JsonValue value = v == null ? JsonNull.INSTANCE : v;
        values.add(value);

        // Auto-index if element is an object containing an "id" key
        if (value instanceof JsonObject obj) {
            if (obj.containsKey("id")) {
                JsonValue idVal = obj.get("id");
                if (idVal != null) {
                    idIndex.put(idVal.toString(), obj);
                }
            }
        }
    }

    public int size(){
        return values.size();
    }

    public JsonValue get(int i){
        return values.get(i);
    }

    public void rebuildIndex(){
        idIndex.clear();
        for(JsonValue v : values){
            if(v instanceof JsonObject obj){
                if(obj.containsKey("id")){
                    JsonValue idVal = obj.get("id");
                    if(idVal != null){
                        idIndex.put(idVal.toString(), obj);
                    }
                }
            }
        }
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        if (values.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        if (indentFactor <= 0) {
            sb.append('[');
            for (int i = 0; i < values.size(); i++) {
                if (i > 0) sb.append(',');
                sb.append(values.get(i).toJson(indentFactor, 0));
            }
            sb.append(']');
            return sb.toString();
        }

        // pretty mode
        sb.append('[').append('\n');
        for (int i = 0; i < values.size(); i++) {
            sb.append(indent(indentFactor, indentLevel + 1));
            sb.append(values.get(i).toJson(indentFactor, indentLevel + 1));
            if (i < values.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append(indent(indentFactor, indentLevel)).append(']');
        return sb.toString();
    }
}
