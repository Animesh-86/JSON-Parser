package core;

import java.util.Arrays;

public abstract class JsonValue {

    public String toJson(int indentFactor) {
        return toJson(indentFactor, 0);
    }

    protected abstract String toJson(int indentFactor, int indentLevel);

    protected String indent(int indentFactor, int indentLevel){
        if(indentFactor <= 0) return "";
        int total = indentFactor * indentLevel;
        char[] a = new char[total];
        Arrays.fill(a, ' ');
        return new String(a);
    }

    protected static String escapeString(String s){
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            switch(c){
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if(c < 0x20){
                        sb.append(String.format("\\u%04x", (int) c));
                    }else{
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}

