package core;

import java.util.HashMap;
import java.util.Map;

public class JsonObject extends JsonValue {
    private Map<String, JsonValue> members = new HashMap<>();

    public void put(String key, JsonValue value) {
        members.put(key, value);
    }

    public JsonValue get(String key) {
        return members.get(key);
    }

    @Override
    public String toString() {
        return members.toString();
    }
}
