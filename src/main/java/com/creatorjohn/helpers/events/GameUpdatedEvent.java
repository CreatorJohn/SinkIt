package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.PowerUp;
import com.creatorjohn.helpers.entities.Ship;

import java.util.List;

final public class GameUpdatedEvent extends Event implements ServerEvent {
    final public String currentPlayer;
    final public PowerUpsInfo powerUps;
    final public ShotTilesInfo shotTiles;
    final public List<Ship> ships;
    final public boolean success;

    public GameUpdatedEvent(String currentPlayer, PowerUpsInfo powerUps, ShotTilesInfo shotTiles, List<Ship> ships, boolean success) {
        super(Type.GAME_UPDATED);
        this.currentPlayer = currentPlayer;
        this.powerUps = powerUps;
        this.shotTiles = shotTiles;
        this.ships = ships;
        this.success = success;
    }

    public record PowerUpsInfo(List<PowerUp> my, List<PowerUp> enemy) {
        public PowerUpsInfo() {
            this(List.of(), List.of());
        }
    }
    public record ShotTilesInfo(List<Position> my, List<Position> enemy) {
        public ShotTilesInfo() {
            this(List.of(), List.of());
        }
    }
}
