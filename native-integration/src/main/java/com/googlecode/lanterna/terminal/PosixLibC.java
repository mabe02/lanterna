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

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * Interface to Posix libc
 */
public interface PosixLibC extends Library {
    int tcgetattr(int fd, termios termios_p);
    int tcsetattr(int fd, int optional_actions, termios termios_p);
    int ioctl(int fd, int request, winsize winsize);
    sig_t signal(int sig, sig_t fn);

    // Constants
    int STDIN_FILENO = 0;
    int STDOUT_FILENO = 1;
    int TCSANOW = 0;
    int NCCS = 32;

    // Constants for c_lflag (beware of octal numbers below!!)
    @SuppressWarnings("OctalInteger")
    int ISIG = 01;
    @SuppressWarnings("OctalInteger")
    int ICANON = 02;
    @SuppressWarnings("OctalInteger")
    int ECHO = 010;

    // Signals
    int SIGWINCH = 28;

    // Constants for ioctl
    int TIOCGWINSZ = 0x5413;

    interface sig_t extends Callback {
        void invoke(int signal);
    }

    class termios extends Structure {
        public int c_iflag;           // input mode flags
        public int c_oflag;           // output mode flags
        public int c_cflag;           // control mode flags
        public int c_lflag;           // local mode flags
        public byte c_line;           // line discipline
        public byte c_cc[];           // control characters
        public int c_ispeed;          // input speed
        public int c_ospeed;          // output speed

        public termios() {
            c_cc = new byte[NCCS];
        }

        protected List getFieldOrder() {
            return Arrays.asList(
                    "c_iflag",
                    "c_oflag",
                    "c_cflag",
                    "c_lflag",
                    "c_line",
                    "c_cc",
                    "c_ispeed",
                    "c_ospeed"
            );
        }

        @Override
        public String toString() {
            return "termios{" +
                    "c_iflag=" + c_iflag +
                    ", c_oflag=" + c_oflag +
                    ", c_cflag=" + c_cflag +
                    ", c_lflag=" + c_lflag +
                    ", c_line=" + c_line +
                    ", c_cc=" + Arrays.toString(c_cc) +
                    ", c_ispeed=" + c_ispeed +
                    ", c_ospeed=" + c_ospeed +
                    '}';
        }
    }

    class winsize extends Structure
    {
        public short ws_row;
        public short ws_col;
        public short ws_xpixel;
        public short ws_ypixel;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("ws_row", "ws_col", "ws_xpixel", "ws_ypixel");
        }

        @Override
        public String toString() {
            return "winsize{" +
                    "ws_row=" + ws_row +
                    ", ws_col=" + ws_col +
                    ", ws_xpixel=" + ws_xpixel +
                    ", ws_ypixel=" + ws_ypixel +
                    '}';
        }
    }
}
