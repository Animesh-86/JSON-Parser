package main;

import core.*;
import diff.JsonDiff;

public class Main {
    public static void main(String[] args) {
        // -----------------------
        // Example JSON 1
        // -----------------------
        JsonObject json1 = new JsonObject();
        json1.put("id", new JsonPrimitive(1));
        json1.put("name", new JsonPrimitive("Alice"));
        JsonArray arr1 = new JsonArray();
        arr1.add(new JsonPrimitive(true));
        arr1.add(new JsonPrimitive(false));
        json1.put("flags", arr1);

        // -----------------------
        // Example JSON 2
        // -----------------------
        JsonObject json2 = new JsonObject();
        json2.put("id", new JsonPrimitive(1)); // same
        json2.put("name", new JsonPrimitive("Alicia")); // changed
        JsonArray arr2 = new JsonArray();
        arr2.add(new JsonPrimitive(true)); // same
        json2.put("flags", arr2); // removed one element
        json2.put("active", new JsonPrimitive(true)); // added key

        // -----------------------
        // Run JSON Diff
        // -----------------------
        JsonDiff.DiffResult result = JsonDiff.diff(json1, json2);

        // Pretty-print differences
        System.out.println("JSON Diff Results:");
        System.out.println(result.toPrettyString());
    }
}
