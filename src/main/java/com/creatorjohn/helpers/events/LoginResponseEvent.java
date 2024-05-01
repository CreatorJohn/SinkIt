package com.creatorjohn.helpers.events;

final public class LoginResponseEvent extends Event implements ServerEvent {
    final public String error;

    public LoginResponseEvent(String error) {
        super(Type.LOGIN_RESPONSE);
        this.error = error;
    }
}
