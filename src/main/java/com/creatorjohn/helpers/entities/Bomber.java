package com.creatorjohn.helpers.entities;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final public class Bomber extends PowerUp {
    final private Position position;
    final private Direction direction;

    /**
     * Bomber power-up reveals game board row or game board column
     * @param position game board position
     * @param direction power-up reveal direction
     */
    public Bomber(@NotNull Position position, @NotNull Direction direction) {
        super(Type.BOMBER);
        this.position = position;
        this.direction = direction;
    }

    public Position position() {
        return position;
    }

    public int position(@NotNull Direction direction) {
        return direction == Direction.VERTICAL ? position.x() : position.y();
    }

    public Direction direction() {
        return direction;
    }

    @Override
    public @NotNull List<Position> getPositions() {
        return List.of(position);
    }

    @Override
    public int getCost(@NotNull GameBoard.BoardSize boardSize, int maxShipSize) {
        int price = -1;

        switch (boardSize) {
            case SMALL -> price = JConfig.smallMapShipCount * maxShipSize * 5;
            case MEDIUM -> price = JConfig.mediumMapShipCount * maxShipSize * 5;
            case BIG -> price = JConfig.bigMapShipCount * maxShipSize * 5;
        }

        return price;
    }

    public enum Direction { HORIZONTAL, VERTICAL }
}
