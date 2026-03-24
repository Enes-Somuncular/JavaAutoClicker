package com.autoclicker;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "settings.properties";
    private Properties props = new Properties();

    public int hotkeyRecord = NativeKeyEvent.VC_F7;
    public int hotkeyStopRecord = NativeKeyEvent.VC_F8;
    public int hotkeyPlay = NativeKeyEvent.VC_F9;
    public int hotkeyStopPlay = NativeKeyEvent.VC_F10;

    public ConfigManager() {
        load();
    }

    public void load() {
        File f = new File(CONFIG_FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
                hotkeyRecord = Integer.parseInt(props.getProperty("hotkeyRecord", String.valueOf(NativeKeyEvent.VC_F7)));
                hotkeyStopRecord = Integer.parseInt(props.getProperty("hotkeyStopRecord", String.valueOf(NativeKeyEvent.VC_F8)));
                hotkeyPlay = Integer.parseInt(props.getProperty("hotkeyPlay", String.valueOf(NativeKeyEvent.VC_F9)));
                hotkeyStopPlay = Integer.parseInt(props.getProperty("hotkeyStopPlay", String.valueOf(NativeKeyEvent.VC_F10)));
            } catch (Exception e) {
                System.err.println("Could not load config: " + e.getMessage());
            }
        }
    }

    public void save() {
        props.setProperty("hotkeyRecord", String.valueOf(hotkeyRecord));
        props.setProperty("hotkeyStopRecord", String.valueOf(hotkeyStopRecord));
        props.setProperty("hotkeyPlay", String.valueOf(hotkeyPlay));
        props.setProperty("hotkeyStopPlay", String.valueOf(hotkeyStopPlay));
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Java AutoClicker Settings");
        } catch (Exception e) {
            System.err.println("Could not save config: " + e.getMessage());
        }
    }
}
