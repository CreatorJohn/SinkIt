package com.creatorjohn.helpers;

import com.creatorjohn.helpers.powerups.PowerUp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class JCursor {
    public static @Nullable Cursor create(@NotNull Type type, @Nullable Direction direction) {
        Cursor cursor = null;
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            StringBuilder path = new StringBuilder();
            path.append("/assets/");
            path.append(type.name().toLowerCase());
            if (direction != null) path.append("-").append(direction.name().toLowerCase());
            path.append("-cursor.png");
            InputStream input = JCursor.class.getResourceAsStream(path.toString());

            if (input == null) return cursor;

            BufferedImage image = ImageIO.read(input);
            double x = toolkit.getBestCursorSize(128, 128).getWidth() / 2;
            double y = toolkit.getBestCursorSize(128, 128).getHeight() / 2;
            cursor = toolkit.createCustomCursor(image, new Point((int) x, (int) y), "attack");

            input.close();
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }

        return cursor;
    }
    public static @Nullable Cursor create(@NotNull Type type) {
        return JCursor.create(type, null);
    }

    public enum Type { BOMB, BOMBER, FARM, SHIP, SHOOT }
    public enum Direction { TOP, RIGHT, BOTTOM, LEFT }
}
