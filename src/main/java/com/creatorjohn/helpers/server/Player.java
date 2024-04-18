package com.creatorjohn.helpers.server;

import com.creatorjohn.handlers.Server;
import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.json.MyGson;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Player {
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
            System.err.println("Player >> " + e.getLocalizedMessage());
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
    }

    public String id() {
        return id;
    }

    public boolean joinGame(String gameID) {
        if (this.gameID != null || gameID == null) return false;

        this.gameID = gameID;

        return true;
    }

    private void listen() {
        thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String incoming = in.readLine();
                    ClientEvent event = (ClientEvent) MyGson.instance.fromJson(incoming, Event.class);

                    switch (event) {
                        case CreateGameEvent ignored -> {
                            if (this.gameID != null) break;

                            String gameID = server.createGame(this);
                            this.gameID = gameID;
                            sendEvent(new GameCreatedEvent(gameID));
                        }
                        case JoinGameEvent ev -> {
                            if (this.gameID != null || ev.gameID == null) break;

                            Game game = server.findGame(ev.gameID);

                            if (game == null) break;

                            this.gameID = game.id();
                            Player enemy = game.player(game.id());

                            if (enemy == null) break;

                            enemy.sendEvent(new PlayerJoinedEvent());
                        }
                        case DisconnectEvent ignored -> {
                            if (this.gameID == null) break;

                            Game game = server.findGame(this.gameID);

                            if (game == null) break;

                            Player enemy = game.player(this.gameID);
                            game.removePlayer(this);
                            this.gameID = null;

                            if (enemy == null) break;

                            enemy.sendEvent(new PlayerLeftEvent());
                        }
                        case null, default -> System.err.println("Player >> Unknown command!");
                    }
                }
            } catch (IOException e) {
                System.err.println("Player >> " + e.getLocalizedMessage());
            }
        });
        thread.start();
    }

    private void sendEvent(ServerEvent event) {

    }

    public void disconnect() {
        if (!connected) return;

        try {
            this.out.close();
            this.in.close();
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
