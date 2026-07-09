package com.minicache.store;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Store {

    private final ConcurrentHashMap<String, String> data;
    private TTLManager ttlManager;

    public Store() {
        this.data = new ConcurrentHashMap<>();
    }

    // Called after TTLManager is created to avoid circular dependency
    public void setTTLManager(TTLManager ttlManager) {
        this.ttlManager = ttlManager;
    }

    public void set(String key, String value) {
        data.put(key, value);
    }

    public void set(String key, String value, int ttlSeconds) {
        data.put(key, value);
        if (ttlManager != null) {
            ttlManager.setExpiry(key, ttlSeconds);
        }
    }

    public String get(String key) {
        // Check if key is expired before returning
        if (ttlManager != null && ttlManager.isExpired(key)) {
            data.remove(key);
            ttlManager.removeExpiry(key);
            return null;
        }
        return data.get(key);
    }

    public boolean delete(String key) {
        if (ttlManager != null) {
            ttlManager.removeExpiry(key);
        }
        return data.remove(key) != null;
    }

    public boolean exists(String key) {
        // Check expiry before confirming existence
        if (ttlManager != null && ttlManager.isExpired(key)) {
            data.remove(key);
            ttlManager.removeExpiry(key);
            return false;
        }
        return data.containsKey(key);
    }

    public Set<String> keys() {
        return data.keySet();
    }

    public long getRemainingTTL(String key) {
        if (ttlManager == null) return -1;
        return ttlManager.getRemainingTTL(key);
    }
}