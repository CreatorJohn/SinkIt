package com.creatorjohn.components;

import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.PowerUp;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.creatorjohn.helpers.JConfig.buttonInsets;

public class JShipPicker {
    final private Color buttonBg = new JButton().getBackground();
    final private List<ShipContainer> infos = new ArrayList<>();
    final private JPanel instance = new JPanel();
    private ShipContainer selected = null;
    private Consumer<List<Position>> onRotate;

    public JShipPicker(@NotNull List<ShipInfo> infos, int size, int space, Type type) {
        if (type == Type.HORIZONTAL) instance.setLayout(new FlowLayout(FlowLayout.CENTER, space, 0));
        else instance.setLayout(new BoxLayout(instance, BoxLayout.Y_AXIS));

        instance.setOpaque(false);
        boolean isFirst = true;

        for (ShipInfo info : infos) {
            if (info.offsets.size() >= JConfig.maxShipSize) continue;

            JButton button = createButton(info, size);

            if (!isFirst && type == Type.VERTICAL) instance.add(new JSpacer(space, JSpacer.Direction.Y_Axis));

            isFirst = false;

            instance.add(button);
        }
    }

    private @NotNull JButton createButton(@NotNull ShipInfo info, int size) {
        JButton button;

        if (info.label() != null) button = new JButton(info.label());
        else button = new JButton(info.icon());

        button.setMinimumSize(new Dimension(size, size));
        button.setPreferredSize(new Dimension(size, size));
        button.setMaximumSize(new Dimension(size, size));
        button.setFocusPainted(false);

        button.setMargin(buttonInsets);
        button.addActionListener(e -> {
            if (selected != null && selected.button == button) selected = null;
            else selected = new ShipContainer(button, info);

            infos.forEach(it -> {
                if (selected != null && selected.button == it.button) it.button.setBackground(Color.LIGHT_GRAY);
                else it.button.setBackground(buttonBg);
            });
        });
        button.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    info.rotate();
                    if (onRotate != null) onRotate.accept(selectedPositions());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        infos.add(new ShipContainer(button, info));

        return button;
    }

    public void onRotate(Consumer<List<Position>> onRotate) {
        this.onRotate = onRotate;
    }

    public void moveSelected(@NotNull Position pos) {
        if (selected == null) return;

        selected.info.move(pos);
    }

    public @NotNull List<Position> selectedPositions() {
        if (selected == null) return List.of();

        return selected.info.positions();
    }

    public JPanel instance() {
        return instance;
    }
    public static class ShipInfo {
        final private BufferedImage image;
        final private String label;
        private List<Position> offsets;
        private Position origin = new Position(0, 0);

        public ShipInfo(BufferedImage image, List<Position> offsets) {
            this.image = image;
            this.offsets = offsets;
            this.label = null;
        }

        public ShipInfo(String label, List<Position> offsets) {
            this.image = null;
            this.offsets = offsets;
            this.label = label;
        }

        public void move(Position newCoord) {
            origin = newCoord;
        }

        public void rotate() {
            this.offsets = offsets.stream().map(it -> new Position(-it.y(), it.x())).toList();
        }

        public @NotNull List<Position> positions() {
            List<Position> positions = new ArrayList<>();

            positions.add(origin);

            for (Position coord : offsets) {
                positions.add(new Position(origin.x() + coord.x(), origin.y() + coord.y()));
            }

            return positions;
        }

        public String label() {
            return label;
        }

        public Icon icon() {
            return image != null ? new ImageIcon(image) : null;
        }
    }
    private record ShipContainer(JButton button, ShipInfo info) {}
    public enum Type { HORIZONTAL, VERTICAL }
}
