package com.jsonparser.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonArray extends JsonValue implements Iterable<JsonValue> {
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

    @Override
    public Iterator<JsonValue> iterator() {
        return values.iterator();
    }

    @Override
    public String toString() {
        return toJson(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonArray jsonArray = (JsonArray) o;
        return values.equals(jsonArray.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
}
