package com.creatorjohn.helpers.entities;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Farm power-up can be placed on game board through game.
 * It generates bonus tokens every round depending on count of surrounded occupied tiles by unrevealed ship tiles.
 * When Farm tile hit, bonus token production decreases by 1/4 of production.
 * When surrounding unrevealed ship tile gets revealed, bonus production decreases by 1.
 */
final public class Farm extends PowerUp {
    private final List<Position> positions;

    public Farm(List<Position> positions) {
        super(Type.FARM);
        this.positions = positions;
    }

    public List<Position> positions() {
        return positions;
    }

    public int getGenerates(List<Position> revealed, List<Position> shipsPositions) {
        Optional<Integer> result = positions
                .stream()
                .map(pos -> getSurroundingTiles(shipsPositions, pos).size())
                .reduce(Integer::sum);
        long notRevealed = positions
                .stream()
                .filter(pos -> !revealed.contains(pos))
                .count();

        return Math.round((float) (result.orElse(0) * notRevealed) / positions().size());
    }

    private List<Position> getSurroundingTiles(List<Position> tiles, Position pos) {
        return tiles.stream().filter(tile -> {
            boolean isTop = tile.x() == pos.x() && tile.y() == pos.y() - 1;
            boolean isRight = tile.x() == pos.x() + 1 && tile.y() == pos.y();
            boolean isBottom = tile.x() == pos.x() && tile.y() == pos.y() + 1;
            boolean isLeft = tile.x() == pos.x() - 1 && tile.y() == pos.y();

            return isTop || isRight || isBottom || isLeft;
        }).toList();
    }

    @Override
    public List<Position> getPositions() {
        return positions();
    }

    @Override
    public int getCost(GameBoard.@NotNull BoardSize boardSize, int maxShipSize) {
        double price = -1;

        switch (boardSize) {
            case SMALL -> price = JConfig.smallMapShipCount * maxShipSize * 3.5;
            case MEDIUM -> price = JConfig.mediumMapShipCount * maxShipSize * 3.5;
            case BIG -> price = JConfig.bigMapShipCount * maxShipSize * 3.5;
        }

        return (int) price;
    }

}
