package com.creatorjohn.db.models;

import java.util.UUID;

public record UserModel(String id, String username, String password) implements DataModel<UserModel> {

    public UserModel(String username, String password) {
        this(UUID.randomUUID().toString(), username, password);
    }
}
