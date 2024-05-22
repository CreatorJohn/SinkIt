package com.creatorjohn.screens;

import com.creatorjohn.components.*;
import com.creatorjohn.handlers.Client;
import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.JLock;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.*;
import com.creatorjohn.helpers.events.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.creatorjohn.helpers.JConfig.*;

public class GameScreen extends Screen {
    final private URL iconUrl = getClass().getResource("/assets/loader32.gif");
    final private JButton gameConfirmButton = new JButton("Waiting...");
    final private JLabel shootsText = new JLabel("Shoot: " + maxShots);
    final private JLabel priceText = new JLabel("Power-up price: -");
    final private JLabel tokenText = new JLabel("Tokens: 0");
    final private List<PowerUp> usedPowerUps = new ArrayList<>();
    final private List<Position> shotTiles = new ArrayList<>();
    final private JLock threadLock = new JLock();
    final private JPowerUpPicker powerUpPicker;
    final private JShipPicker shipPicker;
    final private GameBoard enemyGame;
    final private JBoard enemyBoard;
    final private GameBoard myGame;
    final private JPanel boardRow;
    final private Client client;
    final private JBoard myBoard;
    private int shoot = maxShots;
    private JPanel currentPlayerText = createString("");
    private boolean updating = false;
    private Thread waitingThread;
    private String currentPlayer;

    public GameScreen(JFrame frame, Client client, GameBoard.BoardSize size, String gameID) {
        this(frame, client, size, gameID, List.of(), List.of());
    }

    public GameScreen(JFrame frame, Client client, GameBoard.BoardSize size, String gameID, List<Ship> myShips, List<Ship> enemyShips) {
        super(frame);
        this.client = client;

        instance.setLayout(new BoxLayout(instance, BoxLayout.Y_AXIS));

        List<JShipPicker.ShipInfo> availableShips = List.of(
                new JShipPicker.ShipInfo(
                        "3I",
                        List.of(new Position(0, -1), new Position(0, 1))
                ),
                new JShipPicker.ShipInfo(
                        "4T",
                        List.of(new Position(-1, 0), new Position(1, 0), new Position(0, 1))
                ),
                new JShipPicker.ShipInfo(
                        "5L",
                        List.of(new Position(-2, 0), new Position(-1, 0), new Position(0, 1), new Position(0, 2))
                )
        );
        List<JPowerUpPicker.PowerUpInfo> availablePowerUps = List.of(
                new JPowerUpPicker.PowerUpInfo("bomb-cursor.png", PowerUp.Type.BOMB),
                new JPowerUpPicker.PowerUpInfo("bomber-cursor.png", PowerUp.Type.BOMBER),
                new JPowerUpPicker.PowerUpInfo("radar-cursor.png", PowerUp.Type.RADAR),
                new JPowerUpPicker.PowerUpInfo("farm-cursor.png", PowerUp.Type.FARM)
        );

        this.myGame = new GameBoard(size, GameBoard.BoardType.MY, maxShipSize);
        this.enemyGame = new GameBoard(size, GameBoard.BoardType.ENEMY, maxShipSize);

        JPanel gameLabelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gameLabelWrapper.setOpaque(false);
        JButton leaveButton = new JButton("Leave");
        leaveButton.setMargin(buttonInsets);
        leaveButton.setFont(buttonFont);
        leaveButton.addActionListener(e -> {
            client.sendEvent(new DisconnectEvent());
            navigateBack();
        });
        gameLabelWrapper.add(leaveButton);
        gameLabelWrapper.add(new JSpacer(10, JSpacer.Direction.X_Axis));
        JLabel gameLabel = new JLabel(gameID);
        gameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gameLabel.setForeground(Color.WHITE);
        gameLabelWrapper.add(gameLabel);
        gameLabelWrapper.add(new JSpacer(10, JSpacer.Direction.X_Axis));
        JButton copyButton = new JButton("Copy");
        copyButton.setFont(buttonFont);
        copyButton.setMargin(buttonInsets);
        copyButton.addActionListener(e -> {
            StringSelection selectedString = new StringSelection(gameID);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selectedString, null);
            copyButton.setText("Copied!");
        });
        copyButton.setFocusPainted(false);
        gameLabelWrapper.add(copyButton);
        instance.add(gameLabelWrapper);

        int boardWidth = frame.getWidth() / 3;
        int tileCount = JBoard.getBoardEdgeTiles(size);
        int tileSize = (boardWidth - 5 * (tileCount + 1)) / tileCount;
        int xSpace = (frame.getWidth() - 2 * boardWidth) / 3;
        shipPicker = new JShipPicker(availableShips, (int)((float)tileSize * 1.5), 5, JShipPicker.Type.HORIZONTAL);
        powerUpPicker = new JPowerUpPicker(availablePowerUps, tileCount, tileSize, 5);

        boardRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardRow.setOpaque(false);

        myBoard = new JBoard(size, new Dimension(boardWidth, boardWidth), tileSize, false);
        myBoard.onHover(selected -> {
            powerUpPicker.setBoardType(GameBoard.BoardType.MY);
            return powerUpPicker.move(selected);
        });
        myBoard.onLeftClick(positions -> {
            if (currentPlayer == null || threadLock.locked()) return;

            PowerUp selectedPowerUp = powerUpPicker.selected();

            if (selectedPowerUp == null) return;
            else if (!myGame.usePowerUp(selectedPowerUp)) return;

            switch (selectedPowerUp) {
                case Bomb bomb -> myBoard.placeBomb(bomb);
                case Farm farm -> myBoard.placeFarm(farm);
                case Radar radar -> myBoard.placeRadar(radar);
                case Bomber ignored -> positions.forEach(pos -> {
                    if (myGame.ship(pos) == null) myBoard.destroy(List.of(pos));
                    else myBoard.destroyShip(List.of(pos));
                });
                default -> {}
            };
            usedPowerUps.add(selectedPowerUp);
        });
        myGame.onTokensChanged(tokens -> tokenText.setText("Tokens: " + tokens));
        myGame.onShoot(pos -> {
            if (myGame.ship(pos) == null) myBoard.destroy(List.of(pos));
            else myBoard.destroyShip(List.of(pos));
        });

        enemyBoard = new JBoard(size, new Dimension(boardWidth, boardWidth), tileSize, true);
        enemyBoard.onHover(selected -> {
            powerUpPicker.setBoardType(GameBoard.BoardType.ENEMY);
            return powerUpPicker.move(selected);
        });
        enemyBoard.onLeftClick(positions -> {
            if (currentPlayer == null || threadLock.locked()) return;

            PowerUp selectedPowerUp = powerUpPicker.selected();

            if (selectedPowerUp == null && shoot - positions.size() < 0 && !positions.stream().allMatch(it -> enemyGame.canShootTile(it.x(), it.y())))
                return;
            else if (selectedPowerUp == null) {
                positions.forEach(pos -> {
                    if (enemyGame.shootTile(pos.x(), pos.y())) shoot--;
                });
                enemyBoard.destroy(positions);
                shotTiles.addAll(positions);
                shootsText.setText("Shoot: " + shoot);
                return;
            }
            else if (!enemyGame.usePowerUp(selectedPowerUp)) return;

            switch (selectedPowerUp) {
                case Bomb bomb -> enemyBoard.placeBomb(bomb);
                case Farm farm -> enemyBoard.placeFarm(farm);
                case Radar radar -> enemyBoard.placeRadar(radar);
                case Bomber ignored -> positions.forEach(pos -> {
                    if (enemyGame.ship(pos) == null) enemyBoard.destroy(List.of(pos));
                    else enemyBoard.destroyShip(List.of(pos));
                });
                default -> {}
            }
            usedPowerUps.add(selectedPowerUp);
        });
        enemyGame.onShoot(pos -> {
            if (enemyGame.ship(pos) == null) enemyBoard.destroy(List.of(pos));
            else enemyBoard.destroyShip(List.of(pos));
        });
        enemyGame.onGameOver(() -> client.sendEvent(new UpdateGameEvent(shotTiles, usedPowerUps, true)));
        enemyGame.onBombShot(pos -> myGame.shootTile(pos.x(), pos.y()));
        powerUpPicker.onSelected(selected -> {
            if (selected == null) priceText.setText("Power-up price: -");
            else priceText.setText("Power-up price: " + selected.getCost(size, maxShipSize));
        });
        powerUpPicker.onChangeDirection(changed -> {
            if (powerUpPicker.selectedBoardType() == GameBoard.BoardType.MY) {
                myBoard.unhover();
                myBoard.hover(changed);
            } else {
                enemyBoard.unhover();
                enemyBoard.hover(changed);
            }
        });
        instance.add(boardRow);

