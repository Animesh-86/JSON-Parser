package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class JsonValue {
}

class JsonObject extends JsonValue{
    Map<String, JsonValue> map = new HashMap<>();
}

class JsonArray extends JsonValue{
    List<JsonValue> list = new ArrayList<>();
}

class JsonString extends JsonValue{
    String value;
    JsonString(String value) {
        this.value = value;
    }
}

class JsonNumber extends JsonValue{
    long value;
    JsonNumber(long value){
        this.value = value;
    }
}

class JsonBoolean extends JsonValue{
    boolean value;
    JsonBoolean(boolean value){
        this.value = value;
    }
}

class JsonNull extends JsonValue { }