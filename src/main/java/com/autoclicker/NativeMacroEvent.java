package com.autoclicker;

import java.io.Serializable;

public class NativeMacroEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum EventType {
        KEY_PRESSED,
        KEY_RELEASED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_MOVED
    }

    private EventType type;
    private int keyCode; // AWT KeyCode or Raw KeyCode
    private int x;
    private int y;
    private int button;
    private long delayFromPrevious;

    public NativeMacroEvent(EventType type, int keyCode, int x, int y, int button, long delayFromPrevious) {
        this.type = type;
        this.keyCode = keyCode;
        this.x = x;
        this.y = y;
        this.button = button;
        this.delayFromPrevious = delayFromPrevious;
    }

    public EventType getType() {
        return type;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getButton() {
        return button;
    }

    public long getDelayFromPrevious() {
        return delayFromPrevious;
    }

    @Override
    public String toString() {
        return "NativeMacroEvent{" +
                "type=" + type +
                ", keyCode=" + keyCode +
                ", x=" + x +
                ", y=" + y +
                ", button=" + button +
                ", delay=" + delayFromPrevious +
                '}';
    }
}
