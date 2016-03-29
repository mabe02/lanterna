package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.ansi.TerminalDeviceControlStrategy;
import com.sun.jna.Native;

import java.io.IOException;

import static com.googlecode.lanterna.terminal.PosixLibC.*;

/**
 * Created by martin on 27/03/16.
 */
public class NativeUnixTerminalDeviceController implements TerminalDeviceControlStrategy {

    private final PosixLibC libc;
    private PosixLibC.termios savedTerminalState;

    public NativeUnixTerminalDeviceController() {
        this.libc = (PosixLibC)Native.loadLibrary("c", PosixLibC.class);
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

    public TerminalSize getTerminalSize() throws IOException {
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
