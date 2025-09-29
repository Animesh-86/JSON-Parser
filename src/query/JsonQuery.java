package query;

import core.JsonArray;
import core.JsonObject;
import core.JsonValue;
import exception.JsonParseException;

import java.util.ArrayList;
import java.util.List;

public class JsonQuery {
    private final JsonValue root;

    public JsonQuery(JsonValue root){
        this.root = root;
    }

    public JsonValue query(String query) throws JsonParseException{
        List<QueryToken> tokens = parseQuery(query);
        return traverse(root, tokens);
    }

    private List<QueryToken> parseQuery(String query) throws JsonParseException{
        if(!query.startsWith("$")){
            throw new JsonParseException("Query must starts with $");
        }
        query = query.substring(1);
        if(query.startsWith(".")) query = query.substring(1);

        String[] parts = query.split("\\.");
        List<QueryToken> tokens = new ArrayList<>();
        return tokens;
    }

    private JsonValue traverse(JsonValue current, List<QueryToken> tokens) throws JsonParseException {
        for (QueryToken token : tokens) {
            if (current instanceof JsonObject) {
                JsonObject obj = (JsonObject) current;
                current = obj.get(token.getKey());
                if (current == null) {
                    throw new JsonParseException("Key not found: " + token.getKey());
                }
                if (token.isArrayAccess()) {
                    if (!(current instanceof JsonArray)) {
                        throw new JsonParseException("Expected array at key: " + token.getKey());
                    }
                    JsonArray arr = (JsonArray) current;
                    int idx = token.getIndex();
                    if (idx < 0 || idx >= arr.size()) {
                        throw new JsonParseException("Array index out of bounds: " + idx);
                    }
                    current = arr.get(idx);
                }
            } else if (current instanceof JsonArray) {
                if (!token.isArrayAccess()) {
                    throw new JsonParseException("Expected object key but found array");
                }
                JsonArray arr = (JsonArray) current;
                int idx = token.getIndex();
                if (idx < 0 || idx >= arr.size()) {
                    throw new JsonParseException("Array index out of bounds: " + idx);
                }
                current = arr.get(idx);
            } else {
                throw new JsonParseException("Cannot traverse primitive value for token: " + token);
            }
        }
        return current;
    }
}
