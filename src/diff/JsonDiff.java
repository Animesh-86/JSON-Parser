package diff;

import java.util.*;

import core.JsonArray;
import core.JsonNull;
import core.JsonObject;
import core.JsonPrimitive;
import core.JsonValue;

import static java.util.Objects.isNull;

public class JsonDiff {
    /**
     * Represents the type of difference operation.
     */
    public enum DiffOp {
        ADD,
        REMOVE,
        REPLACE
    }

    /**
     * A single entry representing a difference between two JSON values.
     * Contains the operation type, the path to the change, and the old/new values.
     */
    public static final class DiffEntry {
        public final DiffOp op;
        public final String path;
        public final JsonValue oldValue;
        public final JsonValue newValue;

        public DiffEntry(DiffOp op, String path, JsonValue oldValue, JsonValue newValue) {
            this.op = op;
            this.path = path;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            switch (op) {
                case ADD:
                    return String.format("ADD %s => %s", path, valueToString(newValue));
                case REMOVE:
                    return String.format("REM %s (was %s)", path, valueToString(oldValue));
                case REPLACE:
                    return String.format("REP %s: %s -> %s", path, valueToString(oldValue), valueToString(newValue));
                default:
                    return op + " " + path;
            }
        }

        private String valueToString(JsonValue v) {
            if (v == null || v == JsonNull.INSTANCE) return "null";
            return v.toString();
        }
    }

    /**
     * The result of a JSON comparison, containing a list of all differences.
     */
    public static final class DiffResult {
        private final List<DiffEntry> entries;

        public DiffResult(List<DiffEntry> entries) {
            this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
        }

        public List<DiffEntry> entries() {
            return entries;
        }

        public boolean isEmpty() {
            return entries.isEmpty();
        }

        public String toPrettyString() {
            if (entries.isEmpty()) return "(no differences)";
            StringBuilder b = new StringBuilder();
            for (DiffEntry e : entries) {
                b.append(e.toString()).append('\n');
            }
            return b.toString();
        }
    }

    /**
     * Computes the differences between two JSON values.
     *
     * @param left  The original JSON value.
     * @param right The new JSON value.
     * @return A DiffResult object containing a list of differences.
     */
    public static DiffResult diff(JsonValue left, JsonValue right) {
        List<DiffEntry> out = new ArrayList<>();
        compare(left, right, "", out);
        return new DiffResult(out);
    }

    private static void compare(JsonValue a, JsonValue b, String path, List<DiffEntry> out) {
        // Case 1: One value is null and the other is not.
        if (isNull(a) && !isNull(b)) {
            out.add(new DiffEntry(DiffOp.ADD, path, null, b));
            return;
        }

        if (!isNull(a) && isNull(b)) {
            out.add(new DiffEntry(DiffOp.REMOVE, path, a, null));
            return;
        }

        if (isNull(a) && isNull(b)) {
            return;
        }

        // Case 2: Different types, so it's a replacement.
        if (!a.getClass().equals(b.getClass())) {
            out.add(new DiffEntry(DiffOp.REPLACE, path, a, b));
            return;
        }

        // Case 3: Both are objects.
        if (a instanceof JsonObject) {
            JsonObject ao = (JsonObject) a;
            JsonObject bo = (JsonObject) b;

            Set<String> keys = new LinkedHashSet<>();
            keys.addAll(ao.keySet());
            keys.addAll(bo.keySet());

            for (String key : keys) {
                JsonValue va = ao.get(key);
                JsonValue vb = bo.get(key);
                String childPath = path + "/" + escapePointerToken(key);
                compare(va, vb, childPath, out);
            }
            return;
        }

        // Case 4: Both are arrays.
        if (a instanceof JsonArray) {
            JsonArray aa = (JsonArray) a;
            JsonArray bb = (JsonArray) b;
            int na = aa.size();
            int nb = bb.size();
            int n = Math.max(na, nb);
            for (int i = 0; i < n; ++i) {
                if (i >= na) {
                    JsonValue vb = bb.get(i);
                    out.add(new DiffEntry(DiffOp.ADD, path + "/" + i, null, vb));
                } else if (i >= nb) {
                    JsonValue va = aa.get(i);
                    out.add(new DiffEntry(DiffOp.REMOVE, path + "/" + i, va, null));
                } else {
                    compare(aa.get(i), bb.get(i), path + "/" + i, out);
                }
            }
            return;
        }

        // Case 5: Both are primitive values.
        // This check is now correctly placed to only run for primitive types.
        if (a instanceof JsonPrimitive && !a.equals(b)) {
            out.add(new DiffEntry(DiffOp.REPLACE, path, a, b));
        }
    }

    private static boolean isNull(JsonValue v) {
        return v == null || v == JsonNull.INSTANCE;
    }

    // RFC6901 escaping for '~' and '/'
    private static String escapePointerToken(String token) {
        return token.replace("~", "~0").replace("/", "~1");
    }
}