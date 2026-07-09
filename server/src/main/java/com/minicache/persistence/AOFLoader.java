package com.minicache.persistence;

import com.minicache.server.CommandHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AOFLoader {

    private final String filePath;
    private final CommandHandler commandHandler;

    public AOFLoader(String filePath, CommandHandler commandHandler) {
        this.filePath = filePath;
        this.commandHandler = commandHandler;
    }

    // Replay all commands from the AOF log to rebuild in-memory state
    public void load() {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("No AOF file found, starting fresh");
            return;
        }

        System.out.println("Replaying AOF log from: " + filePath);
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    commandHandler.handle(line);
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("AOF error reading file: " + e.getMessage());
        }

        System.out.println("AOF replay complete: " + count + " commands restored");
    }
}