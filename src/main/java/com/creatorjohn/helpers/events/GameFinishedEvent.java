package com.creatorjohn.helpers.events;

final public class GameFinishedEvent extends Event {
    final public Status status;

    public GameFinishedEvent(Status status) {
        super(Type.GAME_FINISHED);
        this.status = status;
    }

    public enum Status { WINNER, LOOSER }
}
