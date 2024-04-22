package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.json.MyGson;

public sealed class Event permits CreateGameEvent, DisconnectEvent, GameCreatedEvent, GameFinishedEvent, GameJoinedEvent, GameUpdatedEvent, InitializeGameEvent, JoinGameEvent, PlayerJoinedEvent, PlayerLeftEvent, UpdateGameEvent {
    final private Type type;

    public Event(Type type) {
        this.type = type;
    }

    public enum Type {
        CREATE_GAME,
        GAME_CREATED,
        JOIN_GAME,
        GAME_JOINED,
        PLAYER_JOINED,
        PLAYER_LEFT,
        INITIALIZE_GAME,
        GAME_UPDATED,
        UPDATE_GAME,
        GAME_FINISHED,
        DISCONNECT,
    }

    @Override
    public String toString() {
        return MyGson.instance.toJson(this);
    }
}
