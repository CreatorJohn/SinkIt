package com.creatorjohn.helpers;


import com.creatorjohn.helpers.entities.*;

import javax.swing.*;
import java.util.List;
import java.util.function.Function;

public class JTileHandler {
    final private GameBoard gameBoard;

    public JTileHandler(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public String handle(PowerUp.Type powerUp, Position position, boolean actionKeyPressed) {
        if (actionKeyPressed) {
            JOptionPane.showMessageDialog(null, "Action key pressed!");
        }

        if (powerUp == PowerUp.Type.FARM) return "Invalid power-up!";
        Function<PowerUp.Type, String> formatError = (type) -> "You can't use \"" + type + "\"!";

        switch (powerUp) {
            case BOMB -> {
                if (!gameBoard.useBombPowerUp(new Bomb(position))) return formatError.apply(powerUp);
            }
            case BOMBER -> {
                Bomber.Direction direction = actionKeyPressed ? Bomber.Direction.HORIZONTAL : Bomber.Direction.VERTICAL;
                if (!gameBoard.useBomberPowerUp(new Bomber(position, direction))) return formatError.apply(powerUp);
            }
            case RADAR -> {
                if (!gameBoard.useRadarPowerUp(new Radar(position))) return formatError.apply(powerUp);
            }
            case null -> {
                if (!gameBoard.shootTile(position.x(), position.y())) return "You can't shoot this tile!";
            }
            default -> throw new IllegalStateException("Unexpected value: " + powerUp);
        }

        return null;
    }

    public String handle(PowerUp.Type powerUp, List<Position> positions) {
        switch (powerUp) {
            case BOMB, BOMBER, RADAR: return "Invalid power-up!";
            case FARM: if (!gameBoard.canUsePowerUp(new Farm(positions))) return "You can't use farm here!";
            case null: if (positions.stream().anyMatch(pos -> gameBoard.canShootTile(pos.x(), pos.y())))
                return "You can't shoot some tiles!";
        }

        if (powerUp == null) {
            for (Position pos : positions) {
                gameBoard.shootTile(pos.x(), pos.y());
            }
        } else gameBoard.useFarmPowerUp(new Farm(positions));

        return null;
    }
}
