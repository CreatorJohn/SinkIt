package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.powerups.PowerUp;

import java.util.List;

final public class GameUpdatedEvent extends Event implements ServerEvent {
    final public String currentPlayer;
    final public List<Position> shotTiles;

    public GameUpdatedEvent(String currentPlayer, List<Position> shotTiles) {
        super(Type.GAME_UPDATED);
        this.currentPlayer = currentPlayer;
        this.shotTiles = shotTiles;
    }
}
