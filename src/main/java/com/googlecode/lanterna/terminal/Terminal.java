/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.InputProvider;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * This is the main terminal interface, at the lowest level supported by Lanterna. You can write your own
 * implementation of this if you want to target an exotic text terminal specification or another graphical environment
 * (like SWT), but you should probably extend {@code AbstractTerminal} instead of implementing this interface directly.
 * <p>
 * The normal way you interact in Java with a terminal is through the standard output (System.out) and standard error
 * (System.err) and it's usually through printing text only. This interface abstracts a terminal at a more fundamental
 * level, expressing methods for not only printing text but also changing colors, moving the cursor new positions,
 * enable special modifiers and get notified when the terminal's size has changed.
 * <p>
 * If you want to write an application that has a very precise control of the terminal, this is the
 * interface you should be programming against.
 *
 * @author Martin
 */
public interface Terminal extends InputProvider, Closeable {

    /**
     * Calling this method will, where supported, give your terminal a private area to use, separate from what was there
     * before. Some terminal emulators will preserve the terminal history and restore it when you exit private mode.
     * Some terminals will just clear the screen and put the cursor in the top-left corner. Typically, if you terminal
     * supports scrolling, going into private mode will disable the scrolling and leave you with a fixed screen, which
     * can be useful if you don't want to deal with what the terminal buffer will look like if the user scrolls up.
     *
     * @throws java.io.IOException If there was an underlying I/O error
     * @throws IllegalStateException If you are already in private mode
     */
    void enterPrivateMode() throws IOException;

    /**
     * If you have previously entered private mode, this method will exit this and, depending on implementation, maybe
     * restore what the terminal looked like before private mode was entered. If the terminal doesn't support a
     * secondary buffer for private mode, it will probably make a new line below the private mode and place the cursor
     * there.
     *
     * @throws java.io.IOException If there was an underlying I/O error
     * @throws IllegalStateException If you are not in private mode
     */
    void exitPrivateMode() throws IOException;

    /**
     * Removes all the characters, colors and graphics from the screen and leaves you with a big empty space. Text
     * cursor position is undefined after this call (depends on platform and terminal) so you should always call
     * {@code moveCursor} next. Some terminal implementations doesn't reset color and modifier state so it's also good
     * practise to call {@code resetColorAndSGR()} after this.
     * @throws java.io.IOException If there was an underlying I/O error
     */
    void clearScreen() throws IOException;

    /**
     * Moves the text cursor to a new location on the terminal. The top-left corner has coordinates 0 x 0 and the bottom-
     * right corner has coordinates terminal_width-1 x terminal_height-1. You can retrieve the size of the terminal by
     * calling getTerminalSize().
     *
     * @param x The 0-indexed column to place the cursor at
     * @param y The 0-indexed row to place the cursor at
     * @throws java.io.IOException If there was an underlying I/O error
     */
    void setCursorPosition(int x, int y) throws IOException;

    /**
     * Same as calling {@code setCursorPosition(position.getColumn(), position.getRow())}
     *
     * @param position Position to place the cursor at
     * @throws java.io.IOException If there was an underlying I/O error
     */
    void setCursorPosition(TerminalPosition position) throws IOException;

    /**
     * Returns the position of the cursor, as reported by the terminal. The top-left corner has coordinates 0 x 0 and
     * the bottom-right corner has coordinates terminal_width-1 x terminal_height-1.
     * @return Position of the cursor
     * @throws IOException In there was an underlying I/O error
     */
    TerminalPosition getCursorPosition() throws IOException;

    /**
     * Hides or shows the text cursor, but not all terminal (-emulators) supports this. The text cursor is normally a
     * text block or an underscore, sometimes blinking, which shows the user where keyboard-entered text is supposed to
     * show up.
     *
     * @param visible Hides the text cursor if {@code false} and shows it if {@code true}
     * @throws java.io.IOException If there was an underlying I/O error
     */
    void setCursorVisible(boolean visible) throws IOException;

