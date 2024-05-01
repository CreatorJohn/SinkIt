package com.creatorjohn.helpers.events;

final public class LoginEvent extends Event implements ClientEvent {
    final public String username;
    final public String password;

    public LoginEvent(String username, String password) {
        super(Type.LOGIN);
        this.username = username;
        this.password = password;
    }
}
