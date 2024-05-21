package com.creatorjohn.handlers;

import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.json.MyGson;
import com.creatorjohn.helpers.logging.MyLogger;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class Client {
    final private static MyLogger logger = new MyLogger("Client");
    final private Socket instance;
    final private BufferedReader in;
    final private PrintWriter out;
    final private String username;
    final private String password;
    final private String address;
    final private int port;
    private Consumer<LoginResponseEvent> onLoginResponse;
    private Consumer<RegisterResponseEvent> onRegisterResponse;
    private Consumer<GameCreatedEvent> onGameCreated;
    private Consumer<GameJoinedEvent> onGameJoined;
    private Consumer<GameInitializedEvent> onGameInitialized;
    private Consumer<GameUpdatedEvent> onGameUpdated;
    private Consumer<GameFinishedEvent> onGameFinished;
    private Consumer<PlayerJoinedEvent> onPlayerJoined;
    private Consumer<PlayerLeftEvent> onPlayerLeft;
    private Consumer<StatisticsResponseEvent> onStatisticsResponse;
    private boolean handling = false;
    private Thread thread;

    private Client(String username, String password, String address, int port) throws IOException {
        this.instance = new Socket(address, port);
        this.in = new BufferedReader(new InputStreamReader(instance.getInputStream()));
        this.out = new PrintWriter(instance.getOutputStream(), true);
        this.username = username;
        this.password = password;
        this.address = address;
        this.port = port;
    }

    public static Client connect(String username, String password, String address, int port) {
        try {
            return new Client(username, password, address, port);
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
            return null;
        }
    }

    public String id() {
        return instance.getLocalSocketAddress().toString();
    }

    public String username() {
        return username;
    }

    public void sendEvent(ClientEvent event) {
        out.println(MyGson.instance.toJson(event));
    }

    public void handleIncomingEvents() {
        this.thread = new Thread(() -> {
            handling = true;

            try {
                String incoming;
                while ((incoming = in.readLine()) != null && handling) {
                    ServerEvent event = (ServerEvent) MyGson.instance.fromJson(incoming, Event.class);

                    switch (event) {
                        case LoginResponseEvent ev -> {
                            if (onLoginResponse != null) onLoginResponse.accept(ev);
                        }
                        case RegisterResponseEvent ev -> {
                            if (onRegisterResponse != null) onRegisterResponse.accept(ev);
                        }
                        case GameCreatedEvent ev -> {
                            if (onGameCreated != null) onGameCreated.accept(ev);
                        }
                        case GameJoinedEvent ev -> {
                            if (onGameJoined != null) onGameJoined.accept(ev);
                        }
                        case GameUpdatedEvent ev -> {
                            if (onGameUpdated != null) onGameUpdated.accept(ev);
                        }
                        case GameFinishedEvent ev -> {
                            if (onGameFinished != null) onGameFinished.accept(ev);
                        }
                        case PlayerJoinedEvent ev -> {
                            if (onPlayerJoined != null) onPlayerJoined.accept(ev);
                        }
                        case PlayerLeftEvent ev -> {
                            if (onPlayerLeft != null) onPlayerLeft.accept(ev);
                        }
                        case GameInitializedEvent ev -> {
                            if (onGameInitialized != null) onGameInitialized.accept(ev);
                        }
                        case StatisticsResponseEvent ev -> {
                            if (onStatisticsResponse != null) onStatisticsResponse.accept(ev);
                        }
                        case null, default -> logger.warning("Unknown event!");
                    }
                }
            } catch (IOException e) {
                logger.info("Player disconnected!");
            } catch (Exception e) {
                logger.severe(e.getLocalizedMessage());
            }
            handling = false;
        });
        thread.start();
    }

    public void cancelEventHandler() {
        handling = false;
    }

    public void onLoginResponse(Consumer<LoginResponseEvent> function) {
        this.onLoginResponse = function;
    }

    public void onRegisterResponse(Consumer<RegisterResponseEvent> function) {
        this.onRegisterResponse = function;
    }

    public void onGameCreated(Consumer<GameCreatedEvent> function) {
        this.onGameCreated = function;
    }

    public void onGameJoined(Consumer<GameJoinedEvent> function) {
        this.onGameJoined = function;
    }

    public void onGameInitialized(Consumer<GameInitializedEvent> function) {
        this.onGameInitialized = function;
    }

    public void onGameUpdated(Consumer<GameUpdatedEvent> function) {
        this.onGameUpdated = function;
    }

    public void onGameFinished(Consumer<GameFinishedEvent> function) {
        this.onGameFinished = function;
    }

    public void onPlayerJoined(Consumer<PlayerJoinedEvent> function) {
        this.onPlayerJoined = function;
    }

    public void onPlayerLeft(Consumer<PlayerLeftEvent> function) {
        this.onPlayerLeft = function;
    }

    public void onStatisticsResponse(Consumer<StatisticsResponseEvent> function) {
        this.onStatisticsResponse = function;
    }

    public <T extends Event> T receiveEvent(Class<T> tClass) {
        try {
            String incoming = in.readLine();

            if (incoming == null) return null;

            return MyGson.instance.fromJson(incoming, tClass);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean disconnect() {
        try {
            if (this.thread != null) this.thread.interrupt();
            this.instance.close();

            return true;
        } catch (Exception e) {
            logger.info("Failed to close player!");
            return false;
        }
    }
}
