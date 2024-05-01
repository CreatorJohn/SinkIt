package com.creatorjohn.screens;

import com.creatorjohn.components.JLogo;
import com.creatorjohn.components.JLogin;
import com.creatorjohn.components.JRegister;
import com.creatorjohn.handlers.Client;
import com.creatorjohn.helpers.events.LoginEvent;
import com.creatorjohn.helpers.events.LoginResponseEvent;
import com.creatorjohn.helpers.events.RegisterEvent;
import com.creatorjohn.helpers.events.RegisterResponseEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Objects;

final public class MenuScreen extends Screen {
    final private JFrame frame;
    final private JPanel registerWrapper = new JPanel(new GridBagLayout());
    final private JPanel loginWrapper = new JPanel(new GridBagLayout());
    final private JPanel menuWrapper = new JPanel(new GridBagLayout());
    final private GridBagConstraints c = new GridBagConstraints();
    private Client client;

    public MenuScreen(JFrame frame) {
        super(frame);
        this.frame = frame;
        instance.setLayout(new GridBagLayout());
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        JLogo logo = new JLogo("logo.png", frame.getWidth() / 2, frame.getWidth() / 2);
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = 0;
        instance.add(logo, c);

        createLogin();
        createRegister();

        c.gridy = 1;
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
        loginWrapper.setOpaque(false);
        JLogin loginForm = new JLogin(frame, new JLogin.Config() {
            @Override
            public String login(String username, String password) {
                if (username.isBlank()) return "Username is required!";
                else if (password.isBlank()) return "Password is required!";

                client = Client.connect("127.0.0.1", 5000);

                if (client == null) return "Failed to connect to server!";

                client.sendEvent(new LoginEvent(username, password));

                LoginResponseEvent response = client.receiveEvent(LoginResponseEvent.class);

                if (response.error == null) JOptionPane.showMessageDialog(frame, "Successfully connected!", "Login success", JOptionPane.INFORMATION_MESSAGE);

                return response.error;
            }

            @Override
            public void toRegister() {
                c.gridy = 1;
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
        registerWrapper.setOpaque(false);
        JRegister registerForm = new JRegister(frame, new JRegister.Config() {
            @Override
            public String register(String username, String password, String rePassword) {
                if (username.isBlank() || username.length() < 4) return "Username must be at least 4 characters long!";
                else if (password.isBlank() || password.length() < 6) return "Password must be at least 6 characters long!";
                else if (!Objects.equals(password, rePassword)) return "Passwords don't match!";

                client = Client.connect("127.0.0.1", 5000);

                if (client == null) return "Failed to connect to server!";

                client.sendEvent(new RegisterEvent(username, password));

                RegisterResponseEvent response = client.receiveEvent(RegisterResponseEvent.class);

                if (response.error == null) JOptionPane.showMessageDialog(frame, "Successfully connected!", "Register success", JOptionPane.INFORMATION_MESSAGE);

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
}
