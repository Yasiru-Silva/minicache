package com.minicache.protocol;

import java.util.Arrays;
import java.util.List;

public class ProtocolParser {

    // Takes a raw line from the client and returns a Command object
    public Command parse(String rawInput) {
        // Handle null or empty input
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return null;
        }

        // Split the input by spaces
        String[] parts = rawInput.trim().split("\\s+");

        // First part is always the command name
        String commandName = parts[0].toUpperCase();

        // Validate the command is one we support
        if (!isValidCommand(commandName)) {
            return null;
        }

        // Remaining parts are the arguments
        List<String> args = Arrays.asList(parts).subList(1, parts.length);

        // Validate argument count per command
        if (!hasValidArgCount(commandName, args)) {
            return null;
        }

        return new Command(commandName, args);
    }

    // Check if command name is one we support
    private boolean isValidCommand(String commandName) {
        switch (commandName) {
            case "SET":
            case "GET":
            case "DEL":
            case "EXISTS":
            case "KEYS":
            case "PING":
                return true;
            default:
                return false;
        }
    }

    // Check if the correct number of arguments were provided
    private boolean hasValidArgCount(String commandName, List<String> args) {
        switch (commandName) {
            case "SET":
                // SET key value  OR  SET key value EX 30
                return args.size() == 2 || args.size() == 4;
            case "GET":
            case "DEL":
            case "EXISTS":
                // All need exactly one argument: the key
                return args.size() == 1;
            case "KEYS":
            case "PING":
                // No arguments needed
                return args.size() == 0;
            default:
                return false;
        }
    }
}