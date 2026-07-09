package com.minicache;

import com.minicache.persistence.AOFLoader;
import com.minicache.persistence.AOFWriter;
import com.minicache.server.CommandHandler;
import com.minicache.server.TcpServer;
import com.minicache.store.Store;
import com.minicache.store.TTLManager;

public class Main {

    private static final String AOF_FILE_PATH = "minicache.aof";

    public static void main(String[] args) {
        // Wire everything together
        Store store = new Store();
        TTLManager ttlManager = new TTLManager(store);
        store.setTTLManager(ttlManager);

        CommandHandler commandHandler = new CommandHandler(store);

        // Replay AOF log on startup to restore previous state
        AOFLoader aofLoader = new AOFLoader(AOF_FILE_PATH, commandHandler);
        aofLoader.load();

        // Start AOF writer for new commands
        AOFWriter aofWriter = new AOFWriter(AOF_FILE_PATH);
        commandHandler.setAOFWriter(aofWriter);

        // Start TCP server
        TcpServer server = new TcpServer(6379, commandHandler);
        server.start();
    }
}