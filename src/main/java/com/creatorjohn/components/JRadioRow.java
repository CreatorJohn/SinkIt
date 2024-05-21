package com.creatorjohn.components;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.creatorjohn.helpers.JConfig.*;

public class JRadioRow<T> extends JPanel {
    final private List<Item<T>> items;

    public JRadioRow(String label, List<T> values, Function<T, String> mapper) {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.setOpaque(false);

        JPanel row = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        row.setOpaque(false);
        c.anchor = GridBagConstraints.CENTER;

        if (label != null) {
            JLabel rowLabel = new JLabel(label);
            rowLabel.setForeground(Color.WHITE);
            rowLabel.setFont(labelFont);
            this.add(rowLabel);
            this.add(new JSpacer(10, JSpacer.Direction.X_Axis));
        }

        AtomicInteger index = new AtomicInteger(0);
        c.fill = GridBagConstraints.BOTH;
        items = values
                .stream()
                .map(value -> {
                    JButton button = new JButton(mapper.apply(value));
                    button.setFont(buttonFont);
                    button.setMargin(buttonInsets);
                    button.setFocusPainted(false);
                    if (index.get() == 0) button.setBackground(Color.LIGHT_GRAY);
                    else {
                        button.setBackground(Color.WHITE);
                        c.gridx = index.get();
                        row.add(new JSpacer(2, JSpacer.Direction.X_Axis));
                        index.set(c.gridx + 1);
                    }
                    button.setSelected(index.get() == 0);
                    button.addPropertyChangeListener("isSelected", evt -> {
                        System.out.println("Selection changed!");
                    });
                    c.gridx = index.get();
                    row.add(button, c);
                    index.set(c.gridx + 1);

                    return new Item<>(value, button);
                })
                .toList();

        this.add(row);

        for (Item<T> item : items) {
            item.button().addActionListener(e -> {
                List<Item<T>> filtered = items.stream().filter(it -> !Objects.equals(it, item) && it.button.isSelected()).toList();

                filtered.forEach(it -> {
                    it.button.setSelected(false);
                    it.button.setBackground(Color.WHITE);
                    it.button.revalidate();
                    it.button.repaint();
                });

                item.button.setSelected(!item.button.isSelected());

                if (item.button.isSelected()) item.button.setBackground(Color.LIGHT_GRAY);
                else item.button.setBackground(Color.WHITE);

                item.button.revalidate();
                item.button.repaint();
            });
        }
    }

    public T selected() {
        List<T> values = items.stream().filter(it -> it.button.isSelected()).map(it -> it.value).toList();

        if (values.isEmpty()) return null;
        else return values.getFirst();
    }

    private record Item<T>(T value, JButton button) {}
}
