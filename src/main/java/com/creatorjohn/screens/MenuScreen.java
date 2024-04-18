package com.creatorjohn.screens;

import javax.swing.*;
import java.awt.*;

final public class MenuScreen extends Screen {

    public MenuScreen(JFrame frame) {
        super(frame);
        instance.setLayout(new GridLayout(0, 1, 0, 10));
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> navigateBack());

        instance.add(exitButton);
    }
}
