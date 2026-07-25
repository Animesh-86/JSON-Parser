package com.jsonparser.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject extends JsonValue {

    private final Map<String, JsonValue> members = new LinkedHashMap<>();

    public void put(String key, JsonValue value) {
        members.put(key, value == null ? JsonNull.INSTANCE : value);
    }

    public JsonValue get(String key) {
        return members.get(key);
    }

    public boolean containsKey(String key) {
        return members.containsKey(key);
    }

    public Set<String> keySet() {
        return members.keySet();
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
            sb.append(indent(indentFactor, indentLevel + 1))
                    .append(escapeString(e.getKey()))
                    .append(": ")
                    .append(e.getValue().toJson(indentFactor, indentLevel + 1));
            if (i < n - 1) sb.append(',');
            sb.append('\n');
            i++;
        }
        sb.append(indent(indentFactor, indentLevel)).append('}');
        return sb.toString();
    }

    public int size() {
        return members.size();
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public Set<Map.Entry<String, JsonValue>> entrySet() {
        return members.entrySet();
    }

    @Override
    public String toString() {
        return toJson(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonObject that = (JsonObject) o;
        return members.equals(that.members);
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }
}
