package query;

public class QueryToken{
    private final String key;    // key in JsonObject
    private final Integer index; // index if JsonArray access; null if not an array

    public QueryToken(String key, Integer index) {
        this.key = key;
        this.index = index;
    }

    public String getKey(){
        return key;
    }

    public Integer getIndex(){
        return index;
    }

    public boolean isArrayAccess(){
        return index != null;
    }

    @Override
    public String toString(){
        if (isArrayAccess()){
            return key + "[" + index + "]";
        } else {
            return key;
        }
    }
}
