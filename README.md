# Java AutoClicker & Macro Recorder

A lightweight, open-source Java application that lets you record and replay mouse movements, clicks, and keyboard inputs globally. Built with a modern dark UI and global input hooking.

## Features

- **Global Input Recording**: Captures mouse movements, clicks, and keystrokes even when the application is running in the background.
- **Macro Playback**: Replay your recorded actions with customizable loop counts.
- **Customizable Hotkeys**: Change the default hotkeys for Record, Stop, Play, and Stop Play directly from the Settings menu.
- **Save & Load Macros**: Export your recorded macros to a file and load them anytime later.
- **Modern Dark Theme**: Features a sleek dark UI powered by FlatLaf.

## Prerequisites

- **Java 11 or higher** installed on your system.
- (Optional) **Maven** if you wish to build the fat JAR manually.

## Technologies Used

- [JNativeHook 2.2.2](https://github.com/kwhat/jnativehook) - For listening to global keyboard and mouse events.
- [FlatLaf 3.4](https://github.com/JFormDesigner/FlatLaf) - For the modern flat look and feel.

## How to Build and Run

### Windows (Quick Start)

The project includes batch scripts for easy compilation and execution on Windows:

1. Double-click `build_and_run.bat` to automatically download the required dependencies (JNativeHook, FlatLaf), compile the Java files, and prepare the `lib` folder.
2. After the first compilation, you can simply run `run.bat` to launch the application.

### Maven Build

If you have Maven installed, you can build a standalone executable fat JAR:

```bash
mvn clean package
```

The executable JAR will be generated in the `target/` directory. Run it with:

```bash
java -jar target/JavaAutoClicker-1.0-SNAPSHOT.jar
```

## Default Hotkeys

- **Record:** `F7`
- **Stop Recording:** `F8`
- **Play Macro:** `F9`
- **Stop Playback:** `F10`

*You can change these hotkeys at any time by going to `Ayarlar > Kısayol Ayarları` (Settings > Hotkey Settings).*

## Usage

1. Open the application.
2. Press the **Record** hotkey (default: `F7`) to start capturing inputs.
3. Perform the actions you want to automate.
4. Press the **Stop Recording** hotkey (default: `F8`).
5. (Optional) Set the **Loop Count** (Tekrar Sayısı) for how many times you want the macro to repeat.
6. Press the **Play** hotkey (default: `F9`) to execute the macro.
7. To abort playback before it finishes, press **Stop Playback** (default: `F10`).

## Note on Security / Antivirus

Because this application utilizes `JNativeHook` to globally hook keyboard and mouse inputs (which is strictly required for macro recording), your Antivirus software or Windows Defender might flag the executable or the library as a "Keylogger" (`Trojan:Win32/Wacatac` or similar heuristic detections). 
This is a **false positive**. The source code is entirely open, and the application does not transmit any data externally.

## License

This project is open-source and available for personal and educational use.