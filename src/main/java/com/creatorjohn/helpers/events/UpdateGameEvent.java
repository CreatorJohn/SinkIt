package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.Ship;
import com.creatorjohn.helpers.powerups.PowerUp;

import java.util.List;

final public class UpdateGameEvent extends Event implements ClientEvent {
    final public List<Position> tilesShot;
    final public List<PowerUp> usedPowerUps;

    public UpdateGameEvent(List<Position> tilesShot, List<PowerUp> usedPowerUps) {
        super(Type.UPDATE_GAME);
        this.tilesShot = tilesShot;
        this.usedPowerUps = usedPowerUps;
    }
}
