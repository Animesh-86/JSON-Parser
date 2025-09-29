package streaming;

import java.io.*;
import java.util.Set;
import java.util.Stack;

public class JsonStreamParser {

    private final Reader reader;
    private final JsonEventHandler handler;
    private final Stack<JsonEventType> contextStack = new Stack<>();
    private boolean expectKey = false; // true if we are expecting a key inside an object
    private final Set<String> filterKeys; // keys to emit; if null or empty, emit all
    private boolean insideFilteredKey = false; // true when inside a key of interest

    public JsonStreamParser(Reader reader, JsonEventHandler handler, Set<String> filterKeys) {
        this.reader = reader;
        this.handler = handler;
        this.filterKeys = filterKeys;
    }

    public void parse() throws IOException {
        BufferedReader br = new BufferedReader(reader);
        int ch;

        while ((ch = br.read()) != -1) {
            char c = (char) ch;

            switch (c) {
                case '{':
                    contextStack.push(JsonEventType.START_OBJECT);
                    handler.handleEvent(new JsonEvent(JsonEventType.START_OBJECT, null));
                    expectKey = true; // inside object, we expect a key next
                    break;

                case '}':
                    if (!contextStack.isEmpty() && contextStack.peek() == JsonEventType.START_OBJECT) {
                        contextStack.pop();
                    }
                    handler.handleEvent(new JsonEvent(JsonEventType.END_OBJECT, null));
                    expectKey = false;
                    break;

                case '[':
                    contextStack.push(JsonEventType.START_ARRAY);
                    handler.handleEvent(new JsonEvent(JsonEventType.START_ARRAY, null));
                    expectKey = false;
                    break;

                case ']':
                    if (!contextStack.isEmpty() && contextStack.peek() == JsonEventType.START_ARRAY) {
                        contextStack.pop();
                    }
                    handler.handleEvent(new JsonEvent(JsonEventType.END_ARRAY, null));
                    expectKey = false;
                    break;

                case '"':
                    String str = readString(br);
                    if (expectKey) {
                        // check if key matches filter
                        if (filterKeys == null || filterKeys.isEmpty() || filterKeys.contains(str)) {
                            insideFilteredKey = true;
                            handler.handleEvent(new JsonEvent(JsonEventType.KEY, str));
                        } else {
                            insideFilteredKey = false;
                        }
                        expectKey = false; // after key, expect value
                    } else {
                        // emit value only if inside filtered key or no filter
                        if (insideFilteredKey || filterKeys == null || filterKeys.isEmpty()) {
                            handler.handleEvent(new JsonEvent(JsonEventType.VALUE, str));
                        }
                    }
                    break;


                case ':':
                    // colon separates key and value, expect value next
                    expectKey = false;
                    break;

                case ',':
                    // comma separates elements, determine if next is key or value
                    if (!contextStack.isEmpty() && contextStack.peek() == JsonEventType.START_OBJECT) {
                        expectKey = true; // next element in object is key
                    }
                    break;

                default:
                    if (!Character.isWhitespace(c)) {
                        String literal = readLiteral(br, c);
                        // emit value only if inside filtered key or no filter
                        if (insideFilteredKey || filterKeys == null || filterKeys.isEmpty()) {
                            handler.handleEvent(new JsonEvent(JsonEventType.VALUE, literal));
                        }
                        expectKey = contextStack.peek() == JsonEventType.START_OBJECT;
                    }
                    break;

            }
        }
    }

    private String readString(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = br.read()) != -1) {
            char c = (char) ch;
            if (c == '"') break; // end of string
            if (c == '\\') { // escape sequence
                int next = br.read();
                if (next == -1) break;
                char escaped = (char) next;
                switch (escaped) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        // Unicode escape sequence
                        char[] hex = new char[4];
                        if (br.read(hex, 0, 4) != 4) throw new IOException("Invalid Unicode escape");
                        sb.append((char) Integer.parseInt(new String(hex), 16));
                        break;
                    default:
                        sb.append(escaped); // unknown escape, append as-is
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    private String readLiteral(BufferedReader br, char firstChar) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);
        br.mark(1);
        int ch;
        while ((ch = br.read()) != -1) {
            char c = (char) ch;
            if (Character.isWhitespace(c) || c == ',' || c == ']' || c == '}') {
                br.reset(); // put back the char for next loop
                break;
            }
            sb.append(c);
            br.mark(1);
        }
        String literal = sb.toString();

        // Validate boolean/null
        if (literal.equals("true") || literal.equals("false") || literal.equals("null")) {
            return literal;
        }

        // Validate number
        try {
            Double.parseDouble(literal); // will throw exception if invalid
        } catch (NumberFormatException e) {
            throw new IOException("Invalid JSON literal: " + literal);
        }

        return literal;
    }

}
