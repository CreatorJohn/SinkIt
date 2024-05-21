package com.creatorjohn.helpers.events;

public sealed interface ServerEvent permits GameCreatedEvent, GameFinishedEvent, GameInitializedEvent, GameJoinedEvent, GameUpdatedEvent, LoginResponseEvent, PlayerJoinedEvent, PlayerLeftEvent, RegisterResponseEvent, StatisticsResponseEvent {}
