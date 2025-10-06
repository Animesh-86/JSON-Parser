package index;

import core.JsonArray;
import core.JsonObject;
import core.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class JsonIndexer {
    private final Map<String, JsonValue> indexMap = new HashMap<>();

    public JsonIndexer(JsonValue root) {
        buildIndex("$", root);
    }

    private void buildIndex(String path, JsonValue value) {
        indexMap.put(path, value);

        if (value instanceof JsonObject obj) {
            for (String key : obj.keySet()) {
                JsonValue child = obj.get(key);
                buildIndex(path + "." + key, child);
            }
        } else if ((value instanceof JsonArray arr)) {
            for (int i = 0; i < arr.size(); i++) {
                JsonValue child = arr.get(i);
                buildIndex(path + "[" + i + "]", child);
            }
        }
    }

    public JsonValue get(String path) {
        return indexMap.get(path);
    }

    public boolean contains(String path) {
        return indexMap.containsKey(path);
    }

    public int size() {
        return indexMap.size();
    }
}
