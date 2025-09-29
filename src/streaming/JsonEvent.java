package streaming;

//Each event should carry type + value
public class JsonEvent {
    private final JsonEventType type;
    private final String value;

    public JsonEvent(JsonEventType type, String value) {
        this.type = type;
        this.value = value;
    }

    public JsonEventType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value != null) {
            return type + " : " + value;
        }
        return type.toString();
    }
}
