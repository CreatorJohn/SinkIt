package com.creatorjohn.helpers.events;

final public class GameCreatedEvent extends Event implements ServerEvent {
    final public String gameID;

    public GameCreatedEvent(String gameID) {
        super(Type.GAME_CREATED);
        this.gameID = gameID;
    }
}
