package com.creatorjohn.helpers.server;

import com.creatorjohn.db.models.UserModel;
import com.creatorjohn.db.models.UserStats;
import com.creatorjohn.handlers.Server;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.Ship;
import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.json.MyGson;
import com.creatorjohn.helpers.logging.MyLogger;
import com.creatorjohn.helpers.entities.PowerUp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Objects;

public class Player {
    static final private MyLogger logger = new MyLogger("Player");
    final private Server server;
    final private Socket instance;
    final private PrintWriter out;
    final private BufferedReader in;
    private final String ip;
    private boolean connected;
    private String gameID;
    private Thread thread;
    private String id;
    private String username;

    public static Player create(ServerSocket server, Server parent) {
        try {
            return new Player(server, parent);
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
            return null;
        }
    }

    private Player(ServerSocket server, Server parent) throws IOException {
        this.server = parent;
        this.instance = server.accept();
        this.out = new PrintWriter(instance.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(instance.getInputStream()));
        this.connected = true;
        this.ip = instance.getRemoteSocketAddress().toString();
        this.listen();
    }

    public String id() {
        return id;
    }

    public String ip() {
        return ip;
    }

    public boolean inGame() {
        return gameID != null;
    }

    public boolean isLogged() {
        return id != null && username != null;
    }

