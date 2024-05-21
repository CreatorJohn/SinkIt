package com.creatorjohn;

import com.creatorjohn.components.JLogo;
import com.creatorjohn.handlers.Client;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.events.*;
import com.creatorjohn.screens.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

class Application {

    Application() {
        JFrame frame = new JFrame("SinkIt!");
        frame.setLayout(new GridBagLayout());
//        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
//        frame.setResizable(false);
        Dimension minSize = new Dimension(1080, 720);
        frame.setMinimumSize(minSize);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(0x000f24));

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension d=frame.getSize();
                Dimension minD=frame.getMinimumSize();
                if(d.width<minD.width)
                    d.width=minD.width;
                if(d.height<minD.height)
                    d.height=minD.height;
                frame.setSize(d);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        BufferedImage image = JLogo.loadImage("logo.png");

        MenuScreen main = new MenuScreen(frame);

        frame.setIconImage(image);
        frame.add(main.instance(), c);
        frame.setVisible(true);
        frame.pack();
    }

    static public void main(String[] args) {
        SwingUtilities.invokeLater(Application::new);
    }
}

