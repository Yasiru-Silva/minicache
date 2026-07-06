package com.minicache.server;

import com.minicache.store.Store;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {

    private final int port;
    private final ExecutorService threadPool;
    private final CommandHandler commandHandler;

    public TcpServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
        // Single shared store and command handler across all client threads
        Store store = new Store();
        this.commandHandler = new CommandHandler(store);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("MiniCache listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                // Pass the shared commandHandler to each client thread
                threadPool.submit(new ClientHandler(clientSocket, commandHandler));
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}