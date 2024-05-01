package com.creatorjohn.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JRegister extends JPanel {

    public JRegister(JFrame frame, Config config) {
        this.setOpaque(false);
        this.setLayout(new GridLayout(0, 1, 10, 0));
        Font labelFont = new Font("Arial", Font.BOLD, 24);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Insets buttonInsets = new Insets(5, 5, 5, 5);

        JPanel usernameRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        usernameRow.setOpaque(false);
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
        usernameRow.add(usernameLabel);
        usernameRow.add(usernameField);
        this.add(usernameRow);

        JPanel passwordRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        passwordRow.setOpaque(false);
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
        passwordRow.add(passwordLabel);
        passwordRow.add(passwordField);
        this.add(passwordRow);

        JPanel rePasswordRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rePasswordRow.setOpaque(false);
        JPasswordField rePasswordField = new JPasswordField(20);
        rePasswordField.setFont(fieldFont);
        JLabel rePasswordLabel = new JLabel("Re-password:");
        rePasswordLabel.setFont(labelFont);
        rePasswordLabel.setForeground(Color.white);
        rePasswordLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                rePasswordField.requestFocus();
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
        rePasswordRow.add(rePasswordLabel);
        rePasswordRow.add(rePasswordField);
        this.add(rePasswordRow);

        JPanel buttonRow = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        buttonRow.setOpaque(false);
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        JButton registerButton = new JButton("Register");
        registerButton.setMargin(buttonInsets);
        registerButton.setFont(buttonFont);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String rePassword = new String(rePasswordField.getPassword());
            String error = config.register(username, password, rePassword);

            if (error != null && !error.isBlank()) JOptionPane.showMessageDialog(frame, error.trim(), "Register error", JOptionPane.ERROR_MESSAGE);
            else {
                usernameField.setText("");
                passwordField.setText("");
                rePasswordField.setText("");
            }
        });
        c.insets.set(0, 0, 0, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 3;
        buttonRow.add(registerButton, c);

        JButton loginButton = new JButton("Login");
        loginButton.setMargin(buttonInsets);
        loginButton.setFont(buttonFont);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> config.toLogin());
        c.insets.set(0, 5, 0, 0);
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1;
        buttonRow.add(loginButton, c);

        this.add(buttonRow);
    }

    public interface Config {
        String register(String username, String password, String rePassword);
        void toLogin();
    }
}
