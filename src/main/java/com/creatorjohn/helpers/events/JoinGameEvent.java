package com.creatorjohn.helpers.events;

final public class JoinGameEvent extends Event implements ClientEvent {
    final public String gameID;

    public JoinGameEvent(String gameID) {
        super(Type.JOIN_GAME);
        this.gameID = gameID;
    }
}
