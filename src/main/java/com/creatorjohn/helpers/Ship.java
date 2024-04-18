package com.creatorjohn.helpers;

import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ship {
    final int size;
    final ArrayList<Position> positions = new ArrayList<>();

    public Ship(int size, List<Position> positions) {
        this.size = size;
        this.positions.addAll(positions);
    }

    public List<Position> getGameBoardPositions() {
        return positions;
    }

    public List<GameBoard.DetailedPosition> getGameBoardDetailedPositions() {
        return positions
                .stream()
                .map(it -> new GameBoard.DetailedPosition(new Position(it.x(), it.y()), false))
                .toList();
    }

    @Override
    public String toString() {
        return MyGson.instance.toJson(this);
    }
}
