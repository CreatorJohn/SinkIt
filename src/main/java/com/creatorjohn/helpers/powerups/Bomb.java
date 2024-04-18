package com.creatorjohn.helpers.powerups;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
    public int getCost(GameBoard.BoardSize boardSize) {
        int price = -1;

        switch (boardSize) {
            case SMALL -> price = 8 * 5;
            case MEDIUM -> price = 11 * 5;
            case BIG -> price = 14 * 8;
        }

        return price;
    }


}
