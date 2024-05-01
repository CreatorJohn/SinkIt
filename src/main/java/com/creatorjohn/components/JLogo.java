package com.creatorjohn.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;

public class JLogo extends JPanel {
    private BufferedImage image;
    private BufferedImage reference;

    public JLogo(String assetName, int width, int height) {
        this.setOpaque(false);
        try {
            InputStream input = getClass().getResourceAsStream("/assets/" + assetName);

            if (input == null) throw new InputMismatchException("Invalid asset name!");

            reference = ImageIO.read(input);
            image = resizeImage(reference, width, height);
            this.setPreferredSize(new Dimension(width, height));

            input.close();
        } catch (IOException e) {
            System.err.println("JLogo >> Failed to load logo!");
        }
    }

    public void updateSize(int width, int height) {
        image = resizeImage(reference, width, height);
        this.setPreferredSize(new Dimension(width, height));
        this.revalidate();
        this.repaint();
    }

    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, original.getType());
        Graphics2D g = resized.createGraphics();
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(image, 0, 0, null);

        g2.dispose();
    }
}
