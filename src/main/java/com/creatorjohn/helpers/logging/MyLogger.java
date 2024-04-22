package com.creatorjohn.helpers.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger extends Logger {

    public MyLogger(String name) {
        super(name, null);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new Formatter());
        this.addHandler(ch);
        this.setLevel(Level.ALL);
    }
}
