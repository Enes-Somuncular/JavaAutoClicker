package com.autoclicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        boolean isMac = System.getProperty("os.name", "").toLowerCase().contains("mac");

        if (isMac) {
            // macOS: Retina / HiDPI desteği
            System.setProperty("apple.awt.application.name", "Java AutoClicker");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Java AutoClicker");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("sun.java2d.metal", "true"); // macOS Metal renderer (Java 17+)
        } else {
            // Windows: beyaz ekran sorununu önle
            System.setProperty("sun.java2d.d3d", "false");
            System.setProperty("sun.java2d.noddraw", "true");
        }

        // JNativeHook log spam'ini kapat
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            String msg = ex.getMessage();
            if (isMac && msg != null && msg.toLowerCase().contains("assistive")) {
                // macOS erişilebilirlik izni eksik — kullanıcıya rehber göster
                JOptionPane.showMessageDialog(null,
                    "macOS Erişilebilirlik (Accessibility) izni gerekiyor!\n\n" +
                    "Adımlar:\n" +
                    "  1. Apple Menüsü → Sistem Ayarları\n" +
                    "  2. Gizlilik ve Güvenlik → Erişilebilirlik\n" +
                    "  3. Kilidi aç ve 'Terminal' uygulamasını ekle\n\n" +
                    "İzni verdikten sonra uygulamayı yeniden başlat.",
                    "Erişilebilirlik İzni Gerekli",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Native hook kaydedilemedi:\n" + msg,
                    "Hata", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(1);
        }

        // GUI başlat
        SwingUtilities.invokeLater(() -> {
            try {
                com.formdev.flatlaf.FlatDarkLaf.setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MacroGUI gui = new MacroGUI();

            if (isMac) {
                // macOS: Dock ikonu olarak ayarla
                try {
                    java.awt.Taskbar tb = java.awt.Taskbar.getTaskbar();
                    tb.setIconImage(gui.getIconImage());
                } catch (Exception ignored) {}
            }

            gui.setVisible(true);
        });
    }
}
