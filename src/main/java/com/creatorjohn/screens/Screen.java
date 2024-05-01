package com.creatorjohn.screens;

import com.creatorjohn.handlers.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

abstract class Screen {
    final private JFrame frame;
    private Screen prevScreen;
    final protected JPanel instance = new JPanel(new BorderLayout(10, 10));
    final private WindowListener wl = new WindowListener() {
        @Override
        public void windowOpened(WindowEvent e) {
            onAppOpened();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            onAppClosing();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            onAppClosed();
        }

        @Override
        public void windowIconified(WindowEvent e) {
            onAppIconified();
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            onAppDeiconified();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            onAppActivated();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            onAppDeactivated();
        }
    };

    private void setPrevScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;
    }

    public JPanel instance() {
        return instance;
    }

    Screen(JFrame mainFrame) {
        frame = mainFrame;
        instance.setOpaque(false);
    }

    void navigateTo(Screen target) {
        target.setPrevScreen(this);
        frame.removeWindowListener(wl);
        frame.addWindowListener(target.wl);
        frame.add(target.instance);
        frame.remove(instance);
        updateContent();
    }

    void navigateBack() {
        frame.removeWindowListener(wl);

        if (prevScreen == null) {
            frame.dispose();
            return;
        }

        frame.addWindowListener(prevScreen.wl);
        frame.add(prevScreen.instance);
        frame.remove(instance);
        updateContent();
    }

    protected void updateContent() {
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Runs when frame is closing
     */
    void onAppClosing() {}

    /**
     * Runs when frame is opened
     */
    void onAppOpened() {}

    /**
     * Runs when frame is closed
     */
    void onAppClosed() {}

    /**
     * Runs when frame is minimized
     */
    void onAppIconified() {}

    /**
     * Runs when frame is de-minimized
     */
    void onAppDeiconified() {}

    /**
     * Runs when frame is focused
     */
    void onAppActivated() {}

    /**
     * Runs when frame is not focused
     */
    void onAppDeactivated() {}
}