    /**
     * Prints one character to the terminal at the current cursor location. Please note that the cursor will then move
     * one column to the right, so multiple calls to {@code putCharacter} will print out a text string without the need
     * to reposition the text cursor. If you reach the end of the line while putting characters using this method, you
     * can expect the text cursor to move to the beginning of the next line.
     * <p>
     * You can output CJK (Chinese, Japanese, Korean) characters (as well as other regional scripts) but remember that
     * the terminal that the user is using might not have the required font to render it. Also worth noticing is that
     * CJK (and some others) characters tend to take up 2 columns per character, simply because they are a square in
     * their construction as opposed to the somewhat rectangular shape we fit latin characters in. As it's very
     * difficult to create a monospace font for CJK with a 2:1 height-width proportion, it seems like the implementers
     * back in the days simply gave up and made each character take 2 column. It causes issues for the random terminal
     * programmer because you can't really trust 1 character = 1 column, but I suppose it's "しょうがない".
     *
     * If you try to print non-printable control characters, the terminal is likely to ignore them (all {@link Terminal}
     * implementations bundled with Lanterna will).
     *
     * @param c Character to place on the terminal
     * @throws java.io.IOException If there was an underlying I/O error
     */
    void putCharacter(char c) throws IOException;

    /**
     * Creates a new TextGraphics object that uses this Terminal directly when outputting. Keep in mind that you are
     * probably better off to switch to a Screen to make advanced text graphics more efficient. Also, this TextGraphics
     * implementation will not call {@code .flush()} after any operation, so you'll need to do that on your own.
     * @return TextGraphics implementation that draws directly using this Terminal interface
     * @throws IOException If there was an I/O error when setting up the {@link TextGraphics} object
     */
    TextGraphics newTextGraphics() throws IOException;

    /**
     * Activates an {@code SGR} (Selected Graphic Rendition) code. This code modifies a state inside the terminal
     * that will apply to all characters written afterwards, such as bold, italic, blinking code and so on.
     *
     * @param sgr SGR code to apply
     * @throws java.io.IOException If there was an underlying I/O error
     * @see SGR
     * @see <a href="http://www.vt100.net/docs/vt510-rm/SGR">http://www.vt100.net/docs/vt510-rm/SGR</a>
     */
    void enableSGR(SGR sgr) throws IOException;

    /**
     * Deactivates an {@code SGR} (Selected Graphic Rendition) code which has previously been activated through {@code
     * enableSGR(..)}.
     *
     * @param sgr SGR code to apply
     * @throws java.io.IOException If there was an underlying I/O error
     * @see SGR
     * @see <a href="http://www.vt100.net/docs/vt510-rm/SGR">http://www.vt100.net/docs/vt510-rm/SGR</a>
     */
    void disableSGR(SGR sgr) throws IOException;

    /**
     * Removes all currently active SGR codes and sets foreground and background colors back to default.
     *
     * @throws java.io.IOException If there was an underlying I/O error
     * @see SGR
     * @see <a href="http://www.vt100.net/docs/vt510-rm/SGR">http://www.vt100.net/docs/vt510-rm/SGR</a>
     */
    void resetColorAndSGR() throws IOException;

    /**
     * Changes the foreground color for all the following characters put to the terminal. The foreground color is what
     * color to draw the text in, as opposed to the background color which is the color surrounding the characters.
     * <p>
     * This overload is using the TextColor class to define a color, which is a layer of abstraction above the three
     * different color formats supported (ANSI, indexed and RGB). The other setForegroundColor(..) overloads gives
     * you direct access to set one of those three.
     * <p>
     * Note to implementers of this interface, just make this method call <b>color.applyAsForeground(this);</b>
     *
     * @param color Color to use for foreground
     * @throws java.io.IOException If there was an underlying I/O error
     */
    void setForegroundColor(TextColor color) throws IOException;