        client.onGameFinished(event -> {
            if (event.status == GameFinishedEvent.Status.WINNER)
                JConfig.dialogSuccess("Game info", "You have won!");
            else
                JConfig.dialogSuccess("Game info", "You have lost!");

            client.sendEvent(new DisconnectEvent());
            client.onPlayerLeft(null);
            client.onPlayerJoined(null);
            navigateBack();
        });
        client.onGameUpdated(event -> {
            if (!event.success) {
                threadLock.unlock();
                updating = false;
                return;
            }

            currentPlayer = event.currentPlayer;
            System.out.println("Game updated...");
            System.out.println("Current player: " + currentPlayer);

            instance.remove(currentPlayerText);

            if (Objects.equals(currentPlayer, client.username())) {
                currentPlayerText = createString("You are playing!");
                instance.add(currentPlayerText, 1);
                threadLock.unlock();
                usedPowerUps.clear();
                shotTiles.clear();

                gameConfirmButton.setText("Confirm");
                gameConfirmButton.setIcon(null);
            }
            else {
                currentPlayerText = createString("Enemy is playing!");
                instance.add(currentPlayerText, 1);
                threadLock.lock();

                if (iconUrl != null) gameConfirmButton.setIcon(new ImageIcon(iconUrl));

                gameConfirmButton.setText("Waiting...");
            }

            System.out.println("Incoming my shot tiles: " + event.shotTiles.my());
            System.out.println("Incoming enemy shot tiles: " + event.shotTiles.enemy());
            System.out.println("Incoming my power-ups: " + event.powerUps.my());
            System.out.println("Incoming enemy power-ups: " + event.powerUps.enemy());
            System.out.println("Incoming ships: " + event.ships);

            List<Ship> loadedShips = enemyGame.ships();

            event.ships.forEach(ship -> {
                if (loadedShips.contains(ship)) return;

                enemyGame.placeShip(ship, true);
            });
            event.shotTiles.my().forEach(tile -> {
                enemyGame.shootTile(tile.x(), tile.y(), true);
            });
            event.shotTiles.enemy().forEach(tile -> {
                myGame.shootTile(tile.x(), tile.y(), true);
            });
            event.powerUps.my().forEach(powerUp -> {
                switch (powerUp) {
                    case Bomb bomb -> myGame.useBombPowerUp(bomb, true);
                    case Farm farm -> myGame.useFarmPowerUp(farm, true);
                    case Bomber bomber -> enemyGame.useBomberPowerUp(bomber, true);
                    case Radar radar -> {
                        enemyGame.useRadarPowerUp(radar, true);
                        enemyGame.shootTile(radar.position().x(), radar.position().y(), true);
                        enemyBoard.placeRadar(radar);
                    }
                    default -> {}
                }
            });
            event.powerUps.enemy().forEach(powerUp -> {
                switch (powerUp) {
                    case Bomb bomb -> enemyGame.useBombPowerUp(bomb, true);
                    case Farm farm -> enemyGame.useFarmPowerUp(farm, true);
                    case Bomber bomber -> myGame.useBomberPowerUp(bomber, true);
                    case Radar radar -> {
                        myGame.useRadarPowerUp(radar, true);
                        myGame.shootTile(radar.position().x(), radar.position().y(), true);
                        myBoard.placeRadar(radar);
                    }
                    default -> {}
                }
            });
            /*myGame.shotTiles().forEach(tile -> {
                if (myGame.ship(tile) == null) myBoard.destroy(List.of(tile));
                else myBoard.destroyShip(List.of(tile));
            });
            enemyGame.shotTiles().forEach(tile -> {
                if (enemyGame.ship(tile) == null) enemyBoard.destroy(List.of(tile));
                else enemyBoard.destroyShip(List.of(tile));
            });*/

            if (updating) {
                myGame.generateTokens();
                shoot = maxShots;
                shootsText.setText("Shoot: " + shoot);
            }

            waitingThread = null;
            instance.revalidate();
            instance.repaint();
        });

