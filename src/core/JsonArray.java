package core;

import java.util.ArrayList;
import java.util.List;

public class JsonArray extends JsonValue {
    private final List<JsonValue> values = new ArrayList<>();

    public void add(JsonValue v) {
        values.add(v == null ? JsonNull.INSTANCE : v);
    }

    public int size() {
        return values.size();
    }

    public JsonValue get(int i) {
        return values.get(i);
    }

    @Override
    public JsonValue get(String key) {
        return null;
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

        sb.append('[').append('\n');
        for (int i = 0; i < values.size(); i++) {
            sb.append(indent(indentFactor, indentLevel + 1))
                    .append(values.get(i).toJson(indentFactor, indentLevel + 1));
            if (i < values.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append(indent(indentFactor, indentLevel)).append(']');
        return sb.toString();
    }
}
