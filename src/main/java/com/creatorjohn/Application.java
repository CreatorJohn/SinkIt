package com.creatorjohn;

import com.creatorjohn.handlers.Client;
import com.creatorjohn.handlers.Server;
import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.server.Player;
import com.creatorjohn.screens.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

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

