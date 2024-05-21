package com.creatorjohn.helpers.entities;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity<T> {
    final private T instance;
    final private Dimension size;
    private BufferedImage image;

    public Entity(@NotNull BufferedImage image, @NotNull Dimension size, @NotNull T instance) {
        this.image = image;
        this.size = size;
        this.instance = instance;
    }

    public void updateImage(BufferedImage image) {
        this.image = image;
    }

    public T instance() {
        return instance;
    }

    public void display(Graphics2D g) {
        g.drawImage(image, 0, 0, size.width, size.height, null);
    }
}
