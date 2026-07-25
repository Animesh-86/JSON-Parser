package com.jsonparser.core;

import java.math.BigDecimal;
import java.util.Objects;

public class JsonNumber extends JsonValue {
    private final String value;
    private final BigDecimal decimalValue;

    public JsonNumber(String value) {
        this.value = value;
        BigDecimal temp = null;
        try {
            if (value.equals("Infinity") || value.equals("+Infinity") || value.equals("-Infinity") || value.equals("NaN")) {
                temp = null; // Cannot store as BigDecimal
            } else if (value.startsWith("0x") || value.startsWith("0X")) {
                temp = new BigDecimal(Long.parseLong(value.substring(2), 16));
            } else if (value.startsWith("+")) {
                temp = new BigDecimal(value.substring(1));
            } else {
                temp = new BigDecimal(value);
            }
        } catch (NumberFormatException e) {
            temp = null;
        }
        this.decimalValue = temp;
    }
    
    public String getValue() {
        return value;
    }
    
    public int intValue() {
        if (decimalValue != null) return decimalValue.intValue();
        if (value.startsWith("0x") || value.startsWith("0X")) return Integer.parseInt(value.substring(2), 16);
        return (int) doubleValue();
    }
    
    public double doubleValue() {
        if (decimalValue != null) return decimalValue.doubleValue();
        if (value.equals("Infinity") || value.equals("+Infinity")) return Double.POSITIVE_INFINITY;
        if (value.equals("-Infinity")) return Double.NEGATIVE_INFINITY;
        if (value.equals("NaN")) return Double.NaN;
        if (value.startsWith("0x") || value.startsWith("0X")) return (double) Long.parseLong(value.substring(2), 16);
        return Double.parseDouble(value);
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
        if (this.decimalValue != null && that.decimalValue != null) {
            return decimalValue.compareTo(that.decimalValue) == 0;
        }
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        if (decimalValue != null) return decimalValue.stripTrailingZeros().hashCode();
        return value.hashCode();
    }
}
