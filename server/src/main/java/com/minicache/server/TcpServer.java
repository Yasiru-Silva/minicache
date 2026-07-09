package com.minicache.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {

    private final int port;
    private final ExecutorService threadPool;
    private final CommandHandler commandHandler;

    public TcpServer(int port, CommandHandler commandHandler) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
        this.commandHandler = commandHandler;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("MiniCache listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                threadPool.submit(new ClientHandler(clientSocket, commandHandler));
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}