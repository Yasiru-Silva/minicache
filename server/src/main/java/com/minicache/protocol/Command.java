package com.minicache.protocol;

import java.util.List;

public class Command {

    private final String name;
    private final List<String> args;

    public Command(String name, List<String> args) {
        this.name = name.toUpperCase();
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    // Convenience method to get a specific argument by index
    public String getArg(int index) {
        if (index >= args.size()) {
            return null;
        }
        return args.get(index);
    }

    @Override
    public String toString() {
        return "Command{name=" + name + ", args=" + args + "}";
    }
}