package com.minicache.store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

public class Store {

    private final ConcurrentHashMap<String, String> data;

    public Store() {
        this.data = new ConcurrentHashMap<>();
    }

    public void set(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public boolean delete(String key) {
        return data.remove(key) != null;
    }

    public boolean exists(String key) {
        return data.containsKey(key);
    }

    public Set<String> keys() {
        return data.keySet();
    }
}