package com.creatorjohn.helpers.events;

public sealed interface ServerEvent permits
        GameCreatedEvent,
        GameUpdatedEvent,
        GameFinishedEvent,
        PlayerJoinedEvent,
        PlayerLeftEvent {}
