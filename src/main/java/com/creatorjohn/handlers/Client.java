package com.creatorjohn.handlers;

import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.json.MyGson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    final private Socket instance;
    final private BufferedReader in;
    final private PrintWriter out;
    private Thread thread;
    private EventHandlerConfig config;

    private Client(String address, int port) throws IOException {
        this.instance = new Socket(address, port);
        this.in = new BufferedReader(new InputStreamReader(instance.getInputStream()));
        this.out = new PrintWriter(instance.getOutputStream(), true);
    }

    public static Client connect(String address, int port) {
        try {
            return new Client(address, port);
        } catch (IOException e) {
            System.err.println("Client >> " + e.getLocalizedMessage());
            return null;
        }
    }

    public void setEventHandler(EventHandlerConfig config) {
        this.config = config;
    }

    public boolean sendEvent(ClientEvent event) {
        if (out == null) return false;

        out.println(MyGson.instance.toJson(event));
        return true;
    }

    public void handleIncomingEvents() {
        this.thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String incoming = in.readLine();
                    ServerEvent event = (ServerEvent) MyGson.instance.fromJson(incoming, Event.class);

                    switch (event) {
                        case GameCreatedEvent ev -> config.onGameCreated(ev);
                        case GameUpdatedEvent ev -> config.onGameUpdated(ev);
                        case GameFinishedEvent ev -> config.onGameFinished(ev);
                        case PlayerJoinedEvent ev -> config.onPlayerJoined(ev);
                        case PlayerLeftEvent ev -> config.onPlayerLeft(ev);
                        case null, default -> System.err.println("Client >> Unknown event!");
                    }
                }
            } catch (IOException e) {
                System.out.println("Client >> Incoming event handling finished!");
            }
        });
        thread.start();
    }

    public boolean disconnect() {
        try {
            this.in.close();
            this.out.close();
            this.instance.close();
            this.thread.interrupt();

            return true;
        } catch (IOException e) {
            System.err.println("Client >> " + e.getLocalizedMessage());
            return false;
        }
    }

    public interface EventHandlerConfig {
        void onGameCreated(GameCreatedEvent event);
        void onGameFinished(GameFinishedEvent event);
        void onGameUpdated(GameUpdatedEvent event);
        void onPlayerJoined(PlayerJoinedEvent event);
        void onPlayerLeft(PlayerLeftEvent event);
    }
}
