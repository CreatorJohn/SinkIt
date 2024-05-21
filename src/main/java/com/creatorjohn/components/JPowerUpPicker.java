package com.creatorjohn.components;

import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static com.creatorjohn.helpers.JConfig.buttonInsets;

public class JPowerUpPicker {
    final private Color buttonBg = new JButton().getBackground();
    final private List<ButtonInfo> buttons;
    final private JPanel instance;
    final private int boardSize;
    private Consumer<List<Position>> onChangeDirection;
    private GameBoard.BoardType boardType;
    private Consumer<PowerUp> onSelected;
    private PowerUp selected;

    public JPowerUpPicker(@NotNull List<PowerUpInfo> infos, int boardSize, int iconSize, int xSpace) {
        this(infos, boardSize, iconSize, xSpace, 0);
    }

    public JPowerUpPicker(@NotNull List<PowerUpInfo> infos, int boardSize, int iconSize, int xSpace, int ySpace) {
        instance = new JPanel(new FlowLayout(FlowLayout.CENTER, xSpace, ySpace));
        buttons = new ArrayList<>(infos.size());
        this.boardSize = boardSize;
        instance.setOpaque(false);

        for (PowerUpInfo info : infos) {
            JButton button = createPowerUpButton(info.assetName, iconSize, info.type, () -> {
                if (selected != null && selected.type == info.type) selected = null;
                else {
                    switch (info.type) {
                        case BOMB -> selected = new Bomb(new Position(0, 0));
                        case BOMBER -> selected = new Bomber(new Position(0, 0), Bomber.Direction.VERTICAL);
                        case FARM -> selected = new Farm(List.of(
                                new Position(0, 0),
                                new Position(0, 1),
                                new Position(1, 0),
                                new Position(1, 1)
                        ));
                        case RADAR -> selected = new Radar(new Position(0, 0));
                    }
                }
                
                if (onSelected != null) onSelected.accept(selected);
            });

            buttons.add(new ButtonInfo(info.type, button));
            instance.add(button);
        }
    }

    private @NotNull JButton createPowerUpButton(@NotNull String assetName, int size, PowerUp.Type type, Runnable onClick) {
        BufferedImage image = JLogo.loadImage(assetName);
        Icon icon = new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
        JButton button = new JButton(icon);
        button.setMargin(buttonInsets);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            if (onClick != null) onClick.run();

            PowerUp selected = selected();
            for (ButtonInfo info : buttons) {
                if (selected != null && info.type == selected.type) info.button.setBackground(Color.LIGHT_GRAY);
                else info.button.setBackground(buttonBg);
            }
        });
        if (type == PowerUp.Type.BOMBER) button.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                List<Position> positions = List.of();
                if (selected == null) return;
                if (e.isShiftDown()) {
                    Bomber bomber = (Bomber)selected;
                    Bomber.Direction direction = bomber.direction() == Bomber.Direction.VERTICAL
                            ? Bomber.Direction.HORIZONTAL
                            : Bomber.Direction.VERTICAL;
                    positions = changeDirection(direction);
                }
                if (onChangeDirection != null) onChangeDirection.accept(positions);
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        return button;
    }

    private List<Position> changeDirection(@NotNull Bomber.Direction direction) {
        if (selected == null) return List.of();
        else if (selected.type != PowerUp.Type.BOMBER) return selected.getPositions();

        Bomber bomber = (Bomber) selected;
        selected = new Bomber(bomber.position(), direction);

        return move(bomber.position());
    }

    public void onChangeDirection(Consumer<List<Position>> onChangeDirection) {
        this.onChangeDirection = onChangeDirection;
    }

    public void onSelected(Consumer<PowerUp> onSelected) {
        this.onSelected = onSelected;
    }

    public @NotNull List<Position> move(Position position) {
        if (selected == null) return List.of(position);

        if (selected.type == PowerUp.Type.FARM) {
            List<Position> positions = new ArrayList<>(selected.getPositions());
            Position origin = positions.getFirst();
            int dx = position.x() - origin.x(), dy = position.y() - origin.y();
            int index = 0;

            for (Position pos : positions) {
                positions.set(index, new Position(pos.x() + dx, pos.y() + dy));
                index++;
            }
            selected = new Farm(positions);
            return positions;
        }

        switch (selected) {
            case Bomb ignored -> selected = new Bomb(position);
            case Radar ignored -> selected = new Radar(position);
            case Bomber bomber -> {
                List<Position> positions = new ArrayList<>();

                for (int i = 0; i < boardSize; i++) {
                    if (bomber.direction() == Bomber.Direction.VERTICAL) {
                        positions.add(new Position(position.x(), i));
                    } else {
                        positions.add(new Position(i, position.y()));
                    }
                }

                selected = new Bomber(position, bomber.direction());

                return positions;
            }
            default -> {}
        }

        return selected.getPositions();
    }

    public void setBoardType(GameBoard.BoardType boardType) {
        this.boardType = boardType;
    }

    public GameBoard.BoardType selectedBoardType() {
        return boardType;
    }

    public PowerUp selected() {
        return selected;
    }

    public JPanel instance() {
        return instance;
    }

    public record PowerUpInfo(@NotNull String assetName, @NotNull PowerUp.Type type) {}
    private record ButtonInfo(PowerUp.Type type, JButton button) {}
}
