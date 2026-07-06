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

        // Test PING
        sendAndReceive(out, in, "PING");

        // Test SET
        sendAndReceive(out, in, "SET name john");
        sendAndReceive(out, in, "SET age 25");

        // Test GET
        sendAndReceive(out, in, "GET name");
        sendAndReceive(out, in, "GET age");
        sendAndReceive(out, in, "GET unknown");

        // Test EXISTS
        sendAndReceive(out, in, "EXISTS name");
        sendAndReceive(out, in, "EXISTS unknown");

        // Test KEYS
        sendAndReceive(out, in, "KEYS");

        // Test DEL
        sendAndReceive(out, in, "DEL name");
        sendAndReceive(out, in, "GET name");
        sendAndReceive(out, in, "KEYS");

        // Test invalid command
        sendAndReceive(out, in, "INVALID foo");

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