package com.googlecode.lanterna.terminal;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * Created by martin on 27/03/16.
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
    int ISIG = 01;
    int ICANON = 02;
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
    };
}
