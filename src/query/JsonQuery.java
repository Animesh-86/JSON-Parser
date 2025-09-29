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

    private List<QueryToken> parseQuery(String query) throws JsonParseException {
        if (!query.startsWith("$")) {
            throw new JsonParseException("Query must start with $");
        }

        query = query.substring(1); // remove $

        if (query.startsWith(".")) {
            query = query.substring(1); // remove leading dot if exists
        }

        String[] parts = query.split("\\.");
        List<QueryToken> tokens = new ArrayList<>();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            // Example: skills[1]
            if (part.contains("[")) {
                String key = part.substring(0, part.indexOf("["));
                String indexStr = part.substring(part.indexOf("[") + 1, part.indexOf("]"));

                try {
                    int idx = Integer.parseInt(indexStr);
                    tokens.add(new QueryToken(key, idx)); // key with index
                } catch (NumberFormatException e) {
                    throw new JsonParseException("Invalid array index: " + indexStr);
                }
            } else if (part.startsWith("[")) {
                // Example: [2] directly
                String indexStr = part.substring(1, part.length() - 1);

                try {
                    int idx = Integer.parseInt(indexStr);
                    tokens.add(new QueryToken(idx)); // pure index
                } catch (NumberFormatException e) {
                    throw new JsonParseException("Invalid array index: " + indexStr);
                }
            } else {
                // Just a plain key
                tokens.add(new QueryToken(part));
            }
        }

        return tokens;
    }

}
