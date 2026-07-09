package com.minicache.server;

import com.minicache.protocol.Command;
import com.minicache.protocol.ProtocolParser;
import com.minicache.store.Store;

import java.util.Set;

public class CommandHandler {

    private final Store store;
    private final ProtocolParser parser;

    public CommandHandler(Store store) {
        this.store = store;
        this.parser = new ProtocolParser();
    }

    public String handle(String rawInput) {
        Command command = parser.parse(rawInput);

        if (command == null) {
            return "ERROR unknown command";
        }

        switch (command.getName()) {
            case "PING":
                return "PONG";

            case "SET":
                // Check if EX argument is present
                if (command.getArgs().size() == 4 &&
                    command.getArg(2).equalsIgnoreCase("EX")) {
                    try {
                        int ttlSeconds = Integer.parseInt(command.getArg(3));
                        store.set(command.getArg(0), command.getArg(1), ttlSeconds);
                        return "OK";
                    } catch (NumberFormatException e) {
                        return "ERROR EX value must be an integer";
                    }
                }
                store.set(command.getArg(0), command.getArg(1));
                return "OK";

            case "GET":
                String value = store.get(command.getArg(0));
                return value != null ? value : "NULL";

            case "DEL":
                return store.delete(command.getArg(0)) ? "1" : "0";

            case "EXISTS":
                return store.exists(command.getArg(0)) ? "1" : "0";

            case "KEYS":
                Set<String> keys = store.keys();
                return keys.isEmpty() ? "(empty)" : String.join(", ", keys);

            default:
                return "ERROR unknown command";
        }
    }
}