        myShips.forEach(ship -> {
            myGame.placeShip(ship, true);
            myBoard.placeShip(ship.getGameBoardPositions());
        });
        enemyShips.forEach(ship -> enemyGame.placeShip(ship, true));
        handleSetup(JBoard.getShipsRequiredCount(size), xSpace, size, new Dimension(boardWidth, boardWidth), tileSize, !myShips.isEmpty());
    }

    private void handleSetup(int requiredShipCount,
                             int boardGap,
                             @NotNull GameBoard.BoardSize boardSize,
                             @NotNull Dimension boardDimension,
                             int tileSize,
                             boolean skip) {

        JBoard setupBoard = new JBoard(boardSize, boardDimension, tileSize, false);
        setupBoard.onHover(selected -> {
            shipPicker.moveSelected(selected);

            if (!shipPicker.selectedPositions().isEmpty())
                return shipPicker.selectedPositions();
            else return List.of(selected);
        });
        setupBoard.onLeftClick(positions -> {
            if (waitingThread != null) return;

            List<Position> selectedShipPos = shipPicker.selectedPositions();
            Ship selectedShip = selectedShipPos.isEmpty()
                    ? null
                    : new Ship(selectedShipPos.size(), selectedShipPos);

            if (selectedShip != null && myGame.placeShip(selectedShip)) {
                myBoard.placeShip(positions);
                setupBoard.placeShip(positions);
            }
        });
        setupBoard.onRightClick(position -> {
            if (waitingThread != null) return;

            Ship deletedShip = myGame.removeShip(position);

            if (deletedShip != null) {
                myBoard.removeShip(deletedShip.getGameBoardPositions());
                setupBoard.removeShip(deletedShip.getGameBoardPositions());
            }
        });

        shipPicker.onRotate(rotated -> {
            setupBoard.unhover();
            setupBoard.hover(rotated);
        });

        instance.add(setupBoard);
        instance.add(new JSpacer(10, JSpacer.Direction.Y_Axis));

        JPanel actionRow = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 5;
        c.anchor = GridBagConstraints.CENTER;
        actionRow.setOpaque(false);
        actionRow.add(shipPicker.instance(), c);

        JButton confirmButton = new JButton("Confirm!");

        client.onPlayerJoined(ignored -> JConfig.dialogInfo("Game info", "Player has joined!"));
        client.onPlayerLeft(ignored -> JConfig.dialogInfo("Game info", "Player has left!"));
        client.onGameInitialized(event -> {
            if (!event.success) JConfig.dialogError("Setup error", "Failed to setup game!");
            else {
                instance.remove(actionRow);
                instance.remove(setupBoard);
                handleRunning(boardGap);
            }

            waitingThread = null;
            confirmButton.setIcon(null);
            threadLock.unlock();
        });
        client.handleIncomingEvents();

        confirmButton.setMargin(buttonInsets);
        confirmButton.addActionListener(e -> {
            if (myGame.ships().size() < requiredShipCount) return;
            if (waitingThread != null || threadLock.locked()) return;

            waitingThread = new Thread(() -> {
                threadLock.lock();

                if (iconUrl != null) confirmButton.setIcon(new ImageIcon(iconUrl));

                client.sendEvent(new InitializeGameEvent(myGame.ships()));
            });
            waitingThread.start();
        });
        c.fill = GridBagConstraints.BOTH;
        actionRow.add(confirmButton, c);
        instance.add(actionRow);

        if (skip) {
            instance.remove(actionRow);
            instance.remove(setupBoard);
            handleRunning(boardGap);
        }
    }

    private void handleRunning(int boardGap) {
        boardRow.add(myBoard);
        boardRow.add(new JSpacer(boardGap, JSpacer.Direction.X_Axis));
        boardRow.add(enemyBoard);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        actionRow.setOpaque(false);
        actionRow.add(powerUpPicker.instance());

        if (iconUrl != null) gameConfirmButton.setIcon(new ImageIcon(iconUrl));

        gameConfirmButton.setMargin(buttonInsets);
        gameConfirmButton.setFont(buttonFont);
        gameConfirmButton.addActionListener(e -> {
            if (waitingThread != null || threadLock.locked()) return;

            waitingThread = new Thread(() -> {
                threadLock.lock();

                if (iconUrl != null) gameConfirmButton.setIcon(new ImageIcon(iconUrl));

                gameConfirmButton.setText("Waiting...");

                client.sendEvent(new UpdateGameEvent(shotTiles, usedPowerUps, false));
                instance.remove(currentPlayerText);
                currentPlayerText = createString("Enemy is playing!");
                instance.add(currentPlayerText, 1);
                instance.revalidate();
                instance.repaint();
                updating = true;
            });
            waitingThread.start();
        });

        JPanel infoRow = new JPanel(new GridLayout(1, 0, 10, 0));
        infoRow.setOpaque(false);

        priceText.setFont(labelFont);
        priceText.setForeground(Color.WHITE);
        priceText.setHorizontalAlignment(SwingConstants.CENTER);
        infoRow.add(priceText);

        tokenText.setFont(labelFont);
        tokenText.setForeground(Color.WHITE);
        tokenText.setHorizontalAlignment(SwingConstants.CENTER);
        infoRow.add(tokenText);

        shootsText.setFont(labelFont);
        shootsText.setForeground(Color.WHITE);
        shootsText.setHorizontalAlignment(SwingConstants.CENTER);
        infoRow.add(shootsText);

        actionRow.add(gameConfirmButton);
        instance.add(currentPlayerText, 1);
        instance.add(actionRow);
        instance.add(new JSpacer(5, JSpacer.Direction.Y_Axis));
        instance.add(infoRow);
        instance.revalidate();
        instance.repaint();
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

    @Override
    void onAppClosing() {
        if (client != null) {
            client.sendEvent(new DisconnectEvent());
            client.sendEvent(new LogoutEvent());
            if (client.disconnect()) System.out.println("Player disconnected...");
        }

        navigateBack();
    }

    private enum State { SETUP, RUNNING, PAUSED, FINISHED }
}
