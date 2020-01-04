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

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Interface to Wincon, module in Win32 that can operate on the terminal
 */
interface Wincon extends StdCallLibrary {
    int STD_INPUT_HANDLE = -10;
    int STD_OUTPUT_HANDLE = -11;

    // SetConsoleMode input values
    int ENABLE_PROCESSED_INPUT = 1;
    int ENABLE_LINE_INPUT = 2;
    int ENABLE_ECHO_INPUT = 4;

    // SetConsoleMode screen buffer values
    int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
    int DISABLE_NEWLINE_AUTO_RETURN = 8;

    WinDef.HANDLE GetStdHandle(int var1);
    boolean GetConsoleMode(WinDef.HANDLE var1, IntByReference var2);
    boolean SetConsoleMode(WinDef.HANDLE var1, int var2);
    boolean GetConsoleScreenBufferInfo(WinDef.HANDLE hConsoleOutput, WinDef.CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo);
}
