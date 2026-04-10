package com.autoclicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class MacroGUI extends JFrame implements NativeKeyListener {

    private final MacroRecorder recorder;
    private final MacroPlayer player;
    private final ConfigManager config = new ConfigManager();

    private JLabel statusLabel;
    private JButton btnRecord;
    private JButton btnStopRecord;
    private JButton btnPlay;
    private JButton btnStopPlay;
    private JButton btnEdit;
    private JTextField txtLoopCount;

    public MacroGUI() {
        super("Java AutoClicker & Macro");
        recorder = new MacroRecorder();
        player = new MacroPlayer();

        // Register recorder to global screen
        GlobalScreen.addNativeKeyListener(recorder);
        GlobalScreen.addNativeMouseListener(recorder);
        GlobalScreen.addNativeMouseMotionListener(recorder);

        // Register GUI to global screen to catch hotkeys
        GlobalScreen.addNativeKeyListener(this);

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 320); // slightly taller for menu
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            Image icon = new ImageIcon("icon.png").getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Icon could not be loaded: " + e.getMessage());
        }

        // Create Menu Bar
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu("Dosya");
        JMenuItem miSave = new JMenuItem("Makroyu Kaydet");
        JMenuItem miLoad = new JMenuItem("Makro Yükle");
        fileMenu.add(miSave);
        fileMenu.add(miLoad);
        
        JMenu optionsMenu = new JMenu("Ayarlar");
        JMenuItem miSettings = new JMenuItem("Kısayol Ayarları");
        optionsMenu.add(miSettings);

        mb.add(fileMenu);
        mb.add(optionsMenu);
        setJMenuBar(mb);

        miSave.addActionListener(e -> saveMacro());
        miLoad.addActionListener(e -> loadMacro());
        miSettings.addActionListener(e -> {
            new SettingsDialog(this, config).setVisible(true);
            updateButtonLabels();
        });

        JPanel mainPanel = new JPanel(new GridLayout(7, 1, 10, 10)); // Changed grid from 6 to 7
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        statusLabel = new JLabel("Durum: Bekleniyor...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(statusLabel);

        JPanel loopPanel = new JPanel(new FlowLayout());
        loopPanel.add(new JLabel("Tekrar Sayısı:"));
        txtLoopCount = new JTextField("1", 5);
        loopPanel.add(txtLoopCount);
        mainPanel.add(loopPanel);

        btnRecord = new JButton();
        btnStopRecord = new JButton();
        btnPlay = new JButton();
        btnStopPlay = new JButton();
        btnEdit = new JButton("Görsel Kur / Düzenle");
        updateButtonLabels();

        mainPanel.add(btnRecord);
        mainPanel.add(btnStopRecord);
        mainPanel.add(btnPlay);
        mainPanel.add(btnStopPlay);
        mainPanel.add(btnEdit);

        add(mainPanel, BorderLayout.CENTER);

        btnStopRecord.setEnabled(false);
        btnStopPlay.setEnabled(false);

        // Events
        btnRecord.addActionListener(e -> startRecording());
        btnStopRecord.addActionListener(e -> stopRecording());
        btnPlay.addActionListener(e -> startPlaying());
        btnStopPlay.addActionListener(e -> stopPlaying());
        btnEdit.addActionListener(e -> openEditor());
    }
    
    private void openEditor() {
        new MacroEditorDialog(this, recorder).setVisible(true);
        int count = recorder.getRecordedEvents().size();
        statusLabel.setText("Durum: Hazır (" + count + " olay)");
    }

    private void updateButtonLabels() {
        btnRecord.setText("Kaydet (" + NativeKeyEvent.getKeyText(config.hotkeyRecord) + ")");
        btnStopRecord.setText("Kaydı Durdur (" + NativeKeyEvent.getKeyText(config.hotkeyStopRecord) + ")");
        btnPlay.setText("Oynat (" + NativeKeyEvent.getKeyText(config.hotkeyPlay) + ")");
        btnStopPlay.setText("Durdur (" + NativeKeyEvent.getKeyText(config.hotkeyStopPlay) + ")");
    }

    private void saveMacro() {
        List<NativeMacroEvent> events = recorder.getRecordedEvents();
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kaydedilecek bir makro yok!");
            return;
        }
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fc.getSelectedFile()))) {
                oos.writeObject(events);
                statusLabel.setText("Durum: Makro kaydedildi!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        }
    }

    private void loadMacro() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()))) {
                List<NativeMacroEvent> events = (List<NativeMacroEvent>) ois.readObject();
                recorder.setRecordedEvents(events);
                statusLabel.setText("Durum: Makro yüklendi! (" + events.size() + " olay)");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        }
    }

    private void startRecording() {
        btnRecord.setEnabled(false);
        btnPlay.setEnabled(false);
        btnEdit.setEnabled(false);
        btnStopRecord.setEnabled(true);
        statusLabel.setText("Durum: Kaydediliyor...");
        recorder.startRecording();
    }

    private void stopRecording() {
        recorder.stopRecording();
        btnRecord.setEnabled(true);
        btnStopRecord.setEnabled(false);
        btnPlay.setEnabled(true);
        btnEdit.setEnabled(true);
        
        int count = recorder.getRecordedEvents().size();
        statusLabel.setText("Durum: Kayıt Tamam (" + count + " olay)");
    }

    private void startPlaying() {
        List<NativeMacroEvent> events = recorder.getRecordedEvents();
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Oynatılacak kaydedilmiş olay yok!");
            return;
        }

        int loopCount = 1;
        try {
            loopCount = Integer.parseInt(txtLoopCount.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçerli bir tekrar sayısı girin.");
            return;
        }

        btnRecord.setEnabled(false);
        btnPlay.setEnabled(false);
        btnEdit.setEnabled(false);
        btnStopPlay.setEnabled(true);
        statusLabel.setText("Durum: Oynatılıyor...");

        player.play(events, loopCount);

        // Background thread to check when player finishes
        new Thread(() -> {
            while (player.isPlaying()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    break;
                }
            }
            SwingUtilities.invokeLater(() -> {
                if (statusLabel.getText().equals("Durum: Oynatılıyor...")) {
                    statusLabel.setText("Durum: Oynatma Tamamlandı");
                }
                btnRecord.setEnabled(true);
                btnPlay.setEnabled(true);
                btnEdit.setEnabled(true);
                btnStopPlay.setEnabled(false);
            });
        }).start();
    }

    private void stopPlaying() {
        player.stop();
        statusLabel.setText("Durum: Oynatma Durduruldu");
        btnRecord.setEnabled(true);
        btnPlay.setEnabled(true);
        btnEdit.setEnabled(true);
        btnStopPlay.setEnabled(false);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int code = e.getKeyCode();
        if (code == config.hotkeyRecord && btnRecord.isEnabled()) {
            startRecording();
        } else if (code == config.hotkeyStopRecord && btnStopRecord.isEnabled()) {
            stopRecording();
        } else if (code == config.hotkeyPlay && btnPlay.isEnabled()) {
            startPlaying();
        } else if (code == config.hotkeyStopPlay && btnStopPlay.isEnabled()) {
            stopPlaying();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}
}
