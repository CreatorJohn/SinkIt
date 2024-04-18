package com.creatorjohn.helpers.events;

final public class CreateGameEvent extends Event {

    public CreateGameEvent(String gameID) {
        super(Type.CREATE_GAME);
    }
}
