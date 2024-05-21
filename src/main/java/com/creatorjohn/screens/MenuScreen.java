package com.creatorjohn.screens;

import com.creatorjohn.components.JLogo;
import com.creatorjohn.components.JLogin;
import com.creatorjohn.components.JRadioRow;
import com.creatorjohn.components.JRegister;
import com.creatorjohn.handlers.Client;
import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.entities.Ship;
import com.creatorjohn.helpers.events.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.creatorjohn.helpers.JConfig.*;

final public class MenuScreen extends Screen {
    final private JPanel menuWrapper = new JPanel(new GridLayout(0, 1, 0, 10));
    final private JPanel registerWrapper = new JPanel();
    final private JPanel loginWrapper = new JPanel();
    final private JFrame frame;
    private Thread waitingThread;
    private Client client;

    public MenuScreen(JFrame frame) {
        super(frame);
        this.frame = frame;
        instance.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLogo logo = new JLogo("logo.png", frame.getWidth() / 2, frame.getWidth() / 2);
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = 0;
        instance.add(logo, c);

        createLogin();
        createRegister();
        createMenu();

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        instance.add(loginWrapper, c);

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component comp = e.getComponent();
                int newHeight = comp.getHeight() / 2;
                int newWidth = comp.getWidth() / 2;
                int newSize = Math.min(newHeight, newWidth);

                logo.updateSize(newSize, newSize);
                instance.revalidate();
                instance.repaint();
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
    }

    private void createLogin() {
        GridBagConstraints c = new GridBagConstraints();
        loginWrapper.setOpaque(false);
        JLogin loginForm = new JLogin(frame, new JLogin.Config() {
            @Override
            public String login(String username, String password) {
                if (username.isBlank()) return "Username is required!";
                else if (password.isBlank()) return "Password is required!";

                client = Client.connect(username, password, serverAddress, serverPort);

                if (client == null) return "Failed to connect to server!";

                client.sendEvent(new LoginEvent(username, password));

                LoginResponseEvent response = client.receiveEvent(LoginResponseEvent.class);

                if (response.error == null) {
                    menuWrapper.add(createString("Username: " + username), 0);
                    instance.remove(loginWrapper);
                    c.gridy = 1;
                    instance.add(menuWrapper, c);
                    instance.revalidate();
                    instance.repaint();
                    client.handleIncomingEvents();
                }

                return response.error;
            }

            @Override
            public void toRegister() {
                instance.remove(loginWrapper);
                instance.add(registerWrapper, c);
                instance.revalidate();
                instance.repaint();
            }
        });
        c.gridy = 1;
        loginWrapper.add(loginForm, c);
    }

    private void createRegister() {
        GridBagConstraints c = new GridBagConstraints();
        registerWrapper.setOpaque(false);
        JRegister registerForm = new JRegister(frame, new JRegister.Config() {
            @Override
            public String register(String username, String password, String rePassword) {
                if (username.isBlank() || username.length() < 4) return "Username must be at least 4 characters long!";
                else if (password.isBlank() || password.length() < 6) return "Password must be at least 6 characters long!";
                else if (!Objects.equals(password, rePassword)) return "Passwords don't match!";

                client = Client.connect(username, password, serverAddress, serverPort);

                if (client == null) return "Failed to connect to server!";

                client.sendEvent(new RegisterEvent(username, password));

                RegisterResponseEvent response = client.receiveEvent(RegisterResponseEvent.class);

                if (response.error == null) {
                    menuWrapper.add(createString("Username: " + username), 0);
                    c.gridy = 1;
                    instance.remove(registerWrapper);
                    instance.add(menuWrapper, c);
                    instance.revalidate();
                    instance.repaint();
                    client.handleIncomingEvents();
                }

                return response.error;
            }

            @Override
            public void toLogin() {
                c.gridy = 1;
                instance.remove(registerWrapper);
                instance.add(loginWrapper, c);
                instance.revalidate();
                instance.repaint();
            }
        });
        c.gridy = 1;
        registerWrapper.add(registerForm, c);
    }

