package com.autoclicker;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.ArrayList;
import java.util.List;

public class MacroEditorDialog extends JDialog {

    private final MacroRecorder recorder;
    private final List<NativeMacroEvent> events;
    private JTable table;
    private DefaultTableModel tableModel;

    public MacroEditorDialog(JFrame parent, MacroRecorder recorder) {
        super(parent, "Makro Düzenleyici (Timeline & Kurucu)", true); // Modal
        this.recorder = recorder;
        // Work on a copy so we can cancel
        this.events = new ArrayList<>(recorder.getRecordedEvents());

        initUI();
    }

    private void initUI() {
        setSize(750, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Table
        String[] columns = {"Sıra", "Eylem Türü", "Tuş/Detay", "X", "Y", "Gecikme", "Süreklilik"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0; // Prevent inline edit only for 0 (Sıra), rest is editable
            }
            
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                NativeMacroEvent event = events.get(row);
                try {
                    String strVal = aValue.toString().trim();
                    switch (column) {
                        case 1: // Tür
                            event.setType(NativeMacroEvent.EventType.valueOf(strVal));
                            break;
                        case 2: // Tuş/Detay
                            String numStr = strVal.replaceAll("[^0-9]", "");
                            if(!numStr.isEmpty()) {
                                int val = Integer.parseInt(numStr);
                                if(event.getType().name().contains("KEY")) event.setKeyCode(val);
                                else if(event.getType().name().contains("MOUSE")) event.setButton(val);
                            }
                            break;
                        case 3: // X
                            if(!strVal.equals("-")) event.setX(Integer.parseInt(strVal));
                            break;
                        case 4: // Y
                            if(!strVal.equals("-")) event.setY(Integer.parseInt(strVal));
                            break;
                        case 5: // Gecikme
                            event.setDelayFromPrevious(Long.parseLong(strVal));
                            break;
                        case 6: // Süreklilik
                            event.setExecutionDuration(Long.parseLong(strVal));
                            break;
                    }
                } catch(Exception ignored) {}
                
                SwingUtilities.invokeLater(() -> refreshTable());
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new TableRowTransferHandler());
        
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Top toolbar (Adding actions)
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddVisual = new JButton("Yeni Tıklama Ekle (Nokta Seç)");
        JButton btnAddInPlaceClick = new JButton("Fare Konumuna Göre Tık Ekle");
        JButton btnAddKey = new JButton("Yeni Tuş Ekle (Klavye)");
        JButton btnAddWait = new JButton("Sadece Bekleme Ekle");
        JButton btnAddQuickPressRelease = new JButton("Bas/Çek Ekle (Kısayol)");
        
        topToolbar.add(btnAddVisual);
        topToolbar.add(btnAddInPlaceClick);
        topToolbar.add(btnAddKey);
        topToolbar.add(btnAddWait);
        topToolbar.add(btnAddQuickPressRelease);
        
        // Bottom toolbar (Editing actions)
        JPanel bottomToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEditDelay = new JButton("Süreyi Düzenle");
        JButton btnSpeedUp = new JButton("Hızlandır (-50%)");
        JButton btnSlowDown = new JButton("Yavaşlat (+100%)");
        JButton btnDelete = new JButton("Seçiliyi Sil");
        JButton btnClearAll = new JButton("Tümünü Temizle");
        
        bottomToolbar.add(btnEditDelay);
        bottomToolbar.add(btnSpeedUp);
        bottomToolbar.add(btnSlowDown);
        bottomToolbar.add(btnDelete);
        bottomToolbar.add(btnClearAll);
        
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Kaydet ve Kapat");
        btnSave.setBackground(new Color(60, 150, 60));
        btnSave.setForeground(Color.WHITE);
        savePanel.add(btnSave);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(bottomToolbar, BorderLayout.CENTER);
        bottomContainer.add(savePanel, BorderLayout.EAST);

        add(topToolbar, BorderLayout.NORTH);
        add(bottomContainer, BorderLayout.SOUTH);

        // Actions
        btnAddVisual.addActionListener(e -> startPointPicker());
        btnAddInPlaceClick.addActionListener(e -> addInPlaceClickAction());
        btnAddKey.addActionListener(e -> addKeyboardAction());
        btnAddWait.addActionListener(e -> addWaitAction());
        btnAddQuickPressRelease.addActionListener(e -> addQuickPressReleaseAction());
        btnEditDelay.addActionListener(e -> editSelectedDelay());
        btnSpeedUp.addActionListener(e -> multiplyDelays(0.5));
        btnSlowDown.addActionListener(e -> multiplyDelays(2.0));
        btnDelete.addActionListener(e -> deleteSelected());
        btnClearAll.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Tablodaki listeyi tamamen silmek / temizlemek istediğinize emin misiniz?", "Tümünü Temizle", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                events.clear();
                refreshTable();
            }
        });
        btnSave.addActionListener(e -> {
            recorder.setRecordedEvents(this.events);
            dispose();
        });
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < events.size(); i++) {
            NativeMacroEvent e = events.get(i);
            String detail = "";
            if (e.getType() == NativeMacroEvent.EventType.KEY_PRESSED || e.getType() == NativeMacroEvent.EventType.KEY_RELEASED) {
                detail = "Raw Key: " + e.getKeyCode();
            } else if (e.getType() == NativeMacroEvent.EventType.MOUSE_PRESSED || e.getType() == NativeMacroEvent.EventType.MOUSE_RELEASED) {
                detail = "Button: " + e.getButton();
            } else if (e.getType() == NativeMacroEvent.EventType.WAIT) {
                detail = "Bekliyor";
            }
            
            Object[] row = {
                i + 1,
                e.getType().name(),
                detail,
                (e.getType() == NativeMacroEvent.EventType.WAIT || (e.getX() <= -1 && e.getY() <= -1)) ? "-" : e.getX(),
                (e.getType() == NativeMacroEvent.EventType.WAIT || (e.getX() <= -1 && e.getY() <= -1)) ? "-" : e.getY(),
                e.getDelayFromPrevious(),
                e.getExecutionDuration()
            };
            tableModel.addRow(row);
        }
    }

    private void startPointPicker() {
        JOptionPane.showMessageDialog(this, "Tamam'a tıkladıktan sonra ekranın İSTEDİĞİNİZ bir yerine farenizle TIKLAYIN.");
        
        com.github.kwhat.jnativehook.mouse.NativeMouseInputListener listener = new com.github.kwhat.jnativehook.mouse.NativeMouseInputListener() {
            @Override
            public void nativeMouseClicked(NativeMouseEvent e) {}

            @Override
            public void nativeMousePressed(NativeMouseEvent e) {
                com.github.kwhat.jnativehook.GlobalScreen.removeNativeMouseListener(this);
                com.github.kwhat.jnativehook.GlobalScreen.removeNativeMouseMotionListener(this);
                
                SwingUtilities.invokeLater(() -> {
                    askOptionsForVisualPoint(e.getX(), e.getY());
                });
            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent e) {}
            @Override
            public void nativeMouseMoved(NativeMouseEvent e) {}
            @Override
            public void nativeMouseDragged(NativeMouseEvent e) {}
        };
        
        com.github.kwhat.jnativehook.GlobalScreen.addNativeMouseListener(listener);
        com.github.kwhat.jnativehook.GlobalScreen.addNativeMouseMotionListener(listener);
    }

    private void askOptionsForVisualPoint(int x, int y) {
        String[] options = {
            "Sol Tuşa BAS (Press)", 
            "Sol Tuşu BIRAK (Release)", 
            "Sağ Tuşa BAS (Press)", 
            "Sağ Tuşu BIRAK (Release)",
            "Sadece Fareyi Oraya Götür / Kaydır (Move)"
        };
        Object selection = JOptionPane.showInputDialog(this, 
                "Koordinat: (" + x + "," + y + ") | Ne yapılacak?", 
                "Eylem Türü", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selection == null) return;

        String delayStr = JOptionPane.showInputDialog(this, "Gecikme süresi (ms):", "80");
        if (delayStr == null) return;

        long delay = 80;
        try {
            delay = Long.parseLong(delayStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Geçersiz süre!");
            return;
        }

        if (selection.equals(options[4])) { // Move/Glide
            String durStr = JOptionPane.showInputDialog(this, "Fare oraya kaç milisaniyede kaysın/gitsin?\n(0 yazarsanız anında ışınlanır):", "500");
            long dur = 500;
            try { dur = Long.parseLong(durStr); } catch(Exception ignored){}
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_GLIDE, 0, x, y, 0, delay, dur));
        } else if (selection.equals(options[0])) { // Only Press Left
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_PRESSED, 0, x, y, NativeMouseEvent.BUTTON1, delay));
        } else if (selection.equals(options[1])) { // Only Release Left
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_RELEASED, 0, x, y, NativeMouseEvent.BUTTON1, delay));
        } else if (selection.equals(options[2])) { // Only Press Right
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_PRESSED, 0, x, y, NativeMouseEvent.BUTTON2, delay));
        } else if (selection.equals(options[3])) { // Only Release Right
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_RELEASED, 0, x, y, NativeMouseEvent.BUTTON2, delay));
        }

        refreshTable();
    }
    
    private void addWaitAction() {
        String delayStr = JOptionPane.showInputDialog(this, "Ne kadar bekleme süresi eklensin (ms)?", "80");
        if (delayStr == null) return;
        try {
            long d = Long.parseLong(delayStr);
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.WAIT, 0, -1, -1, 0, 0, d));
            refreshTable();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Geçersiz giriş!");
        }
    }
    
    private void addInPlaceClickAction() {
        String[] options = {
            "Sol Tuşa BAS (Bulunduğu Yerde)", 
            "Sol Tuşu BIRAK (Bulunduğu Yerde)", 
            "Sağ Tuşa BAS (Bulunduğu Yerde)", 
            "Sağ Tuşu BIRAK (Bulunduğu Yerde)"
        };
        Object selection = JOptionPane.showInputDialog(this, 
                "Şu anki fare imlecinin bulunduğu yerde ne yapılsın?:", 
                "Bulunduğu Yere Tık", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selection == null) return;

        String delayStr = JOptionPane.showInputDialog(this, "Gecikme süresi (ms):", "80");
        if (delayStr == null) return;

        long delay = 80;
        try {
            delay = Long.parseLong(delayStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Geçersiz süre!");
            return;
        }

        if (selection.equals(options[0])) { // Only Press Left
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_PRESSED, 0, -1, -1, NativeMouseEvent.BUTTON1, delay));
        } else if (selection.equals(options[1])) { // Only Release Left
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_RELEASED, 0, -1, -1, NativeMouseEvent.BUTTON1, delay));
        } else if (selection.equals(options[2])) { // Only Press Right
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_PRESSED, 0, -1, -1, NativeMouseEvent.BUTTON2, delay));
        } else if (selection.equals(options[3])) { // Only Release Right
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_RELEASED, 0, -1, -1, NativeMouseEvent.BUTTON2, delay));
        }

        refreshTable();
    }

    private void addKeyboardAction() {
        String inputStr = JOptionPane.showInputDialog(this, 
                "Basılacak Tuşu KLAVYEDEN YAZIN\n(Örn: A, 1, ENTER, SPACE, CTRL, SHIFT, TAB):", 
                "");

        if (inputStr == null || inputStr.trim().isEmpty()) return;
        
        String selection = inputStr.trim().toUpperCase();

        int rawCode = 0;
        switch (selection) {
            case "ENTER": rawCode = 13; break;
            case "SPACE": rawCode = 32; break;
            case "SHIFT": rawCode = 160; break;
            case "CTRL": rawCode = 162; break;
            case "ALT": rawCode = 164; break;
            case "BACKSPACE": rawCode = 8; break;
            case "ESCAPE": rawCode = 27; break;
            case "TAB": rawCode = 9; break;
            default:
                if (selection.length() == 1) {
                    char c = selection.charAt(0);
                    rawCode = (int) c;
                } else {
                    JOptionPane.showMessageDialog(this, "Geçersiz veya tanınmayan tuş metni!");
                    return;
                }
                break;
        }
        
        String[] actionOptions = {
            "Sadece Tuşa BAS (Press)",
            "Sadece Tuşu BIRAK (Release)"
        };
        
        Object actionSelection = JOptionPane.showInputDialog(this, 
                "Ney yapmak istiyorsunuz?", 
                "Bas/Çek", JOptionPane.QUESTION_MESSAGE, null, actionOptions, actionOptions[0]);
                
        if(actionSelection == null) return;

        String delayStr = JOptionPane.showInputDialog(this, "Gecikme süresi (ms):", "80");
        if (delayStr == null) return;

        long delay = 80;
        try {
            delay = Long.parseLong(delayStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Geçersiz süre!");
            return;
        }

        if (actionSelection.equals(actionOptions[0])) { // Press Only
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.KEY_PRESSED, rawCode, -1, -1, 0, delay));
        } else if (actionSelection.equals(actionOptions[1])) { // Release Only
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.KEY_RELEASED, rawCode, -1, -1, 0, delay));
        }
        
        refreshTable();
    }

    private void addQuickPressReleaseAction() {
        String[] options = {"Klavye Tuşu", "Fare Sol Tık", "Fare Sağ Tık"};
        Object selection = JOptionPane.showInputDialog(this, 
                "Neye hızlı (Bas + Çek) işlemi eklemek istiyorsunuz?", 
                "Bas/Çek Kısayol", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selection == null) return;
        
        long delay = 80;
        String delayStr = JOptionPane.showInputDialog(this, "Bu işlemden önceki bekleme süresi (ms):", "80");
        if (delayStr != null) {
            try { delay = Long.parseLong(delayStr); } catch (Exception ignored) {}
        }
        
        if (selection.equals(options[0])) { // Klavye
            String inputStr = JOptionPane.showInputDialog(this, "Klavyeden bas/çek yapılacak tuşu yazın (Örn: A, 1, ENTER, SPACE vb.):", "");
            if (inputStr == null || inputStr.trim().isEmpty()) return;
            
            String sel = inputStr.trim().toUpperCase();
            int rawCode = 0;
            switch (sel) {
                case "ENTER": rawCode = 13; break;
                case "SPACE": rawCode = 32; break;
                case "SHIFT": rawCode = 160; break;
                case "CTRL": rawCode = 162; break;
                case "ALT": rawCode = 164; break;
                case "BACKSPACE": rawCode = 8; break;
                case "ESCAPE": rawCode = 27; break;
                case "TAB": rawCode = 9; break;
                default:
                    if (sel.length() == 1) {
                        rawCode = (int) sel.charAt(0);
                    } else {
                        JOptionPane.showMessageDialog(this, "Geçersiz veya tanınmayan tuş metni!");
                        return;
                    }
                    break;
            }
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.KEY_PRESSED, rawCode, -1, -1, 0, delay));
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.KEY_RELEASED, rawCode, -1, -1, 0, 50)); // 50ms çekme gecikmesi
        } else if (selection.equals(options[1])) { // Fare Sol
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_PRESSED, 0, -1, -1, NativeMouseEvent.BUTTON1, delay));
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_RELEASED, 0, -1, -1, NativeMouseEvent.BUTTON1, 50));
        } else if (selection.equals(options[2])) { // Fare Sağ
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_PRESSED, 0, -1, -1, NativeMouseEvent.BUTTON2, delay));
            events.add(new NativeMacroEvent(NativeMacroEvent.EventType.MOUSE_RELEASED, 0, -1, -1, NativeMouseEvent.BUTTON2, 50));
        }
        refreshTable();
    }

    private void editSelectedDelay() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir satır seçin.");
            return;
        }

        NativeMacroEvent event = events.get(row);
        String delayStr = JOptionPane.showInputDialog(this, "Yeni bekleme süresini girin (ms):", String.valueOf(event.getDelayFromPrevious()));
        if (delayStr == null) return;

        try {
            long newDelay = Long.parseLong(delayStr);
            event.setDelayFromPrevious(newDelay);
            refreshTable();
            table.setRowSelectionInterval(row, row);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Geçersiz değer!");
        }
    }

    private void multiplyDelays(double multiplier) {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Lütfen hızlandırmak/yavaşlatmak istediğiniz satırları seçin.");
            return;
        }
        for (int r : rows) {
            NativeMacroEvent event = events.get(r);
            event.setDelayFromPrevious((long) (event.getDelayFromPrevious() * multiplier));
        }
        refreshTable();
    }

    private void deleteSelected() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) return;

        // Traverse backwards to avoid shifting indexes
        for (int i = rows.length - 1; i >= 0; i--) {
            events.remove(rows[i]);
        }
        refreshTable();
    }
    
    // Sürükle ve Bırak (Drag and Drop) İşleyicisi
    private class TableRowTransferHandler extends TransferHandler {
        private int[] indices = null;

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            indices = table.getSelectedRows();
            return new StringSelection(""); // Dummy
        }

        @Override
        public boolean canImport(TransferSupport info) {
            if (!info.isDrop() || !info.getComponent().equals(table)) {
                return false;
            }
            return true;
        }

        @Override
        public boolean importData(TransferSupport info) {
            if (!canImport(info)) return false;

            JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
            int index = dl.getRow();
            int max = table.getModel().getRowCount();
            if (index < 0 || index > max) { index = max; }

            List<NativeMacroEvent> dragged = new ArrayList<>();
            for (int i : indices) { dragged.add(events.get(i)); }

            for (int i = indices.length - 1; i >= 0; i--) {
                events.remove(indices[i]);
            }

            int adjustedIndex = index;
            for (int i : indices) {
                if (i < index) {
                    adjustedIndex--;
                }
            }

            for(int i = 0; i < dragged.size(); i++) {
                events.add(adjustedIndex + i, dragged.get(i));
            }

            refreshTable();
            table.getSelectionModel().clearSelection();
            table.setRowSelectionInterval(adjustedIndex, adjustedIndex + dragged.size() - 1);
            return true;
        }
    }
}
