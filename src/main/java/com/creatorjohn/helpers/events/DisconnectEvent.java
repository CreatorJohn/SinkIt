package com.creatorjohn.helpers.events;

final public class DisconnectEvent extends Event implements ClientEvent {

    public DisconnectEvent() {
        super(Type.DISCONNECT);
    }
}
