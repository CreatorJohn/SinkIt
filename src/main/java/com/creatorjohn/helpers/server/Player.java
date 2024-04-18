package com.creatorjohn.helpers.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Player {
    final private String id;
    final private Socket instance;
    final private PrintWriter out;
    final private BufferedReader in;
    private boolean connected = false;
    private String gameID;

    public Player(ServerSocket server) throws Exception {
        try {
            instance = server.accept();
            out = new PrintWriter(instance.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(instance.getInputStream()));
            id = instance.getRemoteSocketAddress().toString();
            connected = true;
        } catch (IOException e) {
            printError(e);
            throw new Exception(e.getLocalizedMessage());
        }
    }

    public String id() {
        return id;
    }

    public boolean joinGame(String gameID) {
        if (this.gameID != null || gameID == null) return false;

        this.gameID = gameID;

        return true;
    }

    public void disconnect() {
        if (connected) return;

        try {
            out.close();
            in.close();
            instance.close();
            connected = false;
        } catch (IOException e) {
            printError(e);
        }
    }

    private void printError(Exception e) {
        System.out.println("Server client >> " + e.getLocalizedMessage());
    }
}
