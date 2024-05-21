package com.creatorjohn.helpers.entities;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Bomb power-up can be placed on empty unrevealed tile. When hit, it reveals three random unrevealed tiles on enemies board
 */
final public class Bomb extends PowerUp {
    final private Position position;

    public Bomb(Position position) {
        super(Type.BOMB);
        this.position = position;
    }

    public Position position() {
        return position;
    }

    @Override
    public List<Position> getPositions() {
        return List.of(position);
    }

    @Override
    public int getCost(GameBoard.@NotNull BoardSize boardSize, int maxShipSize) {
        int price = -1;

        switch (boardSize) {
            case SMALL -> price = JConfig.smallMapShipCount * maxShipSize;
            case MEDIUM -> price = JConfig.mediumMapShipCount * maxShipSize;
            case BIG -> price = JConfig.bigMapShipCount * maxShipSize;
        }

        return price;
    }


}
