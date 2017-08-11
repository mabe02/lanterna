package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Terminal implementation for the regular Windows cmd.exe terminal emulator, using native invocations through jna to
 * interact with it.
 */
public class WindowsTerminal extends UnixLikeTerminal {

    private static final Wincon WINDOWS_CONSOLE = (Wincon) Native.loadLibrary("kernel32", Wincon.class, W32APIOptions.UNICODE_OPTIONS);
    private static final WinDef.HANDLE CONSOLE_INPUT_HANDLE = WINDOWS_CONSOLE.GetStdHandle(Wincon.STD_INPUT_HANDLE);
    private static final WinDef.HANDLE CONSOLE_OUTPUT_HANDLE = WINDOWS_CONSOLE.GetStdHandle(Wincon.STD_OUTPUT_HANDLE);

    private Integer savedTerminalInputMode;
    private Integer savedTerminalOutputMode;

    public WindowsTerminal() throws IOException {
        this(System.in, System.out, Charset.defaultCharset(), CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
    }

    public WindowsTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {

        super(terminalInput,
                terminalOutput,
                terminalCharset,
                terminalCtrlCBehaviour);
    }

    @Override
    protected void acquire() throws IOException {
        super.acquire();
        int terminalOutputMode = getConsoleOutputMode();
        terminalOutputMode |= Wincon.ENABLE_VIRTUAL_TERMINAL_PROCESSING;
        terminalOutputMode |= Wincon.DISABLE_NEWLINE_AUTO_RETURN;
        WINDOWS_CONSOLE.SetConsoleMode(CONSOLE_OUTPUT_HANDLE, terminalOutputMode);
    }

    @Override
    public synchronized void saveTerminalSettings() throws IOException {
        this.savedTerminalInputMode = getConsoleInputMode();
        this.savedTerminalOutputMode = getConsoleOutputMode();
    }

    @Override
    public synchronized void restoreTerminalSettings() throws IOException {
        if(savedTerminalInputMode != null) {
            WINDOWS_CONSOLE.SetConsoleMode(CONSOLE_INPUT_HANDLE, savedTerminalInputMode);
            WINDOWS_CONSOLE.SetConsoleMode(CONSOLE_OUTPUT_HANDLE, savedTerminalOutputMode);
        }
    }

    @Override
    public synchronized void keyEchoEnabled(boolean enabled) throws IOException {
        int mode = getConsoleInputMode();
        if(enabled) {
            mode |= Wincon.ENABLE_ECHO_INPUT;
        }
        else {
            mode &= ~Wincon.ENABLE_ECHO_INPUT;
        }
        WINDOWS_CONSOLE.SetConsoleMode(CONSOLE_INPUT_HANDLE, mode);
    }

    @Override
    public synchronized void canonicalMode(boolean enabled) throws IOException {
        int mode = getConsoleInputMode();
        if(enabled) {
            mode |= Wincon.ENABLE_LINE_INPUT;
        }
        else {
            mode &= ~Wincon.ENABLE_LINE_INPUT;
        }
        WINDOWS_CONSOLE.SetConsoleMode(CONSOLE_INPUT_HANDLE, mode);
    }

    @Override
    public synchronized void keyStrokeSignalsEnabled(boolean enabled) throws IOException {
        int mode = getConsoleInputMode();
        if(enabled) {
            mode |= Wincon.ENABLE_PROCESSED_INPUT;
        }
        else {
            mode &= ~Wincon.ENABLE_PROCESSED_INPUT;
        }
        WINDOWS_CONSOLE.SetConsoleMode(CONSOLE_INPUT_HANDLE, mode);
    }


    @Override
    protected TerminalSize findTerminalSize() throws IOException {
        WinDef.CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new WinDef.CONSOLE_SCREEN_BUFFER_INFO();
        WINDOWS_CONSOLE.GetConsoleScreenBufferInfo(CONSOLE_OUTPUT_HANDLE, screenBufferInfo);
        int columns = screenBufferInfo.srWindow.Right - screenBufferInfo.srWindow.Left + 1;
        int rows = screenBufferInfo.srWindow.Bottom - screenBufferInfo.srWindow.Top + 1;
        return new TerminalSize(columns, rows);
    }

    @Override
    public void registerTerminalResizeListener(Runnable runnable) throws IOException {
        // Not implemented yet
    }

    public synchronized TerminalPosition getCursorPosition() {
        WinDef.CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new WinDef.CONSOLE_SCREEN_BUFFER_INFO();
        WINDOWS_CONSOLE.GetConsoleScreenBufferInfo(CONSOLE_OUTPUT_HANDLE, screenBufferInfo);
        int column = screenBufferInfo.dwCursorPosition.X - screenBufferInfo.srWindow.Left;
        int row = screenBufferInfo.dwCursorPosition.Y - screenBufferInfo.srWindow.Top;
        return new TerminalPosition(column, row);
    }

    private int getConsoleInputMode() {
        IntByReference lpMode = new IntByReference();
        WINDOWS_CONSOLE.GetConsoleMode(CONSOLE_INPUT_HANDLE, lpMode);
        return lpMode.getValue();
    }

    private int getConsoleOutputMode() {
        IntByReference lpMode = new IntByReference();
        WINDOWS_CONSOLE.GetConsoleMode(CONSOLE_OUTPUT_HANDLE, lpMode);
        return lpMode.getValue();
    }
}
