package com.creatorjohn.helpers.powerups;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.Gson;

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
    public int getCost(GameBoard.BoardSize boardSize) {
        int price = -1;

        switch (boardSize) {
            case SMALL -> price = 8 * 5 * 2;
            case MEDIUM -> price = 11 * 5 * 2;
            case BIG -> price = 14 * 5 * 2;
        }

        return price;
    }

}
