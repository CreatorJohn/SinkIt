package com.creatorjohn.helpers.powerups;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public sealed class PowerUp permits Bomb, Bomber, Radar, Farm {
    final private Type type;

    public PowerUp(Type type) {
        this.type = type;
    }

    /**
     * Calculates cost of power-up using: 'max ship count on board' * 'max ship size' * 'cost modifier'
     * @param boardSize Type of game board
     * @return power-up cost
     */
    public int getCost(GameBoard.BoardSize boardSize) {
        return -1;
    };

    /**
     * Convert power-up "position/s" property to array
     * @return power-up position array
     */
    public List<Position> getPositions() {
        return List.of();
    };

    @Override
    public String toString() {
        return MyGson.instance.toJson(this);
    }

    public enum Type {
        BOMB,
        BOMBER,
        FARM,
        RADAR
    }
}
