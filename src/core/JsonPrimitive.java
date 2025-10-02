package core;

import java.util.Objects;

/**
 * Represents a JSON primitive value:
 * string, number, or boolean.
 */
public class JsonPrimitive extends JsonValue {

    private final Object value;

    public JsonPrimitive(String value) {
        this.value = value;
    }

    public JsonPrimitive(Number value) {
        this.value = value;
    }

    public JsonPrimitive(Boolean value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        if (value == null) return "null";

        if (value instanceof String) {
            return escapeString((String) value); // use same escape method as JsonObject
        }
        return value.toString(); // number/boolean as-is
    }

    @Override
    public String toString() {
        return toJson(0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JsonPrimitive)) return false;
        JsonPrimitive other = (JsonPrimitive) o;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
