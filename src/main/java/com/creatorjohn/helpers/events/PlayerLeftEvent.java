package com.creatorjohn.helpers.events;

final public class PlayerLeftEvent extends Event implements ServerEvent {

    public PlayerLeftEvent() {
        super(Type.PLAYER_LEFT);
    }
}
