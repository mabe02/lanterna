package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import com.sun.jna.Native;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static com.googlecode.lanterna.terminal.PosixLibC.*;
import static com.googlecode.lanterna.terminal.PosixLibC.SIGWINCH;
import static com.googlecode.lanterna.terminal.PosixLibC.STDIN_FILENO;

/**
 * Created by martin on 27/03/16.
 */
public class NativeGNULinuxTerminal extends UnixLikeTerminal {

    private final PosixLibC libc;
    private PosixLibC.termios savedTerminalState;

    public NativeGNULinuxTerminal() throws IOException {
        this(System.in,
                System.out,
                Charset.defaultCharset(),
                CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
    }

    public NativeGNULinuxTerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset,
            CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {

        super(terminalInput,
                terminalOutput,
                terminalCharset,
                terminalCtrlCBehaviour);


        this.libc = (PosixLibC) Native.loadLibrary("c", PosixLibC.class);
        this.savedTerminalState = null;
    }

    public void saveTerminalSettings() throws IOException {
        savedTerminalState = getTerminalState();
    }

    public void restoreTerminalSettings() throws IOException {
        if(savedTerminalState != null) {
            libc.tcsetattr(STDIN_FILENO, TCSANOW, savedTerminalState);
        }
    }

    public void keyEchoEnabled(boolean b) throws IOException {
        PosixLibC.termios state = getTerminalState();
        if(b) {
            state.c_lflag |= ECHO;
        }
        else {
            state.c_lflag &= ~ECHO;
        }
        libc.tcsetattr(STDIN_FILENO, TCSANOW, state);
    }

    public void canonicalMode(boolean b) throws IOException {
        PosixLibC.termios state = getTerminalState();
        if(b) {
            state.c_lflag |= ICANON;
        }
        else {
            state.c_lflag &= ~ICANON;
        }
        libc.tcsetattr(STDIN_FILENO, TCSANOW, state);
    }

    public void keyStrokeSignalsEnabled(boolean b) throws IOException {
        PosixLibC.termios state = getTerminalState();
        if(b) {
            state.c_lflag |= ISIG;
        }
        else {
            state.c_lflag &= ~ISIG;
        }
        libc.tcsetattr(STDIN_FILENO, TCSANOW, state);
    }

    public void registerTerminalResizeListener(final Runnable runnable) throws IOException {
        libc.signal(SIGWINCH, new sig_t() {
            public synchronized void invoke(int signal) {
                runnable.run();
            }
        });
    }

    @Override
    protected TerminalSize findTerminalSize() throws IOException {
        PosixLibC.winsize winsize = new winsize();
        libc.ioctl(PosixLibC.STDOUT_FILENO, PosixLibC.TIOCGWINSZ, winsize);
        return new TerminalSize(winsize.ws_col, winsize.ws_row);
    }

    private PosixLibC.termios getTerminalState() {
        PosixLibC.termios termios = new PosixLibC.termios();
        libc.tcgetattr(STDIN_FILENO, termios);
        return termios;
    }
}
