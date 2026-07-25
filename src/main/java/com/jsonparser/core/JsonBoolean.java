package com.jsonparser.core;

import java.util.Objects;

public class JsonBoolean extends JsonValue {
    public static final JsonBoolean TRUE = new JsonBoolean(true);
    public static final JsonBoolean FALSE = new JsonBoolean(false);

    private final boolean value;

    public JsonBoolean(boolean value) {
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonBoolean that = (JsonBoolean) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }
}
