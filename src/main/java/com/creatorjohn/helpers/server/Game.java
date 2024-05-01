package com.creatorjohn.helpers.server;

import com.creatorjohn.handlers.Server;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.Ship;
import com.creatorjohn.helpers.json.MyGson;
import com.creatorjohn.helpers.powerups.PowerUp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Game {
    final private String id = UUID.randomUUID().toString();
    final private Hashtable<String, Player> players = new Hashtable<>();
    final private Hashtable<String, List<Ship>> ships = new Hashtable<>();
    final private Hashtable<String, List<PowerUp>> powerUps = new Hashtable<>();
    final private Hashtable<String, List<Position>> shotTiles = new Hashtable<>();
    final private ArrayList<String> initialized = new ArrayList<>(2);
    final private Server server;
    private State state = State.SETUP;

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

    @Nullable
    public Player enemy(String playerID) {
        if (playerID == null) return null;
        else if (!players.containsKey(playerID)) return null;
        else {
            List<String> ids = players
                    .keySet()
                    .stream()
                    .filter(it -> !Objects.equals(it, playerID))
                    .toList();
            if (ids.isEmpty()) return null;
            return players.get(ids.getFirst());
        }
    }

    public List<Player> players() {
        return players.values().stream().toList();
    }

    public List<Ship> ships(String playerID) {
        return ships.getOrDefault(playerID, List.of());
    }

    public List<PowerUp> powerUps(String playerID) {
        return powerUps.getOrDefault(playerID, List.of());
    }

    public List<Position> shotTiles(String playerID) {
        return shotTiles.getOrDefault(playerID, List.of());
    }

    synchronized public boolean initialize(@NotNull Player player, List<Ship> ships) {
        if (initialized.contains(player.id())) return false;
        else if (initialized.size() > 1) return false;

        initialized.add(player.id());
        this.ships.put(player.id(), ships);

        if (initialized.size() == 2) state = State.RUNNING;

        return true;
    }

    synchronized public boolean uninitialize(Player player) {
        if (state != State.SETUP) return false;
        else if (!initialized.contains(player.id())) return false;

        initialized.remove(player.id());
        this.ships.remove(player.id());

        return true;
    }

    synchronized public boolean update(Player player, List<PowerUp> powerUps, List<Position> shotTiles) {
        if (state != State.RUNNING) return false;
        else if (!this.powerUps.containsKey(player.id())) return false;
        else if (!this.shotTiles.containsKey(player.id())) return false;

        Player enemy = enemy(player.id());

        if (enemy == null) return false;

        this.powerUps.put(player.id(), powerUps);
        this.shotTiles.put(enemy.id(), shotTiles);

        return true;
    }

    synchronized public boolean addPlayer(@NotNull Player player) {
        if (players.size() > 1) return false;
        else if (players.containsKey(player.id())) return false;

        players.put(player.id(), player);

        return true;
    }

    synchronized public void removePlayer(@NotNull Player player) {
        players.remove(player.id());

        if (players.isEmpty()) server.deleteGame(this);
    }

    public String id() {
        return id;
    }

    public State state() {
        return state;
    }

    public enum State { SETUP, RUNNING, FINISHED }
}
