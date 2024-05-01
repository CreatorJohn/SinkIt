package com.creatorjohn.components;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JLogin extends JPanel {

    public JLogin(JFrame frame, Config config) {
        this.setLayout(new GridLayout(0, 1, 10, 0));
        this.setOpaque(false);
        Font labelFont = new Font("Arial", Font.BOLD, 24);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Insets buttonInsets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(fieldFont);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(Color.white);
        usernameLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                usernameField.requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.white);
        passwordLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                passwordField.requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        JButton loginButton = new JButton("Login");
        loginButton.setMargin(buttonInsets);
        loginButton.setFont(buttonFont);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> {
            String error = config.login(usernameField.getText(), new String(passwordField.getPassword()));

            if (error != null && !error.isBlank()) JOptionPane.showMessageDialog(frame, error.trim(), "Login error", JOptionPane.ERROR_MESSAGE);
            else {
                usernameField.setText("");
                passwordField.setText("");
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.setMargin(buttonInsets);
        registerButton.setFont(buttonFont);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> config.toRegister());

        JPanel usernameRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        usernameRow.setOpaque(false);
        usernameRow.add(usernameLabel);
        usernameRow.add(usernameField);
        this.add(usernameRow);

        JPanel passwordRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        passwordRow.setOpaque(false);
        passwordRow.add(passwordLabel);
        passwordRow.add(passwordField);
        this.add(passwordRow);

        JPanel buttonRow = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        buttonRow.setOpaque(false);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;

        c.insets.set(0, 0, 0, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 3;
        buttonRow.add(loginButton, c);

        c.insets.set(0, 5, 0, 0);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        buttonRow.add(registerButton, c);

        this.add(buttonRow);
    }

    public interface Config {
        String login(String username, String password);
        void toRegister();
    }
}
