import exception.JsonParseException;
import core.*;
import schema.JsonValidator;
import schema.JsonSchema;


import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // -------- JSON Object --------
            JsonObject json = new JsonObject();
            json.put("name", new JsonString("Animesh"));
            json.put("age", new JsonNumber(20));

            JsonObject contact = new JsonObject();
            contact.put("email", new JsonString("animesh@example.com"));
            contact.put("phone", new JsonString("1234567890"));
            json.put("contact", contact);

            JsonArray skills = new JsonArray();
            skills.add(new JsonString("Java"));
            skills.add(new JsonString("ML"));
            json.put("skills", skills);

            JsonArray projects = new JsonArray();

            JsonObject proj1 = new JsonObject();
            proj1.put("title", new JsonString("Structo"));
            proj1.put("durationMonths", new JsonNumber(6));

            JsonObject proj2 = new JsonObject();
            proj2.put("title", new JsonString("Expenzo"));
            proj2.put("durationMonths", new JsonNumber(4));

            projects.add(proj1);
            projects.add(proj2);

            json.put("projects", projects);

            // -------- Schema --------
            Map<String, Object> contactSchema = new HashMap<>();
            contactSchema.put("email", "string");
            contactSchema.put("phone", "string");

            Map<String, Object> projectSchema = new HashMap<>();
            projectSchema.put("title", "string");
            projectSchema.put("durationMonths", "number");

            Map<String, Object> schemaMap = new HashMap<>();
            schemaMap.put("name", "string");
            schemaMap.put("age", "number");
            schemaMap.put("contact", contactSchema);
            schemaMap.put("skills", List.of("string"));
            schemaMap.put("projects", List.of(projectSchema));

            JsonSchema schema = new JsonSchema(schemaMap);

            // -------- Validation --------
            JsonValidator.validate(json, schema.getSchemaRules());
            System.out.println("Validation passed!");

        } catch (JsonParseException e) {
            System.err.println("Validation failed: " + e.getMessage());
        }
    }
}


