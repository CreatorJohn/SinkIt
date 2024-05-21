package com.creatorjohn.helpers.events;

final public class GameInitializedEvent extends Event implements ServerEvent {
    final public boolean success;

    public GameInitializedEvent(boolean success) {
        super(Type.GAME_INITIALIZED);
        this.success = success;
    }
}
