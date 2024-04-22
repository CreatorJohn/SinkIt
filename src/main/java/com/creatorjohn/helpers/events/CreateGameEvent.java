package com.creatorjohn.helpers.events;

final public class CreateGameEvent extends Event implements ClientEvent {

    public CreateGameEvent() {
        super(Type.CREATE_GAME);
    }
}