    private void listen() {
        thread = new Thread(() -> {
            try {
                String incoming;
                while ((incoming = in.readLine()) != null) {
                    ClientEvent event = (ClientEvent) MyGson.instance.fromJson(incoming, Event.class);
                    System.out.println("Server >> Event: " + event);

                    switch (event) {
                        case LoginEvent ev -> {
                            Server.Result result = server.loginPlayer(this, ev.username.trim(), ev.password.trim());

                            switch (result) {
                                case Server.Result.Error err -> sendEvent(new LoginResponseEvent(err.error));
                                case Server.Result.Success<?> success -> {
                                    UserModel credentials = JConfig.convert(success.data, UserModel.class);

                                    if (credentials != null) {
                                        this.id = credentials.id();
                                        this.username = credentials.username();
                                        sendEvent(new LoginResponseEvent(null));
                                    } else {
                                        sendEvent(new LoginResponseEvent("Invalid credentials!"));
                                        logger.warning("Failed to parse user credentials!");
                                    }
                                }
                                case null, default -> logger.warning("Unknown result!");
                            }
                        }
                        case RegisterEvent ev -> {
                            Server.Result result = server.registerPlayer(this, ev.username.trim(), ev.password.trim());

                            switch (result) {
                                case Server.Result.Error err -> sendEvent(new RegisterResponseEvent(err.error));
                                case Server.Result.Success<?> success -> {
                                    UserModel credentials = JConfig.convert(success.data, UserModel.class);

                                    if (credentials != null) {
                                        this.id = credentials.id();
                                        this.username = credentials.username();
                                        sendEvent(new RegisterResponseEvent(null));
                                    } else {
                                        sendEvent(new RegisterResponseEvent("Invalid credentials!"));
                                        logger.warning("Failed to parse user credentials!");
                                    }
                                }
                                case null, default -> logger.severe("Unknown result!");
                            }
                        }
                        case CreateGameEvent ignored -> {
                            Server.Result result = server.createGame(this);

                            switch (result) {
                                case Server.Result.Error ignored1 -> sendEvent(new GameCreatedEvent(""));
                                case Server.Result.Success<?> success -> {
                                    this.gameID = JConfig.convert(success.data, String.class);

                                    sendEvent(new GameCreatedEvent(gameID));
                                }
                                case null, default -> logger.severe("Unknown event!");
                            }
                        }
                        case JoinGameEvent ev -> {
                            if (!isLogged()) {
                                logger.warning("Player is not logged in!");
                                sendEvent(new GameJoinedEvent(false));
                                break;
                            } else if (inGame()) {
                                logger.warning("Player is already in game!");
                                sendEvent(new GameJoinedEvent(false));
                                break;
                            } else if (ev.gameID == null) {
                                logger.severe("Invalid gameID!");
                                sendEvent(new GameJoinedEvent(false));
                                break;
                            }

                            Game game = server.findGame(ev.gameID);

                            if (game == null) {
                                logger.severe("Game doesn't exist!");
                                sendEvent(new GameJoinedEvent(false));
                                break;
                            }

                            if (game.addPlayer(this)) this.gameID = game.id();
                            else {
                                sendEvent(new GameJoinedEvent(false));
                                return;
                            }

                            Player enemy = game.enemy(id());

                            if (enemy == null) {
                                logger.severe("Enemy not found!");
                                sendEvent(new GameJoinedEvent(false));
                                break;
                            }

                            enemy.sendEvent(new PlayerJoinedEvent(username));
                            logger.info("Player " + id() + " joined the game!");

                            List<Ship> myShips = game.ships(id);
                            List<Ship> enemyShips = game.ships(enemy.id);

                            sendEvent(new GameJoinedEvent(new GameJoinedEvent.ShipInfo(myShips, enemyShips), true));

                            if (game.state() == Game.State.RUNNING) {
                                String currentPlayer = game.currentPlayer().username;
                                GameUpdatedEvent.PowerUpsInfo pInfo = new GameUpdatedEvent.PowerUpsInfo(
                                        game.powerUps(id),
                                        game.powerUps(enemy.id)
                                );
                                GameUpdatedEvent.ShotTilesInfo sInfo = new GameUpdatedEvent.ShotTilesInfo(
                                        game.shotTiles(id),
                                        game.shotTiles(enemy.id)
                                );
                                sendEvent(new GameUpdatedEvent(currentPlayer, pInfo, sInfo, List.of(), true));
                            }
                        }
                        case LogoutEvent ignored -> {
                            if (server.disconnectPlayer(this)) System.out.println("Player disconnected!");
                        }
                        case DisconnectEvent ignored -> {
                            if (!isLogged()) {
                                logger.warning("Player is not logged in!");
                                break;
                            } else if (!inGame()) {
                                logger.warning("Player is not in game!");
                                break;
                            }

                            Game game = server.findGame(this.gameID);

                            if (game == null) {
                                logger.severe("Game not found!");
                                break;
                            }

                            Player enemy = game.enemy(id());
                            game.removePlayer(this);
                            this.gameID = null;

                            if (game.state() == Game.State.SETUP) game.uninitialize(this);
                            if (enemy != null) enemy.sendEvent(new PlayerLeftEvent());
                        }
                        case InitializeGameEvent ev -> {
                            if (!isLogged()) {
                                logger.warning("Player is not logged in!");
                                sendEvent(new GameInitializedEvent(false));
                                break;
                            } else if (!inGame()) {
                                logger.warning("Player is not in game!");
                                sendEvent(new GameInitializedEvent(false));
                                break;
                            }

                            Game game = server.findGame(this.gameID);

                            if (game == null) {
                                logger.severe("Game not found!");
                                sendEvent(new GameInitializedEvent(false));
                                break;
                            } else if (!game.initialize(this, ev.ships)) {
                                logger.severe("Failed to initialize game: " + game.id());
                                sendEvent(new GameInitializedEvent(false));
                                break;
                            } else sendEvent(new GameInitializedEvent(true));

                            Player enemy;
                            if (game.state() == Game.State.RUNNING && (enemy = game.enemy(id)) != null) {
                                System.out.println("Game running and enemy found!");
                                List<Ship> myShips = game.ships(id);
                                List<Ship> enemyShips = game.ships(enemy.id);

                                sendEvent(new GameUpdatedEvent(game.currentPlayer().username,
                                        new GameUpdatedEvent.PowerUpsInfo(),
                                        new GameUpdatedEvent.ShotTilesInfo(),
                                        enemyShips,
                                        true));
                                enemy.sendEvent(new GameUpdatedEvent(game.currentPlayer().username,
                                        new GameUpdatedEvent.PowerUpsInfo(),
                                        new GameUpdatedEvent.ShotTilesInfo(),
                                        myShips,
                                        true));
                            }
                        }
                        case UpdateGameEvent ev -> {
                            if (!isLogged()) {
                                logger.warning("Player is not logged in!");
                                sendEvent(new GameUpdatedEvent(username, new GameUpdatedEvent.PowerUpsInfo(), new GameUpdatedEvent.ShotTilesInfo(), List.of(), false));
                                break;
                            }
                            if (!inGame()) {
                                logger.warning("Player is not in game!");
                                sendEvent(new GameUpdatedEvent(username, new GameUpdatedEvent.PowerUpsInfo(), new GameUpdatedEvent.ShotTilesInfo(), List.of(), false));
                                break;
                            }

                            Game game = server.findGame(this.gameID);

                            if (game == null) {
                                logger.severe("Game not found!");
                                sendEvent(new GameUpdatedEvent(username, new GameUpdatedEvent.PowerUpsInfo(), new GameUpdatedEvent.ShotTilesInfo(), List.of(), false));
                            } else if (!Objects.equals(id, game.currentPlayer().id)) {
                                logger.warning("Not your turn!");
                                sendEvent(new GameUpdatedEvent(game.currentPlayer().username, new GameUpdatedEvent.PowerUpsInfo(), new GameUpdatedEvent.ShotTilesInfo(), List.of(), false));
                            } else if (game.enemy(id) == null) {
                                logger.severe("Enemy not found!");
                                sendEvent(new GameUpdatedEvent(username, new GameUpdatedEvent.PowerUpsInfo(), new GameUpdatedEvent.ShotTilesInfo(), List.of(), false));
                            } else if (!game.update(this, ev.usedPowerUps, ev.tilesShot)) {
                                logger.severe("Game not updated!");
                                sendEvent(new GameUpdatedEvent(username, new GameUpdatedEvent.PowerUpsInfo(), new GameUpdatedEvent.ShotTilesInfo(), List.of(), false));
                            } else {
                                Player enemy = game.enemy(id());

                                game.updateCurrentPlayer();
                                GameUpdatedEvent.PowerUpsInfo pInfo = new GameUpdatedEvent.PowerUpsInfo(List.of(), ev.usedPowerUps);
                                GameUpdatedEvent.ShotTilesInfo sInfo = new GameUpdatedEvent.ShotTilesInfo(List.of(), ev.tilesShot);
                                enemy.sendEvent(new GameUpdatedEvent(game.currentPlayer().username, pInfo, sInfo, List.of(), true));

                                if (ev.gameOver) {
                                    sendEvent(new GameFinishedEvent(GameFinishedEvent.Status.WINNER));
                                    enemy.sendEvent(new GameFinishedEvent(GameFinishedEvent.Status.LOOSER));

                                    UserStats playerStats = server.getPlayerStats(id);
                                    UserStats enemyStats = server.getPlayerStats(enemy.id);

                                    if (playerStats != null) {
                                        playerStats.updateGamesWonBy(1);
                                        if (server.updatePlayerStats(id, playerStats))
                                            logger.info("Player stats successfully updated!");
                                    }

                                    if (enemyStats != null) {
                                        enemyStats.updateGamesLostBy(1);
                                        if (server.updatePlayerStats(enemy.id, enemyStats))
                                            logger.info("Enemy stats successfully updated!");
                                    }
                                }
                            }
                        }
                        case StatisticsRequestEvent ignored -> {
                            if (!isLogged()) {
                                logger.warning("Player is not logged in!");
                                sendEvent(new StatisticsResponseEvent(null));
                                break;
                            }

                            sendEvent(new StatisticsResponseEvent(server.getPlayerStats(id)));
                        }
                        case null, default -> logger.warning("Unknown event!");
                    }
                }
            } catch (SocketException e) {
                logger.warning(e.getLocalizedMessage());
                if (server.disconnectPlayer(this)) System.out.println("Player is disconnected!");;
            } catch (IOException e) {
                logger.severe(e.getLocalizedMessage());
                if (server.disconnectPlayer(this)) System.out.println("Player is disconnected!");;
            }
        });
        thread.start();
    }

    private void sendEvent(ServerEvent event) {
        out.println(MyGson.instance.toJson(event));
    }

    public void disconnect() {
        if (!connected) return;

        try {
            this.instance.close();
            this.thread.interrupt();
            this.connected = false;
            this.gameID = null;
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
        }
    }
}
