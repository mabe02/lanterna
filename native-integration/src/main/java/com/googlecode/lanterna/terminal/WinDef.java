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

import com.sun.jna.*;

import java.util.Arrays;
import java.util.List;

/**
 * Class containing common Win32 structures involved when operating on the terminal
 */
public class WinDef {

    public static final HANDLE INVALID_HANDLE_VALUE = new HANDLE(Pointer.createConstant(Pointer.SIZE == 8?-1L:4294967295L));

    public static class HANDLE extends PointerType {
        private boolean immutable;

        public HANDLE() {
        }

        public HANDLE(Pointer p) {
            this.setPointer(p);
            this.immutable = true;
        }

        public Object fromNative(Object nativeValue, FromNativeContext context) {
            Object o = super.fromNative(nativeValue, context);
            return INVALID_HANDLE_VALUE.equals(o) ? INVALID_HANDLE_VALUE : o;
        }

        public void setPointer(Pointer p) {
            if(this.immutable) {
                throw new UnsupportedOperationException("immutable reference");
            } else {
                super.setPointer(p);
            }
        }

        public String toString() {
            return String.valueOf(this.getPointer());
        }
    }

    public static class WORD extends IntegerType implements Comparable<WORD> {
        public static final int SIZE = 2;

        public WORD() {
            this(0L);
        }

        public WORD(long value) {
            super(2, value, true);
        }

        public int compareTo(WORD other) {
            return compare(this, other);
        }
    }

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
        public WORD       wAttributes;
        public SMALL_RECT srWindow;
        public COORD      dwMaximumWindowSize;

        protected List getFieldOrder() {
            return Arrays.asList("dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize");
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

    private WinDef() {}
}
