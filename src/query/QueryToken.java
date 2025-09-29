package query;

public class QueryToken {
    private final String key;    // key in JsonObject, can be null
    private final Integer index; // index if JsonArray access; null if not

    // Case: key only
    public QueryToken(String key) {
        this.key = key;
        this.index = null;
    }

    // Case: index only
    public QueryToken(int index) {
        this.key = null;
        this.index = index;
    }

    // Case: key + index
    public QueryToken(String key, int index) {
        this.key = key;
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public Integer getIndex() {
        return index;
    }

    public boolean isArrayAccess() {
        return index != null;
    }

    public boolean isKeyOnly() {
        return key != null && index == null;
    }

    public boolean isIndexOnly() {
        return key == null && index != null;
    }

    @Override
    public String toString() {
        if (isKeyOnly()) {
            return key;
        } else if (isArrayAccess() && key != null) {
            return key + "[" + index + "]";
        } else if (isIndexOnly()) {
            return "[" + index + "]";
        } else {
            return "InvalidToken";
        }
    }
}
