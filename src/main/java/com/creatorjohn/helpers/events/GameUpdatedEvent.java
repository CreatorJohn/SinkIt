package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.powerups.PowerUp;

import java.util.List;

final public class GameUpdatedEvent extends Event implements ServerEvent {
    final public String currentPlayer;
    final public List<PowerUp> powerUps;
    final public List<Position> shotTiles;
    final public boolean success;

    public GameUpdatedEvent(String currentPlayer, List<PowerUp> powerUps, List<Position> shotTiles, boolean success) {
        super(Type.GAME_UPDATED);
        this.currentPlayer = currentPlayer;
        this.powerUps = powerUps;
        this.shotTiles = shotTiles;
        this.success = success;
    }
}
