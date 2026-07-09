package com.minicache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 6379);
        System.out.println("Connected to MiniCache server");

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Test basic SET and GET still works
        sendAndReceive(out, in, "SET name john");
        sendAndReceive(out, in, "GET name");

        // Test SET with TTL (expires in 3 seconds)
        sendAndReceive(out, in, "SET session abc123 EX 3");
        sendAndReceive(out, in, "GET session");
        sendAndReceive(out, in, "EXISTS session");

        // Wait 4 seconds for key to expire
        System.out.println("--- Waiting 4 seconds for TTL to expire ---");
        Thread.sleep(4000);

        // Key should be gone now
        sendAndReceive(out, in, "GET session");
        sendAndReceive(out, in, "EXISTS session");

        // name should still be there (no TTL)
        sendAndReceive(out, in, "GET name");

        socket.close();
        System.out.println("Connection closed");
    }

    private static void sendAndReceive(PrintWriter out, BufferedReader in, String command) throws Exception {
        out.println(command);
        System.out.println("Sent:     " + command);
        System.out.println("Received: " + in.readLine());
        System.out.println("---");
    }
}