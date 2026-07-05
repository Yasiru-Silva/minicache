package com.minicache.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {

    private final int port;
    private final ExecutorService threadPool;

    public TcpServer(int port) {
        this.port = port;
        // Creates a pool of 10 threads to handle up to 10 simultaneous clients
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("MiniCache listening on port " + port);

            // Keep accepting new client connections forever
            while (true) {
                // Blocks here until a client connects
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Hand off the client to a thread from the pool
                threadPool.submit(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}