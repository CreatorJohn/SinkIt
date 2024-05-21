package com.creatorjohn.helpers.entities;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.json.MyGson;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.List;

public sealed class PowerUp implements GameComponent permits Bomb, Bomber, Radar, Farm {
    final public Type type;

    public PowerUp(@NotNull Type type) {
        this.type = type;
    }

    /**
     * Calculates cost of power-up using: 'max ship count on board' * 'max ship size' * 'cost modifier'
     * @param boardSize Type of game board
     * @return power-up cost
     */
    public int getCost(@NotNull GameBoard.BoardSize boardSize, int maxShipSize) {
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
