package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.ansi.TerminalDeviceControlStrategy;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Wincon;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

import java.io.IOException;

/**
 * Created by Martin on 2016-03-27.
 */
public class WindowsTerminalDeviceController implements TerminalDeviceControlStrategy {

    private final Wincon windowsConsole;
    private final WinconEx extendedConsole;
    private final WinNT.HANDLE consoleInputHandle;
    private final WinNT.HANDLE consoleOutputHandle;

    private Integer savedTerminalMode;

    public WindowsTerminalDeviceController() {
        this.windowsConsole = Kernel32.INSTANCE;
        this.extendedConsole = (WinconEx)Native.loadLibrary("kernel32", WinconEx.class, W32APIOptions.UNICODE_OPTIONS);
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
        WinDefEx.CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new WinDefEx.CONSOLE_SCREEN_BUFFER_INFO();
        extendedConsole.GetConsoleScreenBufferInfo(consoleOutputHandle, screenBufferInfo);
        int columns = screenBufferInfo.srWindow.Right - screenBufferInfo.srWindow.Left + 1;
        int rows = screenBufferInfo.srWindow.Bottom - screenBufferInfo.srWindow.Top + 1;
        return new TerminalSize(columns, rows);
    }

    private int getConsoleMode() {
        IntByReference lpMode = new IntByReference();
        windowsConsole.GetConsoleMode(consoleInputHandle, lpMode);
        return lpMode.getValue();
    }
}
