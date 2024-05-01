package com.creatorjohn.components;

import javax.swing.*;
import java.awt.*;

public class JSpacer extends JPanel {

    public JSpacer(int space, Direction direction) {
        Dimension dimension = direction == Direction.X_Axis
                ? new Dimension(space, 0)
                : new Dimension(0, space);
        this.setPreferredSize(dimension);
        this.setOpaque(false);
    }

    public enum Direction { X_Axis, Y_Axis }
}
