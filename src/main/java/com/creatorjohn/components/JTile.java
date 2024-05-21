package com.creatorjohn.components;

import com.creatorjohn.helpers.JCursor;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.Entity;
import com.creatorjohn.helpers.entities.GameComponent;
import com.creatorjohn.helpers.entities.PowerUp;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class JTile<T extends GameComponent> extends JPanel {
    final public static Color destroyedBg = new Color(0x006E7A);
    final public static Color defaultBg = new Color(0x008D88);
    final public static Color hoveredBg = new Color(0x32AC8B);
    final private AtomicBoolean revealed = new AtomicBoolean(false);
    private Entity<T> entity;

    public JTile(@NotNull Dimension tileDimension, boolean enemy) {
        if (enemy) setCursor(JCursor.create(JCursor.Type.SHOOT));
        this.setPreferredSize(tileDimension);
        this.setBackground(defaultBg);
    }

    public void reveal() {
        revealed.set(true);
    }

    public void unreveal() {
        revealed.set(false);
    }

    public void updateEntity(Entity<T> entity) {
        this.entity = entity;
        this.revalidate();
        this.repaint();
    }

    public Entity<T> entity() {
        return entity;
    }

    public boolean revealed() {
        return revealed.get();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics = (Graphics2D) g;

        if (entity != null) entity.display(graphics);

        g.dispose();
    }
}
