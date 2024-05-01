package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Ship;

import java.util.List;

final public class GameInitializedEvent extends Event implements ServerEvent {
    final public boolean success;

    public GameInitializedEvent(boolean success) {
        super(Type.GAME_INITIALIZED);
        this.success = success;
    }
}
