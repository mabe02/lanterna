/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;
import com.sun.jna.Native;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static com.googlecode.lanterna.terminal.PosixLibC.*;

/**
 * Terminal implementation that uses native libraries
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
