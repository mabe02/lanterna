package com.googlecode.lanterna.terminal.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Wincon extends StdCallLibrary, com.sun.jna.platform.win32.Wincon {

	Wincon INSTANCE = Native.load("kernel32", Wincon.class, W32APIOptions.UNICODE_OPTIONS);

	int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;
	int DISABLE_NEWLINE_AUTO_RETURN = 0x0008;
	int ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200;

	boolean GetConsoleScreenBufferInfo(HANDLE hConsoleOutput, WinDef.CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo);

	boolean ReadConsoleInput(HANDLE hConsoleInput, WinDef.INPUT_RECORD[] lpBuffer, int nLength, IntByReference lpNumberOfEventsRead);

	boolean WriteConsole(HANDLE hConsoleOutput, String lpBuffer, int nNumberOfCharsToWrite, IntByReference lpNumberOfCharsWritten, LPVOID lpReserved);

}
