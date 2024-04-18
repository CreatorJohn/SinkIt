package com.creatorjohn.helpers.events;

final public class PlayerJoinedEvent extends Event implements ServerEvent {

    public PlayerJoinedEvent() {
        super(Type.PLAYER_JOINED);
    }
}
