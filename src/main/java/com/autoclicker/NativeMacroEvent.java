package com.autoclicker;

import java.io.Serializable;

public class NativeMacroEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum EventType {
        KEY_PRESSED,
        KEY_RELEASED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_MOVED,
        MOUSE_GLIDE,
        WAIT
    }

    private EventType type;
    private int keyCode; // AWT KeyCode or Raw KeyCode
    private int x;
    private int y;
    private int button;
    private long delayFromPrevious;
    private long executionDuration;

    public NativeMacroEvent(EventType type, int keyCode, int x, int y, int button, long delayFromPrevious) {
        this(type, keyCode, x, y, button, delayFromPrevious, 0);
    }

    public NativeMacroEvent(EventType type, int keyCode, int x, int y, int button, long delayFromPrevious, long executionDuration) {
        this.type = type;
        this.keyCode = keyCode;
        this.x = x;
        this.y = y;
        this.button = button;
        this.delayFromPrevious = delayFromPrevious;
        this.executionDuration = executionDuration;
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

    public void setDelayFromPrevious(long delayFromPrevious) {
        this.delayFromPrevious = delayFromPrevious;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
    
    public void setType(EventType type) {
        this.type = type;
    }
    
    public void setButton(int button) {
        this.button = button;
    }
    
    public long getExecutionDuration() {
        return executionDuration;
    }
    
    public void setExecutionDuration(long executionDuration) {
        this.executionDuration = executionDuration;
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
