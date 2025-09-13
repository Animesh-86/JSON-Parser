package core;

import java.util.ArrayList;
import java.util.List;

public class JsonArray extends JsonValue {
    private List<JsonValue> elements = new ArrayList<>();

    public void add(JsonValue value) {
        elements.add(value);
    }

    public JsonValue get(int index) {
        return elements.get(index);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
