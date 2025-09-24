package core;

import java.math.BigDecimal;

class JsonNumber extends JsonValue{
    private final BigDecimal value;

    public JsonNumber(String numericLiteral){
        this.value = new BigDecimal(numericLiteral);
    }
    public JsonNumber(long v){
        this.value = BigDecimal.valueOf(v);
    }

    public JsonNumber(double v){
        this.value = BigDecimal.valueOf(v);
    }

    @Override
    protected String toJson(int indentFactor, int indentLevel) {
        return value.toPlainString();
    }

    @Override public String toString() { return value.toPlainString(); }
}


