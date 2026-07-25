package com.jsonparser.core;

public class JsonNull extends JsonValue {
    public static final JsonNull INSTANCE = new JsonNull();

    public JsonNull() {
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        return "null";
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof JsonNull;
    }

    @Override
    public int hashCode() {
        return 0; // null hash code is commonly 0
    }
}