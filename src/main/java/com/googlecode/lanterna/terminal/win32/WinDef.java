package com.googlecode.lanterna.terminal.win32;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;

public interface WinDef extends com.sun.jna.platform.win32.WinDef {

	/**
	 * COORD structure
	 */
	@FieldOrder({ "X", "Y" })
	class COORD extends Structure {

		public short X;
		public short Y;

		@Override
		public String toString() {
			return String.format("COORD(%s,%s)", X, Y);
		}
	}

	/**
	 * SMALL_RECT structure
	 */
	@FieldOrder({ "Left", "Top", "Right", "Bottom" })
	class SMALL_RECT extends Structure {

		public short Left;
		public short Top;
		public short Right;
		public short Bottom;

		@Override
		public String toString() {
			return String.format("SMALL_RECT(%s,%s)(%s,%s)", Left, Top, Right, Bottom);
		}
	}

	/**
	 * CONSOLE_SCREEN_BUFFER_INFO structure
	 */
	@FieldOrder({ "dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize" })
	class CONSOLE_SCREEN_BUFFER_INFO extends Structure {

		public COORD dwSize;
		public COORD dwCursorPosition;
		public short wAttributes;
		public SMALL_RECT srWindow;
		public COORD dwMaximumWindowSize;

		@Override
		public String toString() {
			return String.format("CONSOLE_SCREEN_BUFFER_INFO(%s,%s,%s,%s,%s)", dwSize, dwCursorPosition, wAttributes, srWindow, dwMaximumWindowSize);
		}
	}

	@FieldOrder({ "EventType", "Event" })
	class INPUT_RECORD extends Structure {

		public static final short KEY_EVENT = 0x01;
		public static final short MOUSE_EVENT = 0x02;
		public static final short WINDOW_BUFFER_SIZE_EVENT = 0x04;

		public short EventType;
		public Event Event;

		public static class Event extends Union {
			public KEY_EVENT_RECORD KeyEvent;
			public MOUSE_EVENT_RECORD MouseEvent;
			public WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;
		}

		@Override
		public void read() {
			super.read();
			switch (EventType) {
			case KEY_EVENT:
				Event.setType("KeyEvent");
				break;
			case MOUSE_EVENT:
				Event.setType("MouseEvent");
				break;
			case WINDOW_BUFFER_SIZE_EVENT:
				Event.setType("WindowBufferSizeEvent");
				break;
			}
			Event.read();
		}

		@Override
		public String toString() {
			return String.format("INPUT_RECORD(%s)", EventType);
		}
	}

	@FieldOrder({ "bKeyDown", "wRepeatCount", "wVirtualKeyCode", "wVirtualScanCode", "uChar", "dwControlKeyState" })
	class KEY_EVENT_RECORD extends Structure {

		public boolean bKeyDown;
		public short wRepeatCount;
		public short wVirtualKeyCode;
		public short wVirtualScanCode;
		public char uChar;
		public int dwControlKeyState;

		@Override
		public String toString() {
			return String.format("KEY_EVENT_RECORD(%s,%s,%s,%s,%s,%s)", bKeyDown, wRepeatCount, wVirtualKeyCode, wVirtualKeyCode, wVirtualScanCode, uChar, dwControlKeyState);
		}
	}

	@FieldOrder({ "dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags" })
	class MOUSE_EVENT_RECORD extends Structure {

		public COORD dwMousePosition;
		public int dwButtonState;
		public int dwControlKeyState;
		public int dwEventFlags;

		@Override
		public String toString() {
			return String.format("MOUSE_EVENT_RECORD(%s,%s,%s,%s)", dwMousePosition, dwButtonState, dwControlKeyState, dwEventFlags);
		}
	}

	@FieldOrder({ "dwSize" })
	class WINDOW_BUFFER_SIZE_RECORD extends Structure {

		public COORD dwSize;

		@Override
		public String toString() {
			return String.format("WINDOW_BUFFER_SIZE_RECORD(%s)", dwSize);
		}
	}

}
