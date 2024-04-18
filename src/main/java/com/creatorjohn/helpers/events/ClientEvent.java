package com.creatorjohn.helpers.events;

public sealed interface ClientEvent permits
        CreateGameEvent,
        InitializeGameEvent,
        JoinGameEvent,
        UpdateGameEvent,
        DisconnectEvent {}
