package com.creatorjohn.helpers.events;

final public class LogoutEvent extends Event implements ClientEvent {

    public LogoutEvent() {
        super(Type.LOGOUT);
    }
}
