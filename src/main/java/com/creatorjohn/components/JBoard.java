package com.creatorjohn.components;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.Bomb;
import com.creatorjohn.helpers.entities.Entity;
import com.creatorjohn.helpers.entities.Farm;
import com.creatorjohn.helpers.entities.Radar;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class JBoard extends JPanel {
    final private List<Position> hoveredPositions = new ArrayList<>();
    final private List<Position> usedPositions = new ArrayList<>();
    final private int boardSize;
    final private int tileSize;
    final private boolean enemy;
    private Function<Position, List<Position>> onHover;
    private Consumer<List<Position>> onLeftClick;
    private Consumer<Position> onRightClick;

    public JBoard(GameBoard.BoardSize size, Dimension boardDimension, int tileSize, boolean enemy) {
        boardSize = getBoardEdgeTiles(size);
        this.tileSize = tileSize;
        this.enemy = enemy;

        this.setLayout(new GridLayout(0, boardSize, 5, 5));
        this.setOpaque(false);
        this.setMaximumSize(boardDimension);
        this.setMinimumSize(boardDimension);

        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                JTile<?> tile = new JTile<>(new Dimension(tileSize, tileSize), enemy);

                Position thisPos = new Position(x, y);

                tile.addMouseListener(new MouseListener() {
                    boolean active = false;

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1 && onLeftClick != null)
                            onLeftClick.accept(hoveredPositions);
                        else if (e.getButton() == MouseEvent.BUTTON3 && onRightClick != null)
                            onRightClick.accept(thisPos);
                        else System.err.println("You can't do this action!");
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        for (Position pos : hoveredPositions) {
                            if (pos.x() < 0 || pos.y() < 0 || usedPositions.contains(pos)) continue;
                            JBoard.this.getComponent(index(pos)).setBackground(JTile.destroyedBg);
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        for (Position pos : hoveredPositions) {
                            if (pos.x() < 0 || pos.y() < 0 || usedPositions.contains(pos)) continue;
                            if (active) JBoard.this.getComponent(index(pos)).setBackground(JTile.hoveredBg);
                            else JBoard.this.getComponent(index(pos)).setBackground(JTile.defaultBg);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        active = true;
                        List<Position> hovered = onHover != null ? onHover.apply(thisPos) : List.of();
                        hover(hovered);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        active = false;
                        unhover();
                    }
                });

                this.add(tile);
            }
        }
    }

    public void onHover(Function<Position, List<Position>> onHover) {
        this.onHover = onHover;
    }

    public void onLeftClick(Consumer<List<Position>> onLeftClick) {
        this.onLeftClick = onLeftClick;
    }

    public void onRightClick(Consumer<Position> onRightClick) {
        this.onRightClick = onRightClick;
    }

    public void destroy() {
        destroy(hoveredPositions);
    }

    public void destroy(@NotNull List<Position> positions) {
        for (Position pos : positions) {
            if (usedPositions.contains(pos)) continue;
            JBoard.this.getComponent(index(pos)).setBackground(JTile.destroyedBg);
            usedPositions.add(pos);
        }
    }

    public void hover(@NotNull List<Position> positions) {
        for (Position pos : positions) {
            if (overEdgeCheck(pos, boardSize) || usedPositions.contains(pos)) continue;
            JBoard.this.getComponent(index(pos)).setBackground(JTile.hoveredBg);
            hoveredPositions.add(pos);
        }
    }

    public void unhover(List<Position> positions) {
        for (Position pos : positions) {
            if (usedPositions.contains(pos)) continue;
            JBoard.this.getComponent(index(pos)).setBackground(JTile.defaultBg);
            hoveredPositions.remove(pos);
        }
    }

    public void unhover() {
        for (Position pos : hoveredPositions) {
            if (usedPositions.contains(pos)) continue;

            JBoard.this.getComponent(index(pos)).setBackground(JTile.defaultBg);
        }
        hoveredPositions.clear();
    }

    public void placeShip(@NotNull List<Position> positions) {
        for (Position position : positions) {
            this.getComponent(index(position)).setBackground(Color.GRAY);
            if (!usedPositions.contains(position)) usedPositions.add(position);
        }
    }

    public void removeShip(@NotNull List<Position> positions) {
        for (Position position : positions) {
            this.getComponent(index(position)).setBackground(JTile.defaultBg);
            usedPositions.remove(position);
        }
    }

    public void destroyShip(@NotNull List<Position> positions) {
        for (Position position : positions) {
            this.getComponent(index(position)).setBackground(Color.BLACK);
            if (!usedPositions.contains(position)) usedPositions.add(position);
        }
    }

    public void placeBomb(@NotNull Bomb bomb) {
        Entity<Bomb> entity = new Entity<>(JLogo.loadImage("bomb-cursor.png"), new Dimension(tileSize, tileSize), bomb);
        ((JTile<Bomb>) JBoard.this.getComponent(index(bomb.position()))).updateEntity(entity);
        Graphics2D g = (Graphics2D) JBoard.this.getComponent(index(bomb.position())).getGraphics();
        entity.display(g);
        g.dispose();
    }

    public void placeFarm(@NotNull Farm farm) {
        Entity<Farm> entity = new Entity<>(JLogo.loadImage("farm-cursor.png"), new Dimension(tileSize, tileSize), farm);

        for (Position pos : farm.getPositions()) {
            ((JTile<Farm>)JBoard.this.getComponent(index(pos))).updateEntity(entity);
            Graphics2D g = (Graphics2D) JBoard.this.getComponent(index(pos)).getGraphics();
            entity.display(g);
            g.dispose();
        }
    }

    public void placeRadar(@NotNull Radar radar) {
        Entity<Radar> entity = new Entity<>(JLogo.loadImage("radar-cursor.png"), new Dimension(tileSize, tileSize), radar);
        ((JTile<Radar>) JBoard.this.getComponent(index(radar.position()))).updateEntity(entity);
        Graphics2D g = (Graphics2D) JBoard.this.getComponent(index(radar.position())).getGraphics();
        entity.display(g);
        g.dispose();
    }

    private int index(@NotNull Position coord) {
        return coord.y() * boardSize + coord.x();
    }

    private boolean overEdgeCheck(@NotNull Position position, int boardSize) {
        boolean top = position.y() < 0;
        boolean left = position.x() < 0;
        boolean right = position.x() >= boardSize;
        boolean bottom = position.y() >= boardSize;

        return top || left || bottom || right;
    }

    public static int getBoardEdgeTiles(GameBoard.@NotNull BoardSize size) {
        return switch (size) {
            case SMALL -> 10;
            case MEDIUM -> 14;
            case BIG -> 18;
        };
    }

    public static int getShipsRequiredCount(GameBoard.@NotNull BoardSize size) {
        return switch (size) {
            case SMALL -> JConfig.smallMapShipCount;
            case MEDIUM -> JConfig.mediumMapShipCount;
            case BIG -> JConfig.bigMapShipCount;
        };
    }
}
