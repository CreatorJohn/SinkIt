package com.creatorjohn.helpers.entities;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final public class Radar extends PowerUp {
    final private Position position;

    public Radar(Position position) {
        super(Type.RADAR);
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
            case SMALL -> price = JConfig.smallMapShipCount * maxShipSize * 2;
            case MEDIUM -> price = JConfig.mediumMapShipCount * maxShipSize * 2;
            case BIG -> price = JConfig.bigMapShipCount * maxShipSize * 2;
        }

        return price;
    }

}
