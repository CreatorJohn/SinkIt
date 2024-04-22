package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.Ship;
import com.creatorjohn.helpers.powerups.PowerUp;

import java.util.List;

final public class GameJoinedEvent extends Event implements ServerEvent {
    final public List<Ship> ships;
    final public List<PowerUp> powerUps;
    final public List<Position> shotTiles;
    final public List<Position> revealedTiles;

    public GameJoinedEvent(
            List<Ship> ships,
            List<PowerUp> powerUps,
            List<Position> shotTiles,
            List<Position> revealedTiles
    ) {
        super(Type.GAME_JOINED);
        this.ships = ships;
        this.powerUps = powerUps;
        this.shotTiles = shotTiles;
        this.revealedTiles = revealedTiles;
    }
}
