package com.minicache.http;

import com.minicache.store.Store;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MiniCacheHttpServer {

    private final int port;
    private final Store store;

    public MiniCacheHttpServer(int port, Store store) {
        this.port = port;
        this.store = store;
    }

    public void start() {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);

            // Register endpoints
            httpServer.createContext("/stats", new StatsHandler(store));
            httpServer.createContext("/keys", new KeysHandler(store));

            httpServer.setExecutor(null);
            httpServer.start();

            System.out.println("HTTP server listening on port " + port);

        } catch (IOException e) {
            System.err.println("HTTP server error: " + e.getMessage());
        }
    }
}