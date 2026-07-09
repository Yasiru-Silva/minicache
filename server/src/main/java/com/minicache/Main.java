package com.minicache;

import com.minicache.http.MiniCacheHttpServer;
import com.minicache.persistence.AOFLoader;
import com.minicache.persistence.AOFWriter;
import com.minicache.server.CommandHandler;
import com.minicache.server.TcpServer;
import com.minicache.store.Store;
import com.minicache.store.TTLManager;

public class Main {

    private static final String AOF_FILE_PATH = "minicache.aof";
    private static final int TCP_PORT = 6379;
    private static final int HTTP_PORT = 8081;

    public static void main(String[] args) {
        // Wire store and TTL together
        Store store = new Store();
        TTLManager ttlManager = new TTLManager(store);
        store.setTTLManager(ttlManager);

        // Set up command handler
        CommandHandler commandHandler = new CommandHandler(store);

        // Replay AOF log on startup
        AOFLoader aofLoader = new AOFLoader(AOF_FILE_PATH, commandHandler);
        aofLoader.load();

        // Start AOF writer for new commands
        AOFWriter aofWriter = new AOFWriter(AOF_FILE_PATH);
        commandHandler.setAOFWriter(aofWriter);

        // Start HTTP server in a separate thread so it doesn't block TCP
        MiniCacheHttpServer httpServer = new MiniCacheHttpServer(HTTP_PORT, store);
        Thread httpThread = new Thread(httpServer::start);
        httpThread.setDaemon(true);
        httpThread.start();

        // Start TCP server (blocking - runs forever)
        TcpServer tcpServer = new TcpServer(TCP_PORT, commandHandler);
        tcpServer.start();
    }
}