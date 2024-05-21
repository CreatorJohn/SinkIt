package com.creatorjohn.helpers.events;

public sealed interface ClientEvent permits CreateGameEvent, DisconnectEvent, InitializeGameEvent, JoinGameEvent, LoginEvent, LogoutEvent, RegisterEvent, StatisticsRequestEvent, UpdateGameEvent {}
