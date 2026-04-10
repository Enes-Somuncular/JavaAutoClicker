package com.autoclicker;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "settings.properties";
    private Properties props = new Properties();

    public int hotkeyToggleRecord = NativeKeyEvent.VC_F7;
    public int hotkeyTogglePlay = NativeKeyEvent.VC_F9;
    public int lastLoopCount = 1;
    public String lastMacroPath = "";

    public ConfigManager() {
        load();
    }

    public void load() {
        File f = new File(CONFIG_FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
                hotkeyToggleRecord = Integer.parseInt(props.getProperty("hotkeyToggleRecord", props.getProperty("hotkeyRecord", String.valueOf(NativeKeyEvent.VC_F7))));
                hotkeyTogglePlay = Integer.parseInt(props.getProperty("hotkeyTogglePlay", props.getProperty("hotkeyPlay", String.valueOf(NativeKeyEvent.VC_F9))));
                lastLoopCount = Integer.parseInt(props.getProperty("lastLoopCount", "1"));
                lastMacroPath = props.getProperty("lastMacroPath", "");
            } catch (Exception e) {
                System.err.println("Could not load config: " + e.getMessage());
            }
        }
    }

    public void save() {
        props.setProperty("hotkeyToggleRecord", String.valueOf(hotkeyToggleRecord));
        props.setProperty("hotkeyTogglePlay", String.valueOf(hotkeyTogglePlay));
        props.setProperty("lastLoopCount", String.valueOf(lastLoopCount));
        props.setProperty("lastMacroPath", lastMacroPath);
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Java AutoClicker Settings");
        } catch (Exception e) {
            System.err.println("Could not save config: " + e.getMessage());
        }
    }
}
