package com.creatorjohn.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.regex.Pattern;

public class ConnectForm {
    final private JPanel instance = new JPanel();

    public JPanel getInstance() {
        return instance;
    }

    public ConnectForm(ConnectFormConfig config) {
        State state = new State();
        instance.setLayout(new BoxLayout(instance, BoxLayout.Y_AXIS));
        JPanel addressPortRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));

        addressPortRow.add(createInput("Address:", "", 10, new InputConfig() {
            @Override
            public String verify(String value) {
                return Pattern.matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$", value) ? null : "Invalid IP address!";
            }

            @Override
            public void update(String value) {
                state.address = value;
            }

            @Override
            public void revalidate() {
                config.revalidate();
            }
        }));

        addressPortRow.add(createInput(null, "5000", 3, new InputConfig() {
            @Override
            public String verify(String value) {
                if (!Pattern.matches("^[0-9]{4}$", value)) return "Invalid port format!";
                else if (Integer.parseInt(value) < 5000 && Integer.parseInt(value) > 6666) return "Invalid port!";
                else return null;
            }

            @Override
            public void update(String value) {
                state.port = value != null ? Integer.parseInt(value) : -1;
            }

            @Override
            public void revalidate() {
                config.revalidate();
            }
        }));
        JPanel actionRow = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(-1, 26));
        connectButton.addActionListener(e -> {
            if (state.address == null || state.port == -1) return;

            String error = config.connect(state.address, state.port);

            if (error != null) JOptionPane.showMessageDialog(null, error);
        });

        actionRow.add(connectButton);
        instance.add(addressPortRow);
        instance.add(actionRow);
    }

    private JPanel createInput(String label, String initial, int cols, InputConfig config) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JPanel col = new JPanel(new GridLayout(0, 1, 0, 5));

        if (label != null) {
            JLabel text = new JLabel(label);
            text.setFont(new Font("Arial", Font.BOLD, 18));
            row.add(text);
        }

        JTextField textField = new JTextField(initial);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));

        if (cols >= 0) textField.setColumns(cols);

        JLabel error = new JLabel();
        error.setFont(new Font("Arial", Font.BOLD, 12));
        error.setForeground(Color.RED);
        textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                verify();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verify();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                verify();
            }

            private void verify() {
                String errorText = config.verify(textField.getText());

                error.setText(errorText);

                if (errorText == null) {
                    config.update(textField.getText());
                    col.remove(error);
                } else {
                    config.update(null);
                    col.add(error);
                }

                config.revalidate();
            }
        });

        col.add(textField);
        row.add(col);
        return row;
    }

    private interface InputConfig {
        String verify(String value);
        void update(String value);
        void revalidate();
    }

    private class State {
        String address = null;
        int port = 5000;
    }

    public interface ConnectFormConfig {
        String connect(String address, int port);
        void revalidate();
    }
}
