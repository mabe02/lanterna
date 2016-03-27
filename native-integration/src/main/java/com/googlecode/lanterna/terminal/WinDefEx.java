package com.googlecode.lanterna.terminal;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Martin on 2016-03-27.
 */
public class WinDefEx {
    public static class COORD extends Structure {
        public short X;
        public short Y;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("X", "Y");
        }

        @Override
        public String toString() {
            return "COORD{" +
                    "X=" + X +
                    ", Y=" + Y +
                    '}';
        }
    }

    public static class SMALL_RECT extends Structure {
        public short Left;
        public short Top;
        public short Right;
        public short Bottom;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("Left", "Top", "Right", "Bottom");
        }

        @Override
        public String toString() {
            return "SMALL_RECT{" +
                    "Left=" + Left +
                    ", Top=" + Top +
                    ", Right=" + Right +
                    ", Bottom=" + Bottom +
                    '}';
        }
    }

    public static class CONSOLE_SCREEN_BUFFER_INFO extends Structure {
        public COORD      dwSize;
        public COORD      dwCursorPosition;
        public WinDef.WORD wAttributes;
        public SMALL_RECT srWindow;
        public COORD      dwMaximumWindowSize;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] {
                    "dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize"
            });
        }

        @Override
        public String toString() {
            return "CONSOLE_SCREEN_BUFFER_INFO{" +
                    "dwSize=" + dwSize +
                    ", dwCursorPosition=" + dwCursorPosition +
                    ", wAttributes=" + wAttributes +
                    ", srWindow=" + srWindow +
                    ", dwMaximumWindowSize=" + dwMaximumWindowSize +
                    '}';
        }
    }

    private WinDefEx() {}
}