    /**
     * Changes the background color for all the following characters put to the terminal. The background color is the
     * color surrounding the text being printed.
     * <p>
     * This overload is using the TextColor class to define a color, which is a layer of abstraction above the three
     * different color formats supported (ANSI, indexed and RGB). The other setBackgroundColor(..) overloads gives
     * you direct access to set one of those three.
     * <p>
     * Note to implementers of this interface, just make this method call <b>color.applyAsBackground(this);</b>
     *
     * @param color Color to use for the background
     * @throws java.io.IOException If there was an underlying I/O error
     */
    void setBackgroundColor(TextColor color) throws IOException;

    /**
     * Adds a {@link TerminalResizeListener} to be called when the terminal has changed size. There is no guarantee that
     * this listener will really be invoked when the terminal has changed size, at all depends on the terminal emulator
     * implementation. Normally on Unix systems the WINCH signal will be sent to the process and lanterna can intercept
     * this.
     * <p>
     * There are no guarantees on what thread the call will be made on, so please be careful with what kind of operation
     * you perform in this callback. You should probably not take too long to return.
     *
     * @see TerminalResizeListener
     * @param listener Listener object to be called when the terminal has been changed
     */
    void addResizeListener(TerminalResizeListener listener);

    /**
     * Removes a {@link TerminalResizeListener} from the list of listeners to be notified when the terminal has changed
     * size
     *
     * @see TerminalResizeListener
     * @param listener Listener object to remove
     */
    void removeResizeListener(TerminalResizeListener listener);

    /**
     * Returns the size of the terminal, expressed as a {@code TerminalSize} object. Please bear in mind that depending
     * on the {@code Terminal} implementation, this may or may not be accurate. See the implementing classes for more
     * information. Most commonly, calling getTerminalSize() will involve some kind of hack to retrieve the size of the
     * terminal, like moving the cursor to position 5000x5000 and then read back the location, unless the terminal
     * implementation has a more smooth way of getting this data. Keep this in mind and see if you can avoid calling
     * this method too often. There is a helper class, SimpleTerminalResizeListener, that you can use to cache the size
     * and update it only when resize events are received (which depends on if a resize is detectable, which they are not
     * on all platforms).
     *
     * @return Size of the terminal
     * @throws java.io.IOException if there was an I/O error trying to retrieve the size of the terminal
     */
    TerminalSize getTerminalSize() throws IOException;

    /**
     * Retrieves optional information from the terminal by printing the ENQ ({@literal \}u005) character. Terminals and terminal
     * emulators may or may not respond to this command, sometimes it's configurable.
     *
     * @param timeout How long to wait for the talk-back message, if there's nothing immediately available on the input
     * stream, you should probably set this to a somewhat small value to prevent unnecessary blockage on the input stream
     * but large enough to accommodate a round-trip to the user's terminal (~300 ms if you are connection across the globe).
     * @param timeoutUnit What unit to use when interpreting the {@code timeout} parameter
     * @return Answer-back message from the terminal or empty if there was nothing
     * @throws java.io.IOException If there was an I/O error while trying to read the enquiry reply
     */
    byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) throws IOException;

    /**
     * Prints 0x7 to the terminal, which will make the terminal (emulator) ring a bell (or more likely beep). Not all
     * terminals implements this. <a href="https://en.wikipedia.org/wiki/Bell_character">Wikipedia</a> has more details.
     * @throws IOException If there was an underlying I/O error
     */
    void bell() throws IOException;

    /**
     * Calls {@code flush()} on the underlying {@code OutputStream} object, or whatever other implementation this
     * terminal is built around. Some implementing classes of this interface (like SwingTerminal) doesn't do anything
     * as it doesn't really apply to them.
     * @throws java.io.IOException If there was an underlying I/O error
     */
    void flush() throws IOException;

    /**
     * Closes the terminal, if applicable. If the implementation doesn't support closing the terminal, this will do
     * nothing. The Swing/AWT emulator implementations will translate this into a dispose() call on the UI resources,
     * the telnet implementation will hang out the connection.
     * @throws IOException If there was an underlying I/O error
     */
    void close() throws IOException;
}
