package schema;

import core.*;
import exception.JsonParseException;

import java.util.List;
import java.util.Map;

public class JsonValidator {

    public static void validate(JsonValue json, Object schema) throws JsonParseException {
        if (schema instanceof String) {
            validatePrimitive(json, (String) schema);
        } else if (schema instanceof Map) {
            if (!(json instanceof JsonObject)) {
                throw new JsonParseException("Expected object but found: " + json.getClass().getSimpleName());
            }
            validateObject((JsonObject) json, (Map<String, Object>) schema);
        } else if (schema instanceof List) {
            if (!(json instanceof JsonArray)) {
                throw new JsonParseException("Expected array but found: " + json.getClass().getSimpleName());
            }
            validateArray((JsonArray) json, (List<Object>) schema);
        }
    }

    private static void validatePrimitive(JsonValue json, String type) throws JsonParseException {
        switch (type) {
            case "string":
                if (!(json instanceof JsonString)) throw new JsonParseException("Expected string");
                break;
            case "number":
                if (!(json instanceof JsonNumber)) throw new JsonParseException("Expected number");
                break;
            case "boolean":
                if (!(json instanceof JsonBoolean)) throw new JsonParseException("Expected boolean");
                break;
            case "null":
                if (!(json instanceof JsonNull)) throw new JsonParseException("Expected null");
                break;
            default:
                throw new JsonParseException("Unknown type in schema: " + type);
        }
    }

    private static void validateObject(JsonObject jsonObj, Map<String, Object> schemaObj) throws JsonParseException {
        for (Map.Entry<String, Object> entry : schemaObj.entrySet()) {
            String key = entry.getKey();
            Object schemaRule = entry.getValue();
            JsonValue value = jsonObj.get(key); // assuming JsonObject has get(String)
            if (value == null) {
                throw new JsonParseException("Missing required field: " + key);
            }
            validate(value, schemaRule); // recursive call
        }
    }

    private static void validateArray(JsonArray jsonArr, List<Object> schemaList) throws JsonParseException {
        if (schemaList.size() != 1) {
            throw new JsonParseException("Array schema should have exactly one element type");
        }
        Object elementSchema = schemaList.get(0);
        for (int i = 0; i < jsonArr.size(); i++) {
            validate(jsonArr.get(i), elementSchema); // use get(i)
        }
    }
}
