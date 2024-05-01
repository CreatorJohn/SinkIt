package com.creatorjohn.helpers.events;

final public class RegisterResponseEvent extends Event implements ServerEvent {
    final public String error;

    public RegisterResponseEvent(String error) {
        super(Type.REGISTER);
        this.error = error;
    }
}
