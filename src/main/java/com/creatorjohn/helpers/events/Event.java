package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.json.MyGson;
import com.creatorjohn.helpers.powerups.Bomb;
import com.creatorjohn.helpers.powerups.Farm;
import com.creatorjohn.helpers.powerups.PowerUp;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.sql.SQLOutput;
import java.util.List;

public sealed class Event permits CreateGameEvent, DisconnectEvent, GameCreatedEvent, GameFinishedEvent, InitializeGameEvent, JoinGameEvent, NextRound, UpdateGameEvent {
    final private Type type;

    public Event(Type type) {
        this.type = type;
    }

    public enum Type {
        CREATE_GAME,
        GAME_CREATED,
        JOIN_GAME,
        INITIALIZE_GAME,
        NEXT_ROUND,
        UPDATE_GAME,
        GAME_FINISHED,
        DISCONNECT,
    }

    @Override
    public String toString() {
        return MyGson.instance.toJson(this);
    }
}
