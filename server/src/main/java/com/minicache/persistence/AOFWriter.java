package com.minicache.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AOFWriter {

    private final String filePath;
    private BufferedWriter writer;

    public AOFWriter(String filePath) {
        this.filePath = filePath;
        try {
            // true = append mode, don't overwrite existing log
            this.writer = new BufferedWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            System.err.println("AOF error opening file: " + e.getMessage());
        }
    }

    // Append a command to the log file
    public synchronized void log(String command) {
        try {
            writer.write(command);
            writer.newLine();
            // Flush immediately so nothing is lost on crash
            writer.flush();
        } catch (IOException e) {
            System.err.println("AOF error writing command: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("AOF error closing file: " + e.getMessage());
        }
    }
}