package com.googlecode.lanterna.terminal;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Created by Martin on 2016-03-27.
 */
interface WinconEx extends StdCallLibrary {
    boolean GetConsoleScreenBufferInfo(WinNT.HANDLE hConsoleOutput, WinDefEx.CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo);
}
