package com.creatorjohn.helpers;

public class JLock {
    private boolean running = false;

    synchronized public void lock() {
        this.running = true;
    }

    synchronized public void unlock() {
        this.running = false;
    }

    synchronized public boolean locked() {
        return running;
    }
}
