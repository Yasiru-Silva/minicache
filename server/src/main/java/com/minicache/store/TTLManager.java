package com.minicache.store;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class TTLManager {

    // Represents a single expiry entry in the heap
    private static class ExpiryEntry implements Comparable<ExpiryEntry> {
        String key;
        long expiryTime; // milliseconds since epoch

        ExpiryEntry(String key, long expiryTime) {
            this.key = key;
            this.expiryTime = expiryTime;
        }

        @Override
        public int compareTo(ExpiryEntry other) {
            // Min-heap: soonest expiry at the top
            return Long.compare(this.expiryTime, other.expiryTime);
        }
    }

    // Min-heap of expiry entries
    private final PriorityQueue<ExpiryEntry> expiryHeap;
    // Maps key to its expiry time for quick lookup
    private final ConcurrentHashMap<String, Long> expiryMap;
    // Reference to the main store so we can delete expired keys
    private final Store store;

    public TTLManager(Store store) {
        this.store = store;
        this.expiryHeap = new PriorityQueue<>();
        this.expiryMap = new ConcurrentHashMap<>();

        // Start background sweep thread
        startSweepThread();
    }

    // Register a key with an expiry time in seconds
    public void setExpiry(String key, int seconds) {
        long expiryTime = System.currentTimeMillis() + (seconds * 1000L);
        expiryMap.put(key, expiryTime);
        synchronized (expiryHeap) {
            expiryHeap.offer(new ExpiryEntry(key, expiryTime));
        }
    }

    // Check if a key is expired right now
    public boolean isExpired(String key) {
        Long expiryTime = expiryMap.get(key);
        if (expiryTime == null) return false;
        return System.currentTimeMillis() > expiryTime;
    }

    // Remove TTL tracking for a key (e.g. when key is deleted manually)
    public void removeExpiry(String key) {
        expiryMap.remove(key);
    }

    // Get remaining TTL in seconds for a key
    public long getRemainingTTL(String key) {
        Long expiryTime = expiryMap.get(key);
        if (expiryTime == null) return -1;
        long remaining = (expiryTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    // Background thread that sweeps expired keys every 100ms
    private void startSweepThread() {
        Thread sweepThread = new Thread(() -> {
            while (true) {
                try {
                    sweepExpiredKeys();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        // Daemon thread dies automatically when main program exits
        sweepThread.setDaemon(true);
        sweepThread.start();
    }

    private void sweepExpiredKeys() {
        long now = System.currentTimeMillis();
        synchronized (expiryHeap) {
            while (!expiryHeap.isEmpty()) {
                ExpiryEntry top = expiryHeap.peek();
                if (top.expiryTime > now) {
                    // Top hasn't expired yet, nothing else has either
                    break;
                }
                expiryHeap.poll();
                // Only delete if the expiry time matches (key may have been updated)
                Long currentExpiry = expiryMap.get(top.key);
                if (currentExpiry != null && currentExpiry <= now) {
                    store.delete(top.key);
                    expiryMap.remove(top.key);
                    System.out.println("TTL expired: " + top.key);
                }
            }
        }
    }
}