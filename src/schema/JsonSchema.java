package schema;

import java.util.Map;

public class JsonSchema {
    private final Map<String, Object> schemaRules;

    public JsonSchema(Map<String, Object> schemaRules) {
        this.schemaRules = schemaRules;
    }

    public Map<String, Object> getSchemaRules() {
        return schemaRules;
    }
}
