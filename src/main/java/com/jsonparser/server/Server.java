package com.jsonparser.server;

import com.jsonparser.core.JsonObject;
import com.jsonparser.core.JsonValue;
import com.jsonparser.core.Parser;
import com.jsonparser.diff.JsonDiff;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/health", exchange -> {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            sendResponse(exchange, 200, "{\"status\":\"UP\",\"message\":\"JSON Parser API Service Ready\"}");
        });

        server.createContext("/api/parse", new ParseHandler());
        server.createContext("/api/diff", new DiffHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        System.out.println("JSON Parser HTTP Server started on port " + port);
        server.start();
    }

    private static class ParseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 455, "{\"error\":\"Only POST method is supported\"}");
                return;
            }

            try {
                String requestBody = readStream(exchange.getRequestBody());
                Parser parser = new Parser(requestBody, com.jsonparser.core.ParserConfig.json5());
                JsonValue parsed = parser.parse();
                sendResponse(exchange, 200, parsed.toJson(2));
            } catch (Exception e) {
                String errJson = "{\"error\":\"Parse Error: " + escapeJson(e.getMessage()) + "\"}";
                sendResponse(exchange, 400, errJson);
            }
        }
    }

    private static class DiffHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 455, "{\"error\":\"Only POST method is supported\"}");
                return;
            }

            try {
                String requestBody = readStream(exchange.getRequestBody());
                Parser parser = new Parser(requestBody);
                JsonObject root = (JsonObject) parser.parse();

                JsonValue oldVal = root.get("old");
                JsonValue newVal = root.get("new");

                if (oldVal == null || newVal == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Request body must contain 'old' and 'new' JSON objects\"}");
                    return;
                }

                JsonDiff.DiffResult diffResult = JsonDiff.diff(oldVal, newVal);
                sendResponse(exchange, 200, "{\"diff\":\"" + escapeJson(diffResult.toPrettyString()) + "\"}");
            } catch (Exception e) {
                String errJson = "{\"error\":\"Diff Error: " + escapeJson(e.getMessage()) + "\"}";
                sendResponse(exchange, 400, errJson);
            }
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readStream(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }

    private static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
