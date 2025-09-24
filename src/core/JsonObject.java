package core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class JsonObject extends JsonValue {

    private final Map<String, JsonValue> members = new LinkedHashMap<>();

    public void put(String key, JsonValue v){
        members.put(key, v == null ? JsonNull.INSTANCE : v);
    }

    public JsonValue get(String key){
        return members.get(key);
    }

    public boolean containsKey(String key){
        return members.containsKey(key);
    }

    public Set<String> keySet(){
        return members.keySet();
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel){
        if (members.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder();

        if (indentFactor <= 0){
            sb.append('{');
            boolean first = true;
            for (Map.Entry<String, JsonValue> e : members.entrySet()){
                if (!first) sb.append(',');
                first = false;
                sb.append(escapeString(e.getKey())).append(':').append(e.getValue().toJson(indentFactor, 0));
            }
            sb.append('}');
            return sb.toString();
        }

        // pretty mode
        sb.append('{').append('\n');
        int i = 0, n = members.size();
        for (Map.Entry<String, JsonValue> e : members.entrySet()){
            sb.append(indent(indentFactor, indentLevel + 1));
            sb.append(escapeString(e.getKey()));
            sb.append(": ");
            sb.append(e.getValue().toJson(indentFactor, indentLevel + 1));
            if (i < n - 1) sb.append(',');
            sb.append('\n');
            i++;
        }
        sb.append(indent(indentFactor, indentLevel)).append('}');
        return sb.toString();
    }
}
