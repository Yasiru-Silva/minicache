package com.minicache.http;

import com.minicache.store.Store;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class StatsHandler implements HttpHandler {

    private final Store store;
    private final long startTime;

    public StatsHandler(Store store) {
        this.store = store;
        this.startTime = System.currentTimeMillis();
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

        long uptimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
        int keyCount = store.keys().size();

        String json = "{"
                + "\"keyCount\":" + keyCount + ","
                + "\"uptimeSeconds\":" + uptimeSeconds
                + "}";

        byte[] response = json.getBytes();
        exchange.sendResponseHeaders(200, response.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(response);
        }
    }
}