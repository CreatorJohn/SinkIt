package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.Ship;
import com.creatorjohn.helpers.entities.PowerUp;

import java.util.List;

final public class GameJoinedEvent extends Event implements ServerEvent {
    final public boolean success;
    final public ShipInfo ships;

    public GameJoinedEvent(boolean success) {
        this(new ShipInfo(), success);
    }

    public GameJoinedEvent(ShipInfo ships, boolean success) {
        super(Type.GAME_JOINED);
        this.success = success;
        this.ships = ships;
    }

    public record ShipInfo(List<Ship> my, List<Ship> enemy) {
        public ShipInfo() {
            this(List.of(), List.of());
        }
    }
}
