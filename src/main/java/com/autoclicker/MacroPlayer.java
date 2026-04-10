package com.autoclicker;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.List;

public class MacroPlayer {

    private Robot robot;
    private volatile boolean isPlaying = false;
    private Thread playerThread;

    public MacroPlayer() {
        try {
            robot = new Robot();
            robot.setAutoDelay(0);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public synchronized void play(List<NativeMacroEvent> events, int loopCount) {
        if (isPlaying) return;
        if (events == null || events.isEmpty()) return;

        isPlaying = true;
        playerThread = new Thread(() -> {
            try {
                for (int loop = 0; loop < loopCount; loop++) {
                    for (NativeMacroEvent event : events) {
                        if (!isPlaying) break;

                        // Wait for the delay
                        if (event.getDelayFromPrevious() > 0) {
                            Thread.sleep(event.getDelayFromPrevious());
                        }

                        if (!isPlaying) break;

                        try {
                            executeEvent(event);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    if (!isPlaying) break;
                    // Add a small delay between loops to prevent freezing
                    Thread.sleep(100); 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isPlaying = false;
            }
        });
        playerThread.start();
    }

    public synchronized void stop() {
        isPlaying = false;
        if (playerThread != null) {
            playerThread.interrupt();
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    private void executeEvent(NativeMacroEvent event) throws InterruptedException {
        try {
            switch (event.getType()) {
                case WAIT:
                    if(event.getExecutionDuration() > 0) {
                        Thread.sleep(event.getExecutionDuration());
                    }
                    break;
                case MOUSE_GLIDE:
                    Point start = MouseInfo.getPointerInfo().getLocation();
                    int endX = event.getX();
                    int endY = event.getY();
                    long dur = event.getExecutionDuration();
                    if (dur <= 0) {
                        robot.mouseMove(endX, endY);
                    } else {
                        int steps = (int) (dur / 10);
                        if (steps <= 0) steps = 1;
                        for (int i = 1; i <= steps; i++) {
                            if (!isPlaying) break;
                            double progress = (double) i / steps;
                            int currX = (int) (start.x + (endX - start.x) * progress);
                            int currY = (int) (start.y + (endY - start.y) * progress);
                            robot.mouseMove(currX, currY);
                            Thread.sleep(10);
                        }
                    }
                    break;
                case KEY_PRESSED:
                    int pressCode = mapRawCodeToAWT(event.getKeyCode());
                    if(pressCode > 0 && pressCode < 65536) {
                        try {
                            robot.keyPress(pressCode);
                        } catch(Exception ex) {
                            System.err.println("Could not press key " + pressCode + " (raw " + event.getKeyCode() + ")");
                        }
                    }
                    break;
                case KEY_RELEASED:
                    int releaseCode = mapRawCodeToAWT(event.getKeyCode());
                    if(releaseCode > 0 && releaseCode < 65536) {
                        try {
                            robot.keyRelease(releaseCode);
                        } catch(Exception ex) {
                            System.err.println("Could not release key " + releaseCode + " (raw " + event.getKeyCode() + ")");
                        }
                    }
                    break;
                case MOUSE_MOVED:
                    robot.mouseMove(event.getX(), event.getY());
                    break;
                case MOUSE_PRESSED:
                    if (event.getX() > -1 && event.getY() > -1) {
                        robot.mouseMove(event.getX(), event.getY());
                    }
                    robot.mousePress(getAwtMouseButton(event.getButton()));
                    break;
                case MOUSE_RELEASED:
                    if (event.getX() > -1 && event.getY() > -1) {
                        robot.mouseMove(event.getX(), event.getY());
                    }
                    robot.mouseRelease(getAwtMouseButton(event.getButton()));
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Ignore invalid key codes or mouse buttons
            System.err.println("Failed to execute event: " + event + " Reason: " + e.getMessage());
        }
    }

    private int mapRawCodeToAWT(int rawCode) {
        switch (rawCode) {
            case 160: case 161: // LSHIFT, RSHIFT
                return java.awt.event.KeyEvent.VK_SHIFT;
            case 162: case 163: // LCTRL, RCTRL
                return java.awt.event.KeyEvent.VK_CONTROL;
            case 164: case 165: // LALT, RALT
                return java.awt.event.KeyEvent.VK_ALT;
            case 91: case 92:   // LWIN, RWIN
                return java.awt.event.KeyEvent.VK_WINDOWS;
            case 13:            // ENTER
                return java.awt.event.KeyEvent.VK_ENTER;
            case 46:            // DELETE (Windows)
                return java.awt.event.KeyEvent.VK_DELETE;
            case 190:           // PERIOD (Windows) -> AWT PERIOD is 46, but raw code is 190
                return java.awt.event.KeyEvent.VK_PERIOD;
            case 188:           // COMMA
                return java.awt.event.KeyEvent.VK_COMMA;
            case 189:           // MINUS
                return java.awt.event.KeyEvent.VK_MINUS;
            case 187:           // EQUALS
                return java.awt.event.KeyEvent.VK_EQUALS;
            case 186:           // SEMICOLON
                return java.awt.event.KeyEvent.VK_SEMICOLON;
            case 191:           // SLASH
                return java.awt.event.KeyEvent.VK_SLASH;
            case 219:           // OPEN BRACKET
                return java.awt.event.KeyEvent.VK_OPEN_BRACKET;
            case 220:           // BACK SLASH
                return java.awt.event.KeyEvent.VK_BACK_SLASH;
            case 221:           // CLOSE BRACKET
                return java.awt.event.KeyEvent.VK_CLOSE_BRACKET;
            case 222:           // QUOTE
                return java.awt.event.KeyEvent.VK_QUOTE;
            case 192:           // BACK QUOTE
                return java.awt.event.KeyEvent.VK_BACK_QUOTE;
            default:
                return rawCode;
        }
    }

    private int getAwtMouseButton(int nativeButton) {
        switch (nativeButton) {
            case NativeMouseEvent.BUTTON1:
                return InputEvent.BUTTON1_DOWN_MASK; // Left
            case NativeMouseEvent.BUTTON2:
                return InputEvent.BUTTON3_DOWN_MASK; // Right
            case NativeMouseEvent.BUTTON3:
                return InputEvent.BUTTON2_DOWN_MASK; // Middle
            case NativeMouseEvent.BUTTON4:
                // XButton1
                return InputEvent.getMaskForButton(4);
            case NativeMouseEvent.BUTTON5:
                // XButton2
                return InputEvent.getMaskForButton(5);
            default:
                return InputEvent.BUTTON1_DOWN_MASK;
        }
    }
}
