package com.creatorjohn.helpers.events;

final public class DisconnectEvent extends Event {
    final public String gameID;

    public DisconnectEvent(String gameID) {
        super(Type.DISCONNECT);
        this.gameID = gameID;
    }
}
