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
    private JButton btnToggleRecord;
    private JButton btnTogglePlay;
    private JButton btnEdit;
    private JTextField txtLoopCount;
    private boolean isRecordingState = false;

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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    config.lastLoopCount = Integer.parseInt(txtLoopCount.getText());
                } catch (Exception ignored) {}
                config.save();
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("autosave.dat"))) {
                    oos.writeObject(recorder.getRecordedEvents());
                } catch (Exception ignored) {}
                System.exit(0);
            }
        });
        
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
        txtLoopCount = new JTextField(String.valueOf(config.lastLoopCount), 5);
        loopPanel.add(txtLoopCount);
        mainPanel.add(loopPanel);

        btnToggleRecord = new JButton();
        btnTogglePlay = new JButton();
        
        JPanel editPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnEdit = new JButton("Görsel Kur / Düzenle");
        JButton btnRestore = new JButton("Öncekini Kurtar");
        editPanel.add(btnEdit);
        editPanel.add(btnRestore);
        
        updateButtonLabels();

        mainPanel.add(btnToggleRecord);
        mainPanel.add(btnTogglePlay);
        mainPanel.add(editPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Events
        btnToggleRecord.addActionListener(e -> {
            if (isRecordingState) stopRecording();
            else startRecording();
        });
        btnTogglePlay.addActionListener(e -> {
            if (player.isPlaying()) stopPlaying();
            else startPlaying();
        });
        btnEdit.addActionListener(e -> openEditor());
        btnRestore.addActionListener(e -> {
            File f = new File("autosave.dat");
            if (f.exists() && f.isFile()) {
                loadMacroFromFile(f, false);
            } else {
                JOptionPane.showMessageDialog(this, "Kurtarılacak kaydedilmiş bir önceki makro bulunamadı!");
            }
        });
    }
    
    private void openEditor() {
        new MacroEditorDialog(this, recorder).setVisible(true);
        int count = recorder.getRecordedEvents().size();
        statusLabel.setText("Durum: Hazır (" + count + " olay)");
        updateButtonLabels(); // Kayıt göstergesi güncelle
    }

    private void updateButtonLabels() {
        boolean hasRecording = !recorder.getRecordedEvents().isEmpty();
        String dot = hasRecording ? "🟢" : "🔴"; // Yeşil = kayıt var, Kırmızı = boş
        btnToggleRecord.setText(isRecordingState
                ? "Kaydı Durdur (" + NativeKeyEvent.getKeyText(config.hotkeyToggleRecord) + ")"
                : dot + " Kaydet (" + NativeKeyEvent.getKeyText(config.hotkeyToggleRecord) + ")");
        btnTogglePlay.setText(player.isPlaying()
                ? "Durdur (" + NativeKeyEvent.getKeyText(config.hotkeyTogglePlay) + ")"
                : "Oynat (" + NativeKeyEvent.getKeyText(config.hotkeyTogglePlay) + ")");
    }

    private void saveMacro() {
        List<NativeMacroEvent> events = recorder.getRecordedEvents();
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kaydedilecek bir makro yok!");
            return;
        }
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
                oos.writeObject(events);
                statusLabel.setText("Durum: Makro kaydedildi!");
                config.lastMacroPath = f.getAbsolutePath();
                config.save();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage());
            }
        }
    }

    private void loadMacro() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            loadMacroFromFile(fc.getSelectedFile(), true);
        }
    }
    
    private void loadMacroFromFile(File file, boolean saveToConfig) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<NativeMacroEvent> events = (List<NativeMacroEvent>) ois.readObject();
            recorder.setRecordedEvents(events);
            statusLabel.setText("Durum: Makro yüklendi! (" + events.size() + " olay)");
            if (saveToConfig) {
                config.lastMacroPath = file.getAbsolutePath();
                config.save();
            }
            updateButtonLabels(); // Kayıt göstergesi güncelle
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    private void startRecording() {
        // Eğer mevcut kayıt varsa onay iste
        if (!recorder.getRecordedEvents().isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Mevcut kayıt silinecek ve yeni kayıt başlayacak.\nÖnceki kaydınızı kaybedeceksiniz — emin misiniz?",
                    "Kaydı Sıfırla?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
        }
        isRecordingState = true;
        btnTogglePlay.setEnabled(false);
        btnEdit.setEnabled(false);
        updateButtonLabels();
        statusLabel.setText("Durum: Kaydediliyor...");
        recorder.startRecording();
    }

    private void stopRecording() {
        recorder.stopRecording();
        isRecordingState = false;
        btnTogglePlay.setEnabled(true);
        btnEdit.setEnabled(true);
        updateButtonLabels(); // 🟢 göstergesi güncellenir
        
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

        btnToggleRecord.setEnabled(false);
        btnEdit.setEnabled(false);
        updateButtonLabels();
        statusLabel.setText("Durum: Oynatılıyor...");

        player.play(events, loopCount);

        // Background thread to check when player finishes
        final int targetLoop = loopCount;
        new Thread(() -> {
            while (player.isPlaying()) {
                final int current = player.getCurrentLoopCount();
                SwingUtilities.invokeLater(() -> {
                    if (player.isPlaying()) {
                        String suffix = (targetLoop == -1) ? ")" : " / " + targetLoop + ")";
                        statusLabel.setText("Durum: Oynatılıyor... (Tekrar: " + current + suffix);
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    break;
                }
            }
            SwingUtilities.invokeLater(() -> {
                int lastLoop = player.getCurrentLoopCount();
                statusLabel.setText("Durum: Durduruldu/Tamamlandı (Biten Tur Sayısı: " + lastLoop + ")");
                btnToggleRecord.setEnabled(true);
                btnEdit.setEnabled(true);
                updateButtonLabels();
            });
        }).start();
    }

    private void stopPlaying() {
        player.stop();
        int lastLoop = player.getCurrentLoopCount();
        statusLabel.setText("Durum: Durduruldu/Tamamlandı (Biten Tur Sayısı: " + lastLoop + ")");
        btnToggleRecord.setEnabled(true);
        btnEdit.setEnabled(true);
        updateButtonLabels();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int code = e.getKeyCode();
        if (code == config.hotkeyToggleRecord && btnToggleRecord.isEnabled()) {
            if (isRecordingState) stopRecording();
            else startRecording();
        } else if (code == config.hotkeyTogglePlay && btnTogglePlay.isEnabled()) {
            if (player.isPlaying()) stopPlaying();
            else startPlaying();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}
}
