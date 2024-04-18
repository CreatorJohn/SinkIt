package com.creatorjohn.handlers;

import com.creatorjohn.helpers.server.Game;
import com.creatorjohn.helpers.server.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private ServerSocket instance;
    private Thread thread;
    final private HashMap<String, Player> players = new HashMap<>();
    final private HashMap<String, Game> games = new HashMap<>();

    synchronized public boolean open(int port) {
        if (instance != null) return false;

        try {
            this.instance = new ServerSocket(port);
            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Player player = new Player(instance);

                        if (!joinPlayer(player)) System.err.println("Server >> User is already connected!");
                        else System.out.println("Server >> User " + player.id() + " connected!");
                    } catch (Exception e) {
                        System.err.println("Server >> " + e.getLocalizedMessage());
                    }
                }
            });
            thread.start();

            return true;
        } catch (IOException e) {
            System.err.println("Server >> " + e.getLocalizedMessage());
            return false;
        }
    }

    public boolean joinPlayer(Player player) {
        if (players.containsKey(player.id())) return false;

        players.put(player.id(), player);

        return true;
    }

    public boolean disconnectPlayer(Player player) {
        if (!players.containsKey(player.id())) return false;

        players.remove(player.id());

        return true;
    }

    synchronized public boolean close() {
        if (instance == null) return false;

        try {
            thread.interrupt();
            instance.close();
            instance = null;
            thread = null;

            return true;
        } catch (IOException e) {
            System.err.println("Server >> " + e.getLocalizedMessage());
            return false;
        }
    }
}
