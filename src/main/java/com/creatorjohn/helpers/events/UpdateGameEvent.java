package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.PowerUp;

import java.util.List;

final public class UpdateGameEvent extends Event implements ClientEvent {
    final public List<Position> tilesShot;
    final public List<PowerUp> usedPowerUps;
    final public boolean gameOver;

    public UpdateGameEvent(List<Position> tilesShot, List<PowerUp> usedPowerUps, boolean gameOver) {
        super(Type.UPDATE_GAME);
        this.tilesShot = tilesShot;
        this.usedPowerUps = usedPowerUps;
        this.gameOver = gameOver;
    }
}
