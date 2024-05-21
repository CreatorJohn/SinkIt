package com.creatorjohn.handlers;

import com.creatorjohn.db.Database;
import com.creatorjohn.db.DatabaseHandler;
import com.creatorjohn.db.models.UserModel;
import com.creatorjohn.db.models.UserStats;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.events.LoginResponseEvent;
import com.creatorjohn.helpers.events.RegisterResponseEvent;
import com.creatorjohn.helpers.logging.MyLogger;
import com.creatorjohn.helpers.server.Game;
import com.creatorjohn.helpers.server.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Server {
    final private MyLogger logger = new MyLogger("Server");
    private ServerSocket instance;
    private Thread thread;
    final private HashMap<String, Player> players = new HashMap<>();
    final private HashMap<String, Game> games = new HashMap<>();
    final private Database<UserModel> usersDB = new DatabaseHandler<>("users.json", UserModel.class, false);

    synchronized public boolean open(int port) {
        if (instance != null) return false;

        usersDB.setUniqueKeys(List.of("username"));

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

    synchronized public Result loginPlayer(Player player, String username, String password) {
        if (player.id() != null) {
            logger.warning("User already logged in!");
            return new Result.Error("User already logged in!");
        }

        UserModel user = usersDB.get(username, "username");

        if (user == null) return new Result.Error("No user with this username!");
        else if (!Objects.equals(user.password(), password)) return new Result.Error("Incorrect password!");
        else return new Result.Success<>(user);
    }

    synchronized public Result registerPlayer(Player player, String username, String password) {
        if (player.id() != null) {
            logger.warning("User already logged in!");
            return new Result.Error("User already logged in!");
        }

        List<UserModel> users = usersDB.getAll();
        List<UserModel> matching = users
                .stream()
                .filter(it -> Objects.equals(it.username().toLowerCase(), username.toLowerCase()))
                .toList();
        UserModel toSave = new UserModel(username, password);

        if (!matching.isEmpty()) return new Result.Error("User with this username already exists!");
        else if (!usersDB.save(toSave)) return new Result.Error("Failed to register user!");
        else return new Result.Success<>(toSave);
    }

    synchronized public Result createGame(@NotNull Player host) {
        if (host.inGame()) {
            logger.warning("Player is already in game!");

            return new Result.Error("Player is already in game!");
        }

        Game created = Game.create(host, this);

        games.put(created.id(), created);

        return new Result.Success<>(created.id());
    }

    @Nullable
    synchronized public Game findGame(@NotNull String id) {
        return games.get(id);
    }

    synchronized public void deleteGame(@NotNull Game game) {
        games.remove(game.id());
    }

    synchronized public boolean joinPlayer(@NotNull Player player) {
        if (players.containsKey(player.ip())) return false;

        players.put(player.ip(), player);

        return true;
    }

    synchronized public boolean updatePlayerStats(@NotNull String id, UserStats stats) {
        UserModel saved = usersDB.get(id, "id");

        if (saved == null) return false;

        UserModel updated = new UserModel(id, saved.username(), saved.password(), stats);

        return usersDB.update(id, "id", updated);
    }

    synchronized public UserStats getPlayerStats(@NotNull String id) {
        UserModel user = usersDB.get(id, "id");

        if (user == null) return null;

        return user.stats();
    }

    synchronized public boolean disconnectPlayer(@NotNull Player player) {
        if (!players.containsKey(player.ip())) return false;

        players.remove(player.ip());
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

    public static abstract class Result {
        public static class Success<T> extends Result {
            public final T data;

            public Success(T data) {
                this.data = data;
            }
        }
        public static class Error extends Result {
            public final String error;

            public Error(String error) {
                this.error = error;
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();

        if (server.open(JConfig.serverPort)) System.out.println("Server is opened!");
    }
}
