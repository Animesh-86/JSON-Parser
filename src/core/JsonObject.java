package core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject extends JsonValue {

    // Stores the actual JSON data in insertion order
    private final Map<String, JsonValue> members = new LinkedHashMap<>();

    // Index for fast lookup
    private final Map<String, JsonValue> index = new HashMap<>();

    // Unified add/put method
    public void put(String key, JsonValue value) {
        JsonValue val = value == null ? JsonNull.INSTANCE : value;
        members.put(key, val);  // maintain JSON order
        index.put(key, val);    // update index
    }

    // Indexed get
    @Override
    public JsonValue get(String key) {
        return index.get(key);   // O(1) lookup
    }

    public boolean containsKey(String key) {
        return index.containsKey(key);
    }

    public Set<String> keySet() {
        return members.keySet();
    }

    // Optional: rebuild index if members changed
    public void rebuildIndex() {
        index.clear();
        index.putAll(members);
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        if (members.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder();

        if (indentFactor <= 0) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<String, JsonValue> e : members.entrySet()) {
                if (!first) sb.append(',');
                first = false;
                sb.append(escapeString(e.getKey()))
                        .append(':')
                        .append(e.getValue().toJson(indentFactor, 0));
            }
            sb.append('}');
            return sb.toString();
        }

        // Pretty mode
        sb.append('{').append('\n');
        int i = 0, n = members.size();
        for (Map.Entry<String, JsonValue> e : members.entrySet()) {
            sb.append(indent(indentFactor, indentLevel + 1));
            sb.append(escapeString(e.getKey()))
                    .append(": ")
                    .append(e.getValue().toJson(indentFactor, indentLevel + 1));
            if (i < n - 1) sb.append(',');
            sb.append('\n');
            i++;
        }
        sb.append(indent(indentFactor, indentLevel)).append('}');
        return sb.toString();
    }
}
