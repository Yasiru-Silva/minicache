package com.minicache;

import com.minicache.server.TcpServer;

public class Main {

    public static void main(String[] args) {
        // 6379 is the default Redis port — we use the same for familiarity
        TcpServer server = new TcpServer(6379);
        server.start();
    }
}