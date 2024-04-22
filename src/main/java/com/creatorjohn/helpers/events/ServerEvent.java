package com.creatorjohn.helpers.events;

public sealed interface ServerEvent permits GameCreatedEvent, GameFinishedEvent, GameJoinedEvent, GameUpdatedEvent, PlayerJoinedEvent, PlayerLeftEvent {}
