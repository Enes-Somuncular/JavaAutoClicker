package com.autoclicker;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

import java.util.ArrayList;
import java.util.List;

public class MacroRecorder implements NativeKeyListener, NativeMouseInputListener {

    private final List<NativeMacroEvent> recordedEvents = new ArrayList<>();
    private long lastEventTime = 0;
    private boolean isRecording = false;

    public void startRecording() {
        recordedEvents.clear();
        lastEventTime = System.currentTimeMillis();
        isRecording = true;
    }

    public void stopRecording() {
        isRecording = false;
    }

    public List<NativeMacroEvent> getRecordedEvents() {
        return new ArrayList<>(recordedEvents);
    }

    public void setRecordedEvents(List<NativeMacroEvent> events) {
        this.recordedEvents.clear();
        this.recordedEvents.addAll(events);
    }

    private void addEvent(NativeMacroEvent.EventType type, int keyCode, int x, int y, int button) {
        if (!isRecording) return;

        long currentTime = System.currentTimeMillis();
        long delay = currentTime - lastEventTime;
        lastEventTime = currentTime;

        recordedEvents.add(new NativeMacroEvent(type, keyCode, x, y, button, delay));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        addEvent(NativeMacroEvent.EventType.KEY_PRESSED, e.getRawCode(), 0, 0, 0);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        addEvent(NativeMacroEvent.EventType.KEY_RELEASED, e.getRawCode(), 0, 0, 0);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Ignored
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        // Ignored, we use pressed and released
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        addEvent(NativeMacroEvent.EventType.MOUSE_PRESSED, 0, e.getX(), e.getY(), e.getButton());
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        addEvent(NativeMacroEvent.EventType.MOUSE_RELEASED, 0, e.getX(), e.getY(), e.getButton());
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        addEvent(NativeMacroEvent.EventType.MOUSE_MOVED, 0, e.getX(), e.getY(), 0);
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        addEvent(NativeMacroEvent.EventType.MOUSE_MOVED, 0, e.getX(), e.getY(), 0);
    }
}
