package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.json.MyGson;

public sealed class Event permits CreateGameEvent, DisconnectEvent, GameCreatedEvent, GameFinishedEvent, GameInitializedEvent, GameJoinedEvent, GameUpdatedEvent, InitializeGameEvent, JoinGameEvent, LoginEvent, LoginResponseEvent, PlayerJoinedEvent, PlayerLeftEvent, RegisterEvent, RegisterResponseEvent, UpdateGameEvent {
    final private Type type;

    public Event(Type type) {
        this.type = type;
    }

    public enum Type {
        LOGIN,
        REGISTER,
        LOGIN_RESPONSE,
        REGISTER_RESPONSE,
        CREATE_GAME,
        GAME_CREATED,
        JOIN_GAME,
        GAME_JOINED,
        PLAYER_JOINED,
        PLAYER_LEFT,
        INITIALIZE_GAME,
        GAME_INITIALIZED,
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
