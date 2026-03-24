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
    private JButton btnRecord, btnStopRecord, btnPlay, btnStopPlay;
    private int listeningFor = -1; // 0=Record, 1=StopRecord, 2=Play, 3=StopPlay

    public SettingsDialog(JFrame parent, ConfigManager config) {
        super(parent, "Kısayol Ayarları", true);
        this.config = config;
        
        setSize(320, 250);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GlobalScreen.addNativeKeyListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                GlobalScreen.removeNativeKeyListener(SettingsDialog.this);
            }
        });

        mainPanel.add(new JLabel("Kayda Başla:"));
        btnRecord = new JButton(NativeKeyEvent.getKeyText(config.hotkeyRecord));
        btnRecord.addActionListener(e -> listenFor(0, btnRecord));
        mainPanel.add(btnRecord);

        mainPanel.add(new JLabel("Kaydı Durdur:"));
        btnStopRecord = new JButton(NativeKeyEvent.getKeyText(config.hotkeyStopRecord));
        btnStopRecord.addActionListener(e -> listenFor(1, btnStopRecord));
        mainPanel.add(btnStopRecord);

        mainPanel.add(new JLabel("Oynat:"));
        btnPlay = new JButton(NativeKeyEvent.getKeyText(config.hotkeyPlay));
        btnPlay.addActionListener(e -> listenFor(2, btnPlay));
        mainPanel.add(btnPlay);

        mainPanel.add(new JLabel("Oynatmayı Durdur:"));
        btnStopPlay = new JButton(NativeKeyEvent.getKeyText(config.hotkeyStopPlay));
        btnStopPlay.addActionListener(e -> listenFor(3, btnStopPlay));
        mainPanel.add(btnStopPlay);

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
                if (listeningFor == 0) { config.hotkeyRecord = code; btnRecord.setText(text); }
                else if (listeningFor == 1) { config.hotkeyStopRecord = code; btnStopRecord.setText(text); }
                else if (listeningFor == 2) { config.hotkeyPlay = code; btnPlay.setText(text); }
                else if (listeningFor == 3) { config.hotkeyStopPlay = code; btnStopPlay.setText(text); }
                listeningFor = -1;
            });
        }
    }

    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
}
