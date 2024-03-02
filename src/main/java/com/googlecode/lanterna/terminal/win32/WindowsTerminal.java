package com.googlecode.lanterna.terminal.win32;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.BasicCharacterPattern;
import com.googlecode.lanterna.input.CharacterPattern;
import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.win32.WinDef.CONSOLE_SCREEN_BUFFER_INFO;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;
import com.sun.jna.ptr.IntByReference;

public class WindowsTerminal extends UnixLikeTerminal {

	private static final Charset CONSOLE_CHARSET = StandardCharsets.UTF_8;
	private static final WindowsConsoleInputStream CONSOLE_INPUT = new WindowsConsoleInputStream(CONSOLE_CHARSET);
	private static final WindowsConsoleOutputStream CONSOLE_OUTPUT = new WindowsConsoleOutputStream(CONSOLE_CHARSET);

	private int[] settings;

	public WindowsTerminal() throws IOException {
		this(CONSOLE_INPUT, CONSOLE_OUTPUT, CONSOLE_CHARSET, CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
	}

	public WindowsTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset, CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {
		super(CONSOLE_INPUT, CONSOLE_OUTPUT, CONSOLE_CHARSET, terminalCtrlCBehaviour);

		// handle resize events
		CONSOLE_INPUT.onWindowBufferSizeEvent(evt -> {
			onResized(evt.dwSize.X, evt.dwSize.Y);
		});
	}

	@Override
	protected KeyDecodingProfile getDefaultKeyDecodingProfile() {
		ArrayList<CharacterPattern> keyDecodingProfile = new ArrayList<CharacterPattern>();
		// handle Key Code 13 as ENTER
		keyDecodingProfile.add(new BasicCharacterPattern(new KeyStroke(KeyType.ENTER), '\r'));
		// handle everything else as per default
		keyDecodingProfile.addAll(super.getDefaultKeyDecodingProfile().getPatterns());
		return () -> keyDecodingProfile;
	}

	@Override
	protected void acquire() throws IOException {
		super.acquire();

		int terminalOutputMode = getConsoleOutputMode();
		terminalOutputMode |= Wincon.ENABLE_VIRTUAL_TERMINAL_PROCESSING;
		terminalOutputMode |= Wincon.DISABLE_NEWLINE_AUTO_RETURN;
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_OUTPUT.getHandle(), terminalOutputMode);

		int terminalInputMode = getConsoleInputMode();
		terminalInputMode |= Wincon.ENABLE_MOUSE_INPUT;
		terminalInputMode |= Wincon.ENABLE_WINDOW_INPUT;
		terminalInputMode |= Wincon.ENABLE_VIRTUAL_TERMINAL_INPUT;
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT.getHandle(), terminalInputMode);
	}

	@Override
	public void saveTerminalSettings() {
		settings = new int[] { getConsoleInputMode(), getConsoleOutputMode() };
	}

	@Override
	public void restoreTerminalSettings() {
		if (settings != null) {
			Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT.getHandle(), settings[0]);
			Wincon.INSTANCE.SetConsoleMode(CONSOLE_OUTPUT.getHandle(), settings[1]);
		}
	}

	@Override
	public void keyEchoEnabled(boolean enabled) {
		int mode = getConsoleInputMode();
		if (enabled) {
			mode |= Wincon.ENABLE_ECHO_INPUT;
		} else {
			mode &= ~Wincon.ENABLE_ECHO_INPUT;
		}
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT.getHandle(), mode);
	}

	@Override
	public void canonicalMode(boolean enabled) {
		int mode = getConsoleInputMode();
		if (enabled) {
			mode |= Wincon.ENABLE_LINE_INPUT;
		} else {
			mode &= ~Wincon.ENABLE_LINE_INPUT;
		}
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT.getHandle(), mode);
	}

	@Override
	public void keyStrokeSignalsEnabled(boolean enabled) {
		int mode = getConsoleInputMode();
		if (enabled) {
			mode |= Wincon.ENABLE_PROCESSED_INPUT;
		} else {
			mode &= ~Wincon.ENABLE_PROCESSED_INPUT;
		}
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT.getHandle(), mode);
	}

	@Override
	protected TerminalSize findTerminalSize() {
		CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new CONSOLE_SCREEN_BUFFER_INFO();
		Wincon.INSTANCE.GetConsoleScreenBufferInfo(CONSOLE_OUTPUT.getHandle(), screenBufferInfo);
		int columns = screenBufferInfo.srWindow.Right - screenBufferInfo.srWindow.Left + 1;
		int rows = screenBufferInfo.srWindow.Bottom - screenBufferInfo.srWindow.Top + 1;
		return new TerminalSize(columns, rows);
	}

	@Override
	public void registerTerminalResizeListener(Runnable runnable) {
		// ignore
	}

	public TerminalPosition getCursorPosition() {
		CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new CONSOLE_SCREEN_BUFFER_INFO();
		Wincon.INSTANCE.GetConsoleScreenBufferInfo(CONSOLE_OUTPUT.getHandle(), screenBufferInfo);
		int column = screenBufferInfo.dwCursorPosition.X - screenBufferInfo.srWindow.Left;
		int row = screenBufferInfo.dwCursorPosition.Y - screenBufferInfo.srWindow.Top;
		return new TerminalPosition(column, row);
	}

	private int getConsoleInputMode() {
		IntByReference lpMode = new IntByReference();
		Wincon.INSTANCE.GetConsoleMode(CONSOLE_INPUT.getHandle(), lpMode);
		return lpMode.getValue();
	}

	private int getConsoleOutputMode() {
		IntByReference lpMode = new IntByReference();
		Wincon.INSTANCE.GetConsoleMode(CONSOLE_OUTPUT.getHandle(), lpMode);
		return lpMode.getValue();
	}
}
