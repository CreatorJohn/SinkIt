package com.creatorjohn.handlers;

import com.creatorjohn.helpers.logging.MyLogger;
import com.creatorjohn.helpers.server.Game;
import com.creatorjohn.helpers.server.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    final private MyLogger logger = new MyLogger("Server");
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
                        Player player = Player.create(instance, this);

                        if (instance == null) logger.info("Server closed!");
                        else if (player == null) logger.severe("Failed to connect user!");
                        else if (!joinPlayer(player)) logger.severe("User is already connected!");
                        else logger.fine("User " + player.id() + " connected!");
                    } catch (Exception e) {
                        logger.severe(e.getLocalizedMessage());
                    }
                }
            });
            thread.start();

            return true;
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
            return false;
        }
    }

    synchronized public String createGame(@NotNull Player host) {
        Game created = Game.create(host, this);

        games.put(created.id(), created);

        return created.id();
    }

    @Nullable
    synchronized public Game findGame(@NotNull String id) {
        return games.get(id);
    }

    synchronized public void deleteGame(@NotNull Game game) {
        games.remove(game.id());
    }

    synchronized public boolean joinPlayer(@NotNull Player player) {
        if (players.containsKey(player.id())) return false;

        players.put(player.id(), player);

        return true;
    }

    synchronized public boolean disconnectPlayer(@NotNull Player player) {
        if (!players.containsKey(player.id())) return false;

        players.remove(player.id());
        player.disconnect();

        return true;
    }

    synchronized public void close() {
        if (instance == null) return;

        try {
            thread.interrupt();
            instance.close();
            instance = null;
            thread = null;
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
        }
    }
}
