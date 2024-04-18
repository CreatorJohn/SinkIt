package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.Ship;
import com.creatorjohn.helpers.powerups.PowerUp;

import java.util.List;

final public class GameUpdatedEvent extends Event implements ServerEvent {
    final public String currentPlayer;
    final public List<Ship> ships;
    final public List<PowerUp> powerUps;
    final public List<Position> revealedTiles;
    final public List<Position> shotTiles;

    public GameUpdatedEvent(
            String currentPlayer,
            List<Ship> ships,
            List<PowerUp> powerUps,
            List<Position> revealedTiles,
            List<Position> shotTiles
    ) {
        super(Type.GAME_UPDATED);
        this.currentPlayer = currentPlayer;
        this.ships = ships;
        this.powerUps = powerUps;
        this.revealedTiles = revealedTiles;
        this.shotTiles = shotTiles;
    }
}
