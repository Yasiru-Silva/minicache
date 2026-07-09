package com.minicache.http;

import com.minicache.store.Store;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class KeysHandler implements HttpHandler {

    private final Store store;

    public KeysHandler(Store store) {
        this.store = store;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Only allow GET requests
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // Add CORS header so React dashboard can call this
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        Set<String> keys = store.keys();

        StringBuilder json = new StringBuilder("[");
        int i = 0;
        for (String key : keys) {
            long ttl = store.getRemainingTTL(key);
            json.append("{")
                .append("\"key\":\"").append(key).append("\",")
                .append("\"ttl\":").append(ttl)
                .append("}");
            if (i < keys.size() - 1) {
                json.append(",");
            }
            i++;
        }
        json.append("]");

        byte[] response = json.toString().getBytes();
        exchange.sendResponseHeaders(200, response.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(response);
        }
    }
}