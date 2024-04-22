package com.creatorjohn.helpers.server;

import com.creatorjohn.handlers.Server;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.Ship;
import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.json.MyGson;
import com.creatorjohn.helpers.logging.MyLogger;
import com.creatorjohn.helpers.powerups.PowerUp;

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
    final private String id;
    final private Server server;
    final private Socket instance;
    final private PrintWriter out;
    final private BufferedReader in;
    private boolean connected;
    private String gameID;
    private Thread thread;

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
        this.id = instance.getRemoteSocketAddress().toString();
        this.connected = true;
        this.listen();
    }

    public String id() {
        return id;
    }

    private void listen() {
        thread = new Thread(() -> {
            try {
                String incoming;
                while ((incoming = in.readLine()) != null) {
                    ClientEvent event = (ClientEvent) MyGson.instance.fromJson(incoming, Event.class);
                    System.out.println("Server >> Event: " + event);

                    switch (event) {
                        case CreateGameEvent ignored -> {
                            if (this.gameID != null) {
                                logger.warning("Player is already in game!");
                                break;
                            }

                            String gameID = server.createGame(this);
                            this.gameID = gameID;
                            sendEvent(new GameCreatedEvent(gameID));
                        }
                        case JoinGameEvent ev -> {
                            if (this.gameID != null) {
                                logger.warning("Player is already in game!");
                                break;
                            } else if (ev.gameID == null) {
                                logger.severe("Invalid gameID!");
                                break;
                            }

                            Game game = server.findGame(ev.gameID);

                            if (game == null) {
                                logger.severe("Game doesn't exist!");
                                break;
                            }

                            if (game.addPlayer(this)) this.gameID = game.id();
                            else return;

                            Player enemy = game.enemy(id());

                            if (enemy == null) {
                                logger.severe("Enemy not found!");
                                break;
                            }

                            enemy.sendEvent(new PlayerJoinedEvent());
                            System.out.println("Player " + id() + " joined the game!");

                            List<Ship> loadedShips = game.ships(id());
                            List<PowerUp> loadedPowerUps = game.powerUps(id());
                            List<Position> loadedShotTiles = game.shotTiles(id());
                            List<Position> loadedRevealedTiles = game.shotTiles(enemy.id());
                            sendEvent(new GameJoinedEvent(loadedShips, loadedPowerUps, loadedShotTiles, loadedRevealedTiles));
                        }
                        case DisconnectEvent ignored -> {
                            if (this.gameID == null) {
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
                            if (server.disconnectPlayer(this)) System.out.println("Player " + id() + " disconnected!");
                        }
                        case InitializeGameEvent ev -> {
                            if (this.gameID == null) {
                                logger.warning("Player is not in game!");
                                break;
                            }

                            Game game = server.findGame(this.gameID);

                            if (game == null) logger.severe("Game not found!");
                            else if (!game.initialize(this, ev.ships))
                                logger.severe("Failed to initialize game: " + game.id());
                        }
                        case UpdateGameEvent ev -> {
                            if (this.gameID == null) {
                                logger.warning("Player is not in game!");
                                break;
                            }

                            Game game = server.findGame(this.gameID);

                            if (game == null) {
                                logger.severe("Game not found!");
                            } else if (!game.update(this, ev.usedPowerUps, ev.tilesShot))
                                logger.severe("Game not updated!");
                        }
                        case null, default -> logger.warning("Unknown event!");
                    }
                }
            } catch (SocketException e) {
                logger.warning(e.getLocalizedMessage());
            } catch (IOException e) {
                logger.severe(e.getLocalizedMessage());
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
            printError(e);
        }
    }

    private void printError(Exception e) {
        System.out.println("Server client >> " + e.getLocalizedMessage());
    }
}
