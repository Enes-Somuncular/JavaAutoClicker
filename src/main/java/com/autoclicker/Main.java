package com.autoclicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        // Disable JNativeHook logging to prevent console spam
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            // JNativeHook'un kendi thread'ini meşgul etmemesi için ayrı bir dispatcher kuruyoruz.
            // Bu, Windows'ta "beyaz ekran" ve "donma" sorunlarını önler.
            GlobalScreen.setEventDispatcher(new com.github.kwhat.jnativehook.dispatcher.DefaultEventDispatcher());
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
