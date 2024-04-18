package com.creatorjohn.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    final private Socket instance;
    final private BufferedReader in;
    final private PrintWriter out;

    public static Client connect(String address, int port) {
        try {
            return new Client(address, port);
        } catch (IOException e) {
            System.err.println("Client >> " + e.getLocalizedMessage());
            return null;
        }
    }

    private Client(String address, int port) throws IOException {
        this.instance = new Socket(address, port);
        this.in = new BufferedReader(new InputStreamReader(instance.getInputStream()));
        this.out = new PrintWriter(instance.getOutputStream(), true);
    }

    public boolean disconnect() {
        try {
            this.in.close();
            this.out.close();
            this.instance.close();

            return true;
        } catch (IOException e) {
            System.err.println("Client >> " + e.getLocalizedMessage());
            return false;
        }
    }
}
