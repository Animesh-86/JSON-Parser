package com.jsonparser.core;

import java.math.BigDecimal;
import java.util.Objects;

public class JsonNumber extends JsonValue {
    private final String value;
    private final BigDecimal decimalValue;

    public JsonNumber(String value) {
        this.value = value;
        this.decimalValue = new BigDecimal(value);
    }
    
    public String getValue() {
        return value;
    }
    
    public int intValue() {
        return decimalValue.intValue();
    }
    
    public double doubleValue() {
        return decimalValue.doubleValue();
    }
    
    public BigDecimal bigDecimalValue() {
        return decimalValue;
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonNumber that = (JsonNumber) o;
        return decimalValue.compareTo(that.decimalValue) == 0;
    }

    @Override
    public int hashCode() {
        // Strip trailing zeros for consistent hashcode across scales
        return decimalValue.stripTrailingZeros().hashCode();
    }
}
