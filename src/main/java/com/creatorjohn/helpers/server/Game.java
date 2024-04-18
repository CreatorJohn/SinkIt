package com.creatorjohn.helpers.server;

import java.util.Hashtable;
import java.util.UUID;

public class Game {
    final private String id = UUID.randomUUID().toString();
    final private Hashtable<String, Player> players = new Hashtable<>();

    public Game(Player player) {

    }

    public String id() {
        return id;
    }
}
