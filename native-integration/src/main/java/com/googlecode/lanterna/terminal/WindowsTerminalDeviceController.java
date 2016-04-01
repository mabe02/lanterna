package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.ansi.TerminalDeviceControlStrategy;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

import java.io.IOException;

/**
 * Created by Martin on 2016-03-27.
 */
public class WindowsTerminalDeviceController implements TerminalDeviceControlStrategy {

    private final Wincon windowsConsole;
    private final WinDef.HANDLE consoleInputHandle;
    private final WinDef.HANDLE consoleOutputHandle;

    private Integer savedTerminalMode;

    public WindowsTerminalDeviceController() {
        this.windowsConsole = (Wincon)Native.loadLibrary("kernel32", Wincon.class, W32APIOptions.UNICODE_OPTIONS);
        this.consoleInputHandle = windowsConsole.GetStdHandle(Wincon.STD_INPUT_HANDLE);
        this.consoleOutputHandle = windowsConsole.GetStdHandle(Wincon.STD_OUTPUT_HANDLE);
        this.savedTerminalMode = null;
    }

    @Override
    public synchronized void saveTerminalSettings() throws IOException {
        this.savedTerminalMode = getConsoleMode();
    }

    @Override
    public synchronized void restoreTerminalSettings() throws IOException {
        if(savedTerminalMode != null) {
            windowsConsole.SetConsoleMode(consoleInputHandle, savedTerminalMode);
        }
    }

    @Override
    public synchronized void keyEchoEnabled(boolean enabled) throws IOException {
        int mode = getConsoleMode();
        if(enabled) {
            mode |= Wincon.ENABLE_ECHO_INPUT;
        }
        else {
            mode &= ~Wincon.ENABLE_ECHO_INPUT;
        }
        windowsConsole.SetConsoleMode(consoleInputHandle, mode);
    }

    @Override
    public synchronized void canonicalMode(boolean enabled) throws IOException {
        int mode = getConsoleMode();
        if(enabled) {
            mode |= Wincon.ENABLE_LINE_INPUT;
        }
        else {
            mode &= ~Wincon.ENABLE_LINE_INPUT;
        }
        windowsConsole.SetConsoleMode(consoleInputHandle, mode);
    }

    @Override
    public synchronized void keyStrokeSignalsEnabled(boolean enabled) throws IOException {
        int mode = getConsoleMode();
        if(enabled) {
            mode |= Wincon.ENABLE_PROCESSED_INPUT;
        }
        else {
            mode &= ~Wincon.ENABLE_PROCESSED_INPUT;
        }
        windowsConsole.SetConsoleMode(consoleInputHandle, mode);
    }

    @Override
    public synchronized TerminalSize getTerminalSize() throws IOException {
        WinDef.CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new WinDef.CONSOLE_SCREEN_BUFFER_INFO();
        windowsConsole.GetConsoleScreenBufferInfo(consoleOutputHandle, screenBufferInfo);
        int columns = screenBufferInfo.srWindow.Right - screenBufferInfo.srWindow.Left + 1;
        int rows = screenBufferInfo.srWindow.Bottom - screenBufferInfo.srWindow.Top + 1;
        return new TerminalSize(columns, rows);
    }

    @Override
    public void registerTerminalResizeListener(Runnable runnable) throws IOException {
        // Not implemented yet
    }

    public TerminalPosition getCursorPosition() {
        WinDef.CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new WinDef.CONSOLE_SCREEN_BUFFER_INFO();
        windowsConsole.GetConsoleScreenBufferInfo(consoleOutputHandle, screenBufferInfo);
        int column = screenBufferInfo.dwCursorPosition.X - screenBufferInfo.srWindow.Left;
        int row = screenBufferInfo.dwCursorPosition.Y - screenBufferInfo.srWindow.Top;
        return new TerminalPosition(column, row);
    }

    private int getConsoleMode() {
        IntByReference lpMode = new IntByReference();
        windowsConsole.GetConsoleMode(consoleInputHandle, lpMode);
        return lpMode.getValue();
    }
}
