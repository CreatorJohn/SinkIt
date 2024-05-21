package com.creatorjohn.helpers.events;

final public class PlayerJoinedEvent extends Event implements ServerEvent {
    final String username;

    public PlayerJoinedEvent(String username) {
        super(Type.PLAYER_JOINED);
        this.username = username;
    }
}
