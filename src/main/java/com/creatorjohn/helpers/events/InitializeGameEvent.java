package com.creatorjohn.helpers.events;

import com.creatorjohn.helpers.Ship;

import java.util.List;

final public class InitializeGameEvent extends Event implements ClientEvent {
    final public List<Ship> ships;

    public InitializeGameEvent(List<Ship> ships) {
        super(Type.INITIALIZE_GAME);
        this.ships = ships;
    }
}
