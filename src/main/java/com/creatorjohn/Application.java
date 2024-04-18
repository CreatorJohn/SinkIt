package com.creatorjohn;

import com.creatorjohn.screens.*;

import javax.swing.*;
import java.awt.*;

class Application {

    Application() {
        JFrame frame = new JFrame("SinkIt!");
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        frame.setResizable(false);
        frame.setSize(600, 480);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        MenuScreen main = new MenuScreen(frame);

        frame.add(main.getInstance());
        frame.setBackground(Color.GRAY);
    }

    static public void main(String[] args) {
        SwingUtilities.invokeLater(Application::new);
    }
}

