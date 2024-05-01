package com.creatorjohn;

import com.creatorjohn.handlers.Client;
import com.creatorjohn.helpers.events.*;
import com.creatorjohn.screens.*;

import javax.swing.*;
import java.awt.*;

class Application {

    Application() {
        JFrame frame = new JFrame("SinkIt!");
        frame.setLayout(new GridBagLayout());
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        //frame.setResizable(false);
        frame.setPreferredSize(new Dimension(1080, 720));
        frame.setMinimumSize(frame.getPreferredSize());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.getContentPane().setBackground(new Color(0x000f24));

        MenuScreen main = new MenuScreen(frame);

        frame.add(main.instance(), c);
    }

    static public void main(String[] args) {
        Client.EventHandlerConfig handler = new Client.EventHandlerConfig() {
            @Override
            public void onGameCreated(GameCreatedEvent event) {
                System.out.println("Game created: " + event.gameID);
            }

            @Override
            public void onGameJoined(GameJoinedEvent event) {
                System.out.println("Joined | Ships incoming: " + event.ships + " | Shot tiles: " + event.shotTiles);
            }

            @Override
            public void onGameFinished(GameFinishedEvent event) {
                System.out.println("Game finished with status: " + event.status);
            }

            @Override
            public void onGameUpdated(GameUpdatedEvent event) {
                System.out.println("Game updated with current player: " + event.currentPlayer + " & shot tiles: " + event.shotTiles);
            }

            @Override
            public void onPlayerJoined(PlayerJoinedEvent event) {
                System.out.println("Player joined!");
            }

            @Override
            public void onPlayerLeft(PlayerLeftEvent event) {
                System.out.println("Player left!");
            }
        };
        SwingUtilities.invokeLater(Application::new);
    }
}

