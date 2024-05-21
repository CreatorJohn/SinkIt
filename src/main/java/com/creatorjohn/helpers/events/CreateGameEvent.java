package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.GameBoard;

final public class CreateGameEvent extends Event implements ClientEvent {
    final GameBoard.BoardSize size;

    public CreateGameEvent(GameBoard.BoardSize size) {
        super(Type.CREATE_GAME);
        this.size = size;
    }
}
