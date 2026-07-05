package com.minicache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {

    public static void main(String[] args) throws Exception {
        // Connect to the server
        Socket socket = new Socket("localhost", 6379);
        System.out.println("Connected to MiniCache server");

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send a test message
        out.println("hello minicache");
        System.out.println("Sent: hello minicache");

        // Read the response
        String response = in.readLine();
        System.out.println("Received: " + response);

        // Send another test message
        out.println("SET foo bar");
        System.out.println("Sent: SET foo bar");

        response = in.readLine();
        System.out.println("Received: " + response);

        socket.close();
        System.out.println("Connection closed");
    }
}