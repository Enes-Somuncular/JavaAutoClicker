package com.autoclicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsDialog extends JDialog implements NativeKeyListener {
    private final ConfigManager config;
    private JButton btnToggleRecord, btnTogglePlay;
    private int listeningFor = -1; // 0=Record, 1=Play

    public SettingsDialog(JFrame parent, ConfigManager config) {
        super(parent, "Kısayol Ayarları", true);
        this.config = config;
        
        setSize(320, 250);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GlobalScreen.addNativeKeyListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                GlobalScreen.removeNativeKeyListener(SettingsDialog.this);
            }
        });

        mainPanel.add(new JLabel("Kayıt Başlat/Durdur:"));
        btnToggleRecord = new JButton(NativeKeyEvent.getKeyText(config.hotkeyToggleRecord));
        btnToggleRecord.addActionListener(e -> listenFor(0, btnToggleRecord));
        mainPanel.add(btnToggleRecord);

        mainPanel.add(new JLabel("Oynatmayı Başlat/Durdur:"));
        btnTogglePlay = new JButton(NativeKeyEvent.getKeyText(config.hotkeyTogglePlay));
        btnTogglePlay.addActionListener(e -> listenFor(1, btnTogglePlay));
        mainPanel.add(btnTogglePlay);

        add(mainPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Kaydet & Kapat");
        btnSave.addActionListener(e -> {
            config.save();
            dispose();
        });
        bottomPanel.add(btnSave);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void listenFor(int id, JButton btn) {
        listeningFor = id;
        btn.setText("Tuşa basın...");
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (listeningFor != -1) {
            int code = e.getKeyCode();
            String text = NativeKeyEvent.getKeyText(code);
            SwingUtilities.invokeLater(() -> {
                if (listeningFor == 0) { config.hotkeyToggleRecord = code; btnToggleRecord.setText(text); }
                else if (listeningFor == 1) { config.hotkeyTogglePlay = code; btnTogglePlay.setText(text); }
                listeningFor = -1;
            });
        }
    }

    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
}
