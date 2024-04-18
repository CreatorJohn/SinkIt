package com.creatorjohn.helpers;

import com.creatorjohn.helpers.events.UpdateGameEvent;
import com.creatorjohn.helpers.powerups.*;

import java.util.*;
import java.util.stream.Collectors;

public class GameBoard {
    final private ArrayList<Position> shipsPos = new ArrayList<>();
    final private ArrayList<Position> powerUpsPos = new ArrayList<>();
    final ArrayList<PowerUp> powerUps = new ArrayList<>();
    final ArrayList<Ship> ships = new ArrayList<>();
    final private List<DetailedPosition> allPositions;
    final private BoardSize boardSize;
    final private BoardType boardType;
    private int shootCounter = 0;
    private int tokens = 0;
    final int size;

    public GameBoard(BoardSize boardSize, BoardType boardType) {
        this.boardSize = boardSize;
        this.boardType = boardType;

        switch (boardSize) {
            case SMALL -> this.size = 10;
            case MEDIUM -> this.size = 14;
            case BIG -> this.size = 18;
            case null, default -> this.size = -1;
        }

        ArrayList<DetailedPosition> positions = new ArrayList<>();

        for (int i = 0; i < this.size * this.size; i++) {
            int x = i % this.size;
            int y = i / this.size;

            positions.add(new DetailedPosition(new Position(x, y), false));
        }

        this.allPositions = positions;
    }

    public boolean updateGame(UpdateGameEvent event) {
        boolean powerUpsUsed = event.usedPowerUps.stream().allMatch(powerUp -> {
            switch (powerUp) {
                case Bomb bomb -> { return useBombPowerUp(bomb); }
                case Bomber bomber -> { return useBomberPowerUp(bomber); }
                case Farm farm -> { return useFarmPowerUp(farm); }
                case Radar radar -> { return useRadarPowerUp(radar); }
                default -> { return false; }
            }
        });
        boolean tilesShot = event.tilesShot.stream().allMatch(tile -> {
            shootCounter++;
            return canShootTile(tile.x(), tile.y());
        });

        return powerUpsUsed && tilesShot;
    }

    public boolean placeShip(Ship ship) {
        if (boardType == BoardType.ENEMY) return false;
        if (shipsPos.stream().anyMatch(pos -> ship.getGameBoardPositions().contains(pos)))
            return false;

        ships.add(ship);
        shipsPos.addAll(ship.getGameBoardPositions());
        allPositions.removeAll(ship.getGameBoardDetailedPositions());

        return true;
    }

    public boolean removeShip(int x, int y) {
        if (boardType == BoardType.ENEMY) return false;

        Optional<Ship> target = ships
                .stream()
                .filter(ship -> ship.getGameBoardPositions().contains(new Position(x, y)))
                .findFirst();

        if (target.isEmpty()) return false;

        ships.remove(target.get());
        shipsPos.removeAll(target.get().getGameBoardPositions());
        allPositions.removeAll(target.get().getGameBoardDetailedPositions());

        return true;
    }

    public boolean canShootTile(int x, int y) {
        if (boardType == BoardType.MY) return false;
        else if (shootCounter > 4) return false;
        else return !allPositions.contains(new DetailedPosition(new Position(x, y), true));
    }

    public boolean shootTile(int x, int y) {
        if (!canShootTile(x, y)) return false;

        Optional<Ship> targetShip = ships
                .stream()
                .filter(ship -> ship.getGameBoardPositions().contains(new Position(x, y)))
                .findFirst();
        Optional<PowerUp> targetPowerUp = powerUps
                .stream()
                .filter(powerUp -> powerUp.getPositions().contains(new Position(x, y)))
                .findFirst();

        int index = allPositions.indexOf(new DetailedPosition(new Position(x, y), false));

        allPositions.set(index, new DetailedPosition(allPositions.get(index).position(), true));
        targetPowerUp.ifPresent(powerUp -> powerUpsPos.removeAll(powerUp.getPositions()));

        if (targetShip.isPresent()) {
            List<Position> shipPos = targetShip.get().getGameBoardPositions();
            boolean isDestroyed = shipPos
                    .stream()
                    .allMatch(pos -> {
                        Optional<DetailedPosition> found = allPositions
                                .stream()
                                .filter(it -> it.position().x() == pos.x() && it.position().y() == pos.y())
                                .findFirst();

                        return found.isPresent() && found.get().revealed();
                    });

            if (isDestroyed) {
                ships.remove(targetShip.get());
                shipsPos.removeAll(shipPos);
            }
        } else if (targetPowerUp.isPresent() && targetPowerUp.get() instanceof Bomb bomb) {
            powerUps.remove(bomb);

            for (int i = 0; i < 3; i++) {
                int maxIndex = (int) allPositions.stream().filter(it -> !it.revealed()).count();
                int randomIndex = new Random().nextInt(maxIndex);
                Position randomPos = allPositions.get(randomIndex).position();
                shootTile(randomPos.x(), randomPos.y());
            }
        }

        return true;
    }

