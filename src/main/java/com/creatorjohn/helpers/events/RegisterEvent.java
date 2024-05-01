package com.creatorjohn.helpers.events;

final public class RegisterEvent extends Event implements ClientEvent {
    final public String username;
    final public String password;

    public RegisterEvent(String username, String password) {
        super(Type.REGISTER);
        this.username = username;
        this.password = password;
    }
}
