package com.googlecode.lanterna.terminal;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Created by Martin on 2016-03-27.
 */
interface Wincon extends StdCallLibrary {
    int STD_INPUT_HANDLE = -10;
    int STD_OUTPUT_HANDLE = -11;

    int ENABLE_PROCESSED_INPUT = 1;
    int ENABLE_LINE_INPUT = 2;
    int ENABLE_ECHO_INPUT = 4;

    WinDef.HANDLE GetStdHandle(int var1);
    boolean GetConsoleMode(WinDef.HANDLE var1, IntByReference var2);
    boolean SetConsoleMode(WinDef.HANDLE var1, int var2);
    boolean GetConsoleScreenBufferInfo(WinDef.HANDLE hConsoleOutput, WinDef.CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo);
}
