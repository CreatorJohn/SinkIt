package com.creatorjohn.helpers.powerups;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.Gson;

import java.util.List;

/**
 * Bomber power-up reveals game board row or game board column
 * @param position game board row or column index
 * @param direction power-up reveal direction
 */
final public class Bomber extends PowerUp {
    final private int position;
    final private Direction direction;

    public Bomber(int position, Direction direction) {
        super(Type.BOMBER);
        this.position = position;
        this.direction = direction;
    }

    public int position() {
        return position;
    }

    public Direction direction() {
        return direction;
    }

    @Override
    public List<Position> getPositions() {
        return direction == Direction.HORIZONTAL
                ? List.of(new Position(-1, position))
                : List.of(new Position(position, -1));
    }

    @Override
    public int getCost(GameBoard.BoardSize boardSize) {
        int price = -1;

        switch (boardSize) {
            case SMALL -> price = 8 * 5 * 5;
            case MEDIUM -> price = 11 * 5 * 5;
            case BIG -> price = 14 * 5 * 5;
        }

        return price;
    }

    public enum Direction { HORIZONTAL, VERTICAL }
}
