package com.creatorjohn.helpers.entities;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.json.MyGson;

import java.util.ArrayList;
import java.util.List;

public final class Ship implements GameComponent {
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
