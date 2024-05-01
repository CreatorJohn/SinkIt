package com.creatorjohn.handlers;

import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.json.MyGson;
import com.creatorjohn.helpers.logging.MyLogger;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    final private static MyLogger logger = new MyLogger("Client");
    final private Socket instance;
    final private BufferedReader in;
    final private PrintWriter out;
    private Thread thread;

    private Client(String address, int port) throws IOException {
        this.instance = new Socket(address, port);
        this.in = new BufferedReader(new InputStreamReader(instance.getInputStream()));
        this.out = new PrintWriter(instance.getOutputStream(), true);
    }

    public static Client connect(String address, int port) {
        try {
            return new Client(address, port);
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
            return null;
        }
    }

    public String id() {
        return instance.getLocalSocketAddress().toString();
    }

    public void sendEvent(ClientEvent event) {
        out.println(MyGson.instance.toJson(event));
    }

    public void handleIncomingEvents(EventHandlerConfig config) {
        this.thread = new Thread(() -> {
            try {
                String incoming;
                while ((incoming = in.readLine()) != null) {
                    ServerEvent event = (ServerEvent) MyGson.instance.fromJson(incoming, Event.class);

                    switch (event) {
                        case GameCreatedEvent ev -> config.onGameCreated(ev);
                        case GameJoinedEvent ev -> config.onGameJoined(ev);
                        case GameUpdatedEvent ev -> config.onGameUpdated(ev);
                        case GameFinishedEvent ev -> config.onGameFinished(ev);
                        case PlayerJoinedEvent ev -> config.onPlayerJoined(ev);
                        case PlayerLeftEvent ev -> config.onPlayerLeft(ev);
                        case null, default -> logger.warning("Unknown event!");
                    }
                }
            } catch (IOException e) {
                logger.info("Player disconnected!");
            } catch (Exception e) {
                logger.severe(e.getLocalizedMessage());
            }
        });
        thread.start();
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
            this.thread.interrupt();
            this.instance.close();

            return true;
        } catch (IOException e) {
            logger.info("Failed to close player!");
            return false;
        }
    }

    public interface EventHandlerConfig {
        void onGameCreated(GameCreatedEvent event);
        void onGameJoined(GameJoinedEvent event);
        void onGameFinished(GameFinishedEvent event);
        void onGameUpdated(GameUpdatedEvent event);
        void onPlayerJoined(PlayerJoinedEvent event);
        void onPlayerLeft(PlayerLeftEvent event);
    }
}
