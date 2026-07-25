package com.jsonparser.streaming;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class JsonStreamParser implements AutoCloseable {

    private final Reader reader;
    private final JsonEventHandler handler;
    private final Deque<JsonEventType> contextStack = new ArrayDeque<>();
    private boolean expectKey = false;
    private final Set<String> filterKeys;
    private int activeFilterDepth = -1;

    public JsonStreamParser(Reader reader, JsonEventHandler handler, Set<String> filterKeys) {
        this.reader = reader;
        this.handler = handler;
        this.filterKeys = filterKeys;
    }

    public void parse() throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            int ch;
            while ((ch = br.read()) != -1) {
                char c = (char) ch;

                if (activeFilterDepth != -1 && contextStack.size() < activeFilterDepth) {
                    activeFilterDepth = -1; // Exited the filtered object
                }
                
                boolean isFiltered = (activeFilterDepth != -1) || filterKeys == null || filterKeys.isEmpty();

                switch (c) {
                    case '{':
                        handler.handleEvent(new JsonEvent(JsonEventType.START_OBJECT, null));
                        contextStack.push(JsonEventType.START_OBJECT);
                        expectKey = true;
                        break;

                    case '}':
                        if (!contextStack.isEmpty() && contextStack.peek() == JsonEventType.START_OBJECT) {
                            contextStack.pop();
                        }
                        if (activeFilterDepth != -1 && contextStack.size() < activeFilterDepth) {
                            activeFilterDepth = -1;
                        }
                        // Re-evaluate filter status after popping
                        isFiltered = (activeFilterDepth != -1) || filterKeys == null || filterKeys.isEmpty();
                        handler.handleEvent(new JsonEvent(JsonEventType.END_OBJECT, null));
                        expectKey = false;
                        break;

                    case '[':
                        handler.handleEvent(new JsonEvent(JsonEventType.START_ARRAY, null));
                        contextStack.push(JsonEventType.START_ARRAY);
                        expectKey = false;
                        break;

                    case ']':
                        if (!contextStack.isEmpty() && contextStack.peek() == JsonEventType.START_ARRAY) {
                            contextStack.pop();
                        }
                        if (activeFilterDepth != -1 && contextStack.size() < activeFilterDepth) {
                            activeFilterDepth = -1;
                        }
                        isFiltered = (activeFilterDepth != -1) || filterKeys == null || filterKeys.isEmpty();
                        handler.handleEvent(new JsonEvent(JsonEventType.END_ARRAY, null));
                        expectKey = false;
                        break;

                    case '"':
                        String str = readString(br);
                        if (expectKey) {
                            if (filterKeys == null || filterKeys.isEmpty() || filterKeys.contains(str)) {
                                activeFilterDepth = contextStack.size();
                                handler.handleEvent(new JsonEvent(JsonEventType.KEY, str));
                            } else if (activeFilterDepth == contextStack.size()) {
                                // Sibling key of a previously filtered key at the same level - do not emit, and reset filter
                                activeFilterDepth = -1; 
                            }
                            expectKey = false;
                        } else {
                            if (isFiltered) {
                                handler.handleEvent(new JsonEvent(JsonEventType.VALUE, str));
                            }
                        }
                        break;

                    case ':':
                        expectKey = false;
                        break;

                    case ',':
                        if (!contextStack.isEmpty() && contextStack.peek() == JsonEventType.START_OBJECT) {
                            expectKey = true;
                        }
                        break;

                    default:
                        if (!Character.isWhitespace(c)) {
                            String literal = readLiteral(br, c);
                            if (isFiltered) {
                                handler.handleEvent(new JsonEvent(JsonEventType.VALUE, literal));
                            }
                            expectKey = !contextStack.isEmpty() && contextStack.peek() == JsonEventType.START_OBJECT;
                        }
                        break;
                }
            }
        }
    }

    private String readString(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = br.read()) != -1) {
            char c = (char) ch;
            if (c == '"') break;
            if (c == '\\') {
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
                        char[] hex = new char[4];
                        if (br.read(hex, 0, 4) != 4) throw new IOException("Invalid Unicode escape");
                        sb.append((char) Integer.parseInt(new String(hex), 16));
                        break;
                    default:
                        sb.append(escaped);
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
                br.reset();
                break;
            }
            sb.append(c);
            br.mark(1);
        }
        String literal = sb.toString();

        if (literal.equals("true") || literal.equals("false") || literal.equals("null")) {
            return literal;
        }

        try {
            Double.parseDouble(literal);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid JSON literal: " + literal);
        }

        return literal;
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
