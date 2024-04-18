package com.creatorjohn.helpers.server;

import com.creatorjohn.handlers.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

public class Game {
    final private String id = UUID.randomUUID().toString();
    final private Hashtable<String, Player> players = new Hashtable<>();
    final private Server server;

    public static Game create(@NotNull Player host, @NotNull Server parent) {
        Game game = new Game(parent);

        game.addPlayer(host);

        return game;
    }

    private Game(Server parent) {
        this.server = parent;
    }

    @Nullable
    public Player player(String id) {
        return id == null ? null : players.get(id);
    }

    public List<Player> players() {
        return players.values().stream().toList();
    }

    public boolean initialize(@NotNull Player player) {


        return true;
    }

    public boolean addPlayer(@NotNull Player player) {
        if (players.size() > 1) return false;
        else if (players.containsKey(player.id())) return false;

        players.put(player.id(), player);

        return true;
    }

    public boolean removePlayer(@NotNull Player player) {
        if (players.isEmpty()) return false;
        else if (!players.containsKey(player.id())) return false;

        players.remove(player.id());

        if (players.isEmpty()) server.deleteGame(this);

        return true;
    }

    public String id() {
        return id;
    }
}