    public void generateTokens() {
        if (boardType == BoardType.MY) return;

        List<Position> revealed = allPositions
                .stream()
                .filter(DetailedPosition::revealed)
                .map(DetailedPosition::toPosition)
                .toList();
        Optional<Integer> shipsTokens = ships
                .stream()
                .map(ship -> ship
                        .getGameBoardPositions()
                        .stream()
                        .filter(pos -> {
                            Optional<DetailedPosition> position = allPositions
                                    .stream()
                                    .filter(it -> it.position().x() == pos.x() && it.position().y() == pos.y())
                                    .findFirst();

                            return position.isPresent() && !position.get().revealed();
                        })
                        .count())
                .reduce(Long::sum)
                .map(it -> Integer.parseInt(it.toString()));
        Optional<Integer> farmTokens = powerUps
                .stream()
                .filter(powerUp -> powerUp instanceof Farm)
                .map(farm -> ((Farm) farm).getGenerates(revealed, shipsPos))
                .reduce(Integer::sum);

        tokens += farmTokens.orElse(0) + shipsTokens.orElse(0);
    }

    public boolean canUsePowerUp(PowerUp powerUp) {
        if (powerUp.getCost(boardSize) > tokens) return false;

        boolean success = false;
        HashSet<Position> emptyHiddenPositions = new HashSet<>(allPositions
                .stream()
                .filter(it -> !it.revealed()
                        && shipsPos.contains(it.toPosition())
                        && powerUpsPos.contains(it.toPosition()))
                .map(DetailedPosition::toPosition)
                .toList());
        List<Position> emptyRevealedPositions = allPositions
                .stream()
                .filter(it -> it.revealed()
                        && shipsPos.contains(it.toPosition())
                        && powerUpsPos.contains(it.toPosition()))
                .map(DetailedPosition::toPosition)
                .toList();

        switch (powerUp) {
            case Bomb bomb -> success = emptyHiddenPositions.contains(bomb.position());
            case Farm farm -> success = emptyHiddenPositions.containsAll(farm.positions());
            case Radar radar -> success = emptyRevealedPositions.contains(radar.position());
            default -> success = true;
        }

        return success;
    }

    public boolean useBombPowerUp(Bomb bomb) {
        if (!canUsePowerUp(bomb)) return false;

        powerUps.add(bomb);
        powerUpsPos.add(bomb.position());
        tokens -= bomb.getCost(boardSize);

        return true;
    }

    public boolean useFarmPowerUp(Farm farm) {
        if (!canUsePowerUp(farm)) return false;

        powerUps.add(farm);
        tokens -= farm.getCost(boardSize);
        powerUpsPos.addAll(farm.positions());

        return true;
    }

    public boolean useRadarPowerUp(Radar radar) {
        if (!canUsePowerUp(radar)) return false;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) continue;

                shootTile(j, i);
            }
        }

        powerUps.add(radar);
        powerUpsPos.add(radar.position());
        tokens -= radar.getCost(boardSize);

        return true;
    }

    public boolean useBomberPowerUp(Bomber bomber) {
        if (!canUsePowerUp(bomber)) return false;

        boolean isHorizontal = bomber.direction() == Bomber.Direction.HORIZONTAL;
        boolean isVertical = bomber.direction() == Bomber.Direction.VERTICAL;

        ArrayList<Position> out = new ArrayList<>();
        ArrayList<Position> revealedShipTiles = shipsPos
                .stream()
                .filter(shipPos ->
                        (isHorizontal && shipPos.x() == bomber.position()) ||
                                (isVertical && shipPos.y() == bomber.position()))
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Position> revealedPowerUpTiles = powerUpsPos
                .stream()
                .filter(powerUpPos ->
                        (isHorizontal && powerUpPos.x() == bomber.position()) ||
                                (isVertical && powerUpPos.y() == bomber.position()))
                .collect(Collectors.toCollection(ArrayList::new));
        out.addAll(revealedShipTiles);
        out.addAll(revealedPowerUpTiles);

        List<Integer> indexes = out
                .stream()
                .map(it -> allPositions.contains(new DetailedPosition(it, false))
                        ? allPositions.indexOf(new DetailedPosition(it, false))
                        : allPositions.indexOf(new DetailedPosition(it, true)))
                .toList();

        indexes.forEach(index -> {
            allPositions.set(index, new DetailedPosition(allPositions.get(index).position(), true));
        });

        return true;
    }

    public enum BoardSize { SMALL, MEDIUM, BIG }
    public enum BoardType { MY, ENEMY }

    public record DetailedPosition(Position position, boolean revealed) {
        Position toPosition() {
            return position();
        }
    }
}
