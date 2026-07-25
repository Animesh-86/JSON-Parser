package com.jsonparser.streaming;

import java.io.IOException;
import java.io.Reader;

/**
 * A high-performance, zero-allocation JSON streaming parser.
 * Instead of allocating Strings for every token, it provides raw offsets 
 * into a shared character buffer, drastically reducing Garbage Collection overhead.
 */
public class ZeroAllocStreamParser implements AutoCloseable {

    public interface ZeroAllocHandler {
        void handle(JsonEventType type, char[] buffer, int start, int length);
    }

    private final Reader reader;
    private final ZeroAllocHandler handler;
    private final char[] buffer;
    private int pos = 0;
    private int limit = 0;
    
    private static final int BUFFER_SIZE = 8192;

    public ZeroAllocStreamParser(Reader reader, ZeroAllocHandler handler) {
        this.reader = reader;
        this.handler = handler;
        this.buffer = new char[BUFFER_SIZE];
    }

    public void parse() throws IOException {
        while (true) {
            skipWhitespace();
            if (pos >= limit && !fillBuffer()) {
                break;
            }

            char c = buffer[pos];
            if (c == '{') {
                handler.handle(JsonEventType.START_OBJECT, null, 0, 0);
                pos++;
            } else if (c == '}') {
                handler.handle(JsonEventType.END_OBJECT, null, 0, 0);
                pos++;
            } else if (c == '[') {
                handler.handle(JsonEventType.START_ARRAY, null, 0, 0);
                pos++;
            } else if (c == ']') {
                handler.handle(JsonEventType.END_ARRAY, null, 0, 0);
                pos++;
            } else if (c == ':') {
                pos++;
            } else if (c == ',') {
                pos++;
            } else if (c == '"') {
                parseString();
            } else if (c == '-' || Character.isDigit(c)) {
                parseNumber();
            } else if (c == 't' || c == 'f' || c == 'n') {
                parseLiteral();
            } else {
                throw new RuntimeException("Unexpected char: " + c);
            }
        }
    }

    private void parseString() throws IOException {
        pos++; // skip "
        int start = pos;
        while (true) {
            if (pos >= limit && !fillBuffer()) {
                throw new RuntimeException("Unterminated string");
            }
            if (buffer[pos] == '"') {
                break;
            }
            if (buffer[pos] == '\\') {
                pos++; // skip escape
            }
            pos++;
        }
        handler.handle(JsonEventType.VALUE, buffer, start, pos - start);
        pos++; // skip "
    }

    private void parseNumber() throws IOException {
        int start = pos;
        while (true) {
            if (pos >= limit && !fillBuffer()) break;
            char c = buffer[pos];
            if (Character.isDigit(c) || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E') {
                pos++;
            } else {
                break;
            }
        }
        handler.handle(JsonEventType.VALUE, buffer, start, pos - start);
    }

    private void parseLiteral() throws IOException {
        int start = pos;
        while (true) {
            if (pos >= limit && !fillBuffer()) break;
            char c = buffer[pos];
            if (Character.isLetter(c)) {
                pos++;
            } else {
                break;
            }
        }
        handler.handle(JsonEventType.VALUE, buffer, start, pos - start);
    }

    private void skipWhitespace() throws IOException {
        while (true) {
            if (pos >= limit && !fillBuffer()) {
                return;
            }
            if (Character.isWhitespace(buffer[pos])) {
                pos++;
            } else {
                break;
            }
        }
    }

    private boolean fillBuffer() throws IOException {
        if (pos < limit) {
            // shift remaining
            System.arraycopy(buffer, pos, buffer, 0, limit - pos);
            limit -= pos;
            pos = 0;
        } else {
            pos = 0;
            limit = 0;
        }

        int read = reader.read(buffer, limit, buffer.length - limit);
        if (read == -1) {
            return false;
        }
        limit += read;
        return true;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
