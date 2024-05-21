package com.creatorjohn.helpers;

import com.creatorjohn.db.models.UserModel;
import com.creatorjohn.helpers.events.UpdateGameEvent;
import com.creatorjohn.helpers.entities.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class GameBoard {
    final private ArrayList<Position> shipsPos = new ArrayList<>();
    final private ArrayList<Position> powerUpsPos = new ArrayList<>();
    final private List<DetailedPosition> allPositions;
    final private BoardSize boardSize;
    final private BoardType boardType;
    final private int maxShipSize;
    final ArrayList<PowerUp> powerUps = new ArrayList<>();
    final ArrayList<Ship> ships = new ArrayList<>();
    private Consumer<Integer> onTokensChanged;
    private Consumer<Position> onShoot;
    private Runnable onGameOver;
    private boolean gameOver = false;
    private int shootCounter = 0;
    private int shipCounter = 0;
    private int tokens = 0;
    final int size;

    public GameBoard(BoardSize boardSize, BoardType boardType, int maxShipSize) {
        this.boardSize = boardSize;
        this.boardType = boardType;
        this.maxShipSize = maxShipSize;

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

    public void onShoot(Consumer<Position> onShoot) {
        this.onShoot = onShoot;
    }

    public void onGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    public static int shipCount(@NotNull BoardSize size) {
        return switch (size) {
            case SMALL -> JConfig.smallMapShipCount;
            case MEDIUM -> JConfig.mediumMapShipCount;
            case BIG -> JConfig.bigMapShipCount;
        };
    }

    public boolean updateGameBoard(UpdateGameEvent event) {
        boolean powerUpsUsed = event.usedPowerUps.stream().allMatch(powerUp -> switch (powerUp) {
            case Bomb bomb -> useBombPowerUp(bomb);
            case Bomber bomber -> useBomberPowerUp(bomber);
            case Farm farm -> useFarmPowerUp(farm);
            case Radar radar -> useRadarPowerUp(radar);
            default -> false;
        });
        boolean tilesShot = event.tilesShot.stream().allMatch(tile -> {
            shootCounter++;
            return canShootTile(tile.x(), tile.y());
        });

        if (tilesShot) event.tilesShot.forEach(it -> shootTile(it.x(), it.y()));

        return powerUpsUsed && tilesShot;
    }

    public boolean placeShip(Ship ship) {
        return placeShip(ship, false);
    }

    public boolean placeShip(Ship ship, boolean force) {
        if (shipCount(boardSize) <= shipCounter && !force) return false;
        if (shipsPos.stream().anyMatch(pos -> ship.getGameBoardPositions().contains(pos)) && !force)
            return false;

        ships.add(ship);
        shipsPos.addAll(ship.getGameBoardPositions());
        shipCounter++;

        return true;
    }

    public Ship ship(Position position) {
        List<Ship> filtered = ships.stream().filter(it -> it.getGameBoardPositions().contains(position)).toList();

        if (filtered.isEmpty()) return null;
        else return filtered.getFirst();
    }

    public Ship removeShip(@NotNull Position position) {
        return  removeShip(position, false);
    }

    public Ship removeShip(@NotNull Position position, boolean force) {
        if (boardType == BoardType.ENEMY && !force) return null;

        Optional<Ship> target = ships
                .stream()
                .filter(ship -> ship.getGameBoardPositions().contains(position))
                .findFirst();

        if (target.isEmpty()) return null;

        ships.remove(target.get());
        shipsPos.removeAll(target.get().getGameBoardPositions());
        shipCounter--;

        return target.get();
    }

    public boolean canShootTile(int x, int y) {
        if (boardType == BoardType.MY) return false;
        else if (shootCounter > 4) return false;
        else return !allPositions.contains(new DetailedPosition(new Position(x, y), true));
    }

    public boolean shootTile(int x, int y) {
        return shootTile(x, y, false);
    }

    public boolean shootTile(int x, int y, boolean force) {
        if (!canShootTile(x, y) && !force) return false;

        Optional<PowerUp> targetPowerUp = powerUps
                .stream()
                .filter(powerUp -> powerUp.getPositions().stream().anyMatch(it -> it.x() == x && it.y() == y))
                .findFirst();
        Optional<Ship> targetShip = ships
                .stream()
                .filter(ship -> ship.getGameBoardPositions().stream().anyMatch(it -> it.x() == x && it.y() == y))
                .findFirst();

        Optional<DetailedPosition> position = allPositions.stream().filter(it -> it.position.x() == x && it.position.y() == y).findFirst();

        if (position.isEmpty()) return false;

        int index = allPositions.indexOf(position.get());

        allPositions.set(index, new DetailedPosition(allPositions.get(index).position(), true));

        targetShip.ifPresent(ignored -> shipsPos.removeIf(it -> it.x() == x && it.y() == y));
        targetPowerUp.ifPresent(ignored -> powerUpsPos.removeIf(it -> it.x() == x && it.y() == y));

        if (targetPowerUp.isPresent() && targetPowerUp.get() instanceof Bomb bomb) {
            powerUps.remove(bomb);

            if (!force) for (int i = 0; i < 3; i++) {
                int maxIndex = (int) allPositions.stream().filter(it -> !it.revealed()).count();

                if (maxIndex == 0) break;

                int randomIndex = new Random().nextInt(maxIndex);
                Position randomPos = allPositions.get(randomIndex).position();
                shootTile(randomPos.x(), randomPos.y());
            }
        }

        if (onShoot != null) onShoot.accept(new Position(x, y));
        if (onGameOver != null && !gameOver && shipsPos.isEmpty()) {
            onGameOver.run();
            gameOver = true;
        }

        return true;
    }

    public void generateTokens() {
        if (boardType != BoardType.MY) return;

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

        if (onTokensChanged != null) onTokensChanged.accept(tokens);
    }

    public boolean canUsePowerUp(PowerUp powerUp) {
        if (powerUp.getCost(boardSize, maxShipSize) > tokens) return false;

        boolean success;
        HashSet<Position> emptyHiddenPositions = new HashSet<>(allPositions
                .stream()
                .filter(it -> !it.revealed()
                        && !shipsPos.contains(it.toPosition())
                        && !powerUpsPos.contains(it.toPosition()))
                .map(DetailedPosition::toPosition)
                .toList());
        List<Position> emptyRevealedPositions = allPositions
                .stream()
                .filter(it -> it.revealed()
                        && !shipsPos.contains(it.toPosition())
                        && !powerUpsPos.contains(it.toPosition()))
                .map(DetailedPosition::toPosition)
                .toList();

        switch (powerUp) {
            case Bomb bomb -> success = boardType == BoardType.MY && emptyHiddenPositions.contains(bomb.position());
            case Farm farm -> success = boardType == BoardType.MY && emptyHiddenPositions.containsAll(farm.positions());
            case Radar radar -> success = boardType == BoardType.ENEMY && emptyRevealedPositions.contains(radar.position());
            case Bomber ignored -> success = boardType == BoardType.ENEMY;
            default -> success = false;
        }

        return success;
    }

    public boolean usePowerUp(PowerUp powerUp) {
        return usePowerUp(powerUp, false);
    }

    public boolean usePowerUp(PowerUp powerUp, boolean force) {
        return switch (powerUp) {
            case Bomb bomb -> useBombPowerUp(bomb, force);
            case Farm farm -> useFarmPowerUp(farm, force);
            case Radar radar -> useRadarPowerUp(radar, force);
            case Bomber bomber -> useBomberPowerUp(bomber, force);
            case null, default -> false;
        };
    }

    public boolean useBombPowerUp(Bomb bomb) {
        return useBombPowerUp(bomb, false);
    }

    public boolean useBombPowerUp(Bomb bomb, boolean force) {
        if (!canUsePowerUp(bomb) && !force) return false;

        powerUps.add(bomb);
        powerUpsPos.add(bomb.position());
        if (!force) tokens -= bomb.getCost(boardSize, maxShipSize);
        if (onTokensChanged != null) onTokensChanged.accept(tokens);

        return true;
    }

    public boolean useFarmPowerUp(Farm farm) {
        return useFarmPowerUp(farm, false);
    }

    public boolean useFarmPowerUp(Farm farm, boolean force) {
        if (!canUsePowerUp(farm) && !force) return false;

        powerUps.add(farm);
        powerUpsPos.addAll(farm.positions());
        if (!force) tokens -= farm.getCost(boardSize, maxShipSize);
        if (onTokensChanged != null) onTokensChanged.accept(tokens);

        return true;
    }

    public boolean useRadarPowerUp(Radar radar) {
        return useRadarPowerUp(radar, false);
    }

    public boolean useRadarPowerUp(Radar radar, boolean force) {
        if (!canUsePowerUp(radar) && !force) return false;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) continue;

                shootTile(radar.position().x() + j, radar.position().y() + i, force);
            }
        }

        powerUps.add(radar);
        powerUpsPos.add(radar.position());
        if (!force) tokens -= radar.getCost(boardSize, maxShipSize);
        if (onTokensChanged != null) onTokensChanged.accept(tokens);

        return true;
    }

    public boolean useBomberPowerUp(Bomber bomber) {
        return useBomberPowerUp(bomber, false);
    }

    public boolean useBomberPowerUp(Bomber bomber, boolean force) {
        if (!canUsePowerUp(bomber) && !force) return false;

        boolean isHorizontal = bomber.direction() == Bomber.Direction.HORIZONTAL;
        boolean isVertical = bomber.direction() == Bomber.Direction.VERTICAL;

        List<Position> positions = allPositions
                .stream()
                .filter(it -> {
                    if (isHorizontal && it.position.y() == bomber.position().y()) {
                        return true;
                    } else return isVertical && it.position.x() == bomber.position().x();
                })
                .map(DetailedPosition::toPosition)
                .toList();

        positions.forEach(pos -> shootTile(pos.x(), pos.y(), force));

        if (!force) tokens -= bomber.getCost(boardSize, maxShipSize);
        if (onTokensChanged != null) onTokensChanged.accept(tokens);

        return true;
    }

    public void setTokens(int value) {
        this.tokens = value;
    }

    public void onTokensChanged(Consumer<Integer> function) {
        this.onTokensChanged = function;
    }

    public List<Ship> ships() {
        return ships;
    }

    public boolean isGameOver() {
        return shipsPos.isEmpty();
    }

    public enum BoardSize { SMALL, MEDIUM, BIG }
    public enum BoardType { MY, ENEMY }

    public record DetailedPosition(Position position, boolean revealed) {
        Position toPosition() {
            return position();
        }
    }
}