    private void createMenu() {
        menuWrapper.setOpaque(false);

        JRadioRow<GameBoard.BoardSize> mapSizeSelector = new JRadioRow<>(
                "Map size:",
                List.of(GameBoard.BoardSize.SMALL, GameBoard.BoardSize.MEDIUM, GameBoard.BoardSize.BIG),
                GameBoard.BoardSize::name
        );
        menuWrapper.add(mapSizeSelector);

        JButton createGame = new JButton("Create game");
        createGame.setFocusPainted(false);
        createGame.setFont(buttonFont);
        createGame.setMargin(buttonInsets);
        createGame.addActionListener(e -> {
            if (waitingThread != null) return;

            waitingThread = new Thread(() -> {
                URL iconUrl = getClass().getResource("/assets/loader18.gif");

                if (iconUrl != null) createGame.setIcon(new ImageIcon(iconUrl));

                client.onGameCreated(result -> {
                    if (result.gameID.isBlank()) {
                        JConfig.dialogError("Game create error", "Failed to create game!");

                        return;
                    }

                    GameScreen gameScreen = new GameScreen(frame, client, mapSizeSelector.selected(), result.gameID);
                    navigateTo(gameScreen);

                    waitingThread = null;
                    createGame.setIcon(null);
                });
                client.sendEvent(new CreateGameEvent(mapSizeSelector.selected()));
            });
            waitingThread.start();
        });
        menuWrapper.add(createGame);

        JPanel joinRow = new JPanel(new GridBagLayout());
        joinRow.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();

        JTextField gameIDField = new JTextField(20);
        gameIDField.setFont(fieldFont);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.weightx = 3;
        joinRow.add(gameIDField, c);

        JButton joinGameButton = new JButton("Join game");
        joinGameButton.setFocusPainted(false);
        joinGameButton.setFont(buttonFont);
        joinGameButton.setMargin(buttonInsets);
        joinGameButton.addActionListener(e -> {
            if (waitingThread != null) return;

            waitingThread = new Thread(() -> {
                String gameID = gameIDField.getText().trim();

                if (gameID.isBlank()) {
                    JConfig.dialogError("Joining error", "Game ID is required!");
                    waitingThread = null;
                    return;
                }

                URL iconUrl = getClass().getResource("/assets/loader18.gif");

                if (iconUrl != null) joinGameButton.setIcon(new ImageIcon(iconUrl));

                client.onGameJoined(result -> {
                    if (!result.success) JConfig.dialogError("Joining error", "Failed to join the game!");
                    else {
                        List<Ship> myShips = result.ships.my();
                        List<Ship> enemyShips = result.ships.enemy();
                        navigateTo(new GameScreen(frame, client, mapSizeSelector.selected(), gameID, myShips, enemyShips));
                        gameIDField.setText("");
                    }

                    waitingThread = null;
                    joinGameButton.setIcon(null);
                });
                client.sendEvent(new JoinGameEvent(gameID));
            });
            waitingThread.start();
        });
        c.gridx = 1;
        c.weightx = 1;
        joinRow.add(joinGameButton, c);

        menuWrapper.add(joinRow);

        JButton statisticsButton = new JButton("Statistics");
        statisticsButton.setMargin(buttonInsets);
        statisticsButton.setFont(buttonFont);
        statisticsButton.addActionListener(e -> {
            if (waitingThread != null) return;

            waitingThread = new Thread(() -> {
                client.onStatisticsResponse(response -> {
                    if (response == null) {
                        waitingThread = null;
                        return;
                    }

                    JOptionPane.showMessageDialog(
                            null,
                            new StringBuilder()
                                    .append("Games won: ").append(response.stats.gamesWon()).append("\n")
                                    .append("Games lost: ").append(response.stats.gamesWon()).append("\n"),
                            "Statistics",
                            JOptionPane.PLAIN_MESSAGE
                    );

                    waitingThread = null;
                });

                client.sendEvent(new StatisticsRequestEvent());
            });
            waitingThread.start();
        });

        menuWrapper.add(statisticsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setMargin(buttonInsets);
        logoutButton.setFont(buttonFont);
        logoutButton.addActionListener(e -> {
            menuWrapper.remove(0);
            client.sendEvent(new LogoutEvent());
            client.cancelEventHandler();

            if (client.disconnect()) System.err.println("Player logged out!");

            c.gridx = 0;
            c.gridy = 1;
            instance.remove(menuWrapper);
            instance.add(loginWrapper, c);
            instance.revalidate();
            instance.repaint();
        });
        menuWrapper.add(logoutButton);
    }

    private @NotNull JPanel createString(@NotNull String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(labelFont);
        row.setOpaque(false);
        row.add(label);
        return row;
    }
}
