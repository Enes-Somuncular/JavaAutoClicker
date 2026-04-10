package com.autoclicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        // Ekran kartı hızlandırmasını kapat — Windows beyaz ekran sorunlarını çözer
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.noddraw", "true");

        // Disable JNativeHook logging to prevent console spam
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        // Run GUI
        SwingUtilities.invokeLater(() -> {
            try {
                com.formdev.flatlaf.FlatDarkLaf.setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MacroGUI gui = new MacroGUI();
            gui.setVisible(true);
        });
    }
}
