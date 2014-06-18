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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.input.InputProvider;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * This is the main terminal interface, at the lowest level supported by Lanterna. You can implement your own
 * implementation of this if you want to target an exotic text terminal specification or another graphical environment
 * (like SWT). If you want to write an application that has a very precise control of the terminal, this is the
 * interface you should be programming against.
 *
 * @author Martin
 */
public interface Terminal extends InputProvider {

    /**
     * Calling this method will, where supported, give your terminal a private area to use, separate from what was there
     * before. Some terminal emulators will preserve the terminal history and restore it when you exit private mode.
     * Some terminals will just clear the screen and put the cursor in the top-left corner. Typically, if you terminal
     * supports scrolling, going into private mode will disable the scrolling and leave you with a fixed screen, which
     * can be useful if you don't want to deal with what the terminal buffer will look like if the user scrolls up.
     *
     * @throws IllegalStateException If you are already in private mode
     */
    public void enterPrivateMode() throws IOException;

    /**
     * If you have previously entered private mode, this method will exit this and, depending on implementation, maybe
     * restore what the terminal looked like before private mode was entered. If the terminal doesn't support a
     * secondary buffer for private mode, it will probably make a new line below the private mode and place the cursor
     * there.
     *
     * @throws IllegalStateException If you are not in private mode
     */
    public void exitPrivateMode() throws IOException;

    /**
     * Removes all the characters, colors and graphics from the screen and leaves you with a big empty space. Text
     * cursor position is undefined after this call (depends on platform and terminal) so you should always call
     * {@code moveCursor} next.
     */
    public void clearScreen() throws IOException;

    /**
     * Moves the text cursor to a new location on the terminal. The top-left corner has coordinates 0 x 0 and the bottom-
     * right corner has coordinates terminal_width-1 x terminal_height-1. You can retrieve the size of the terminal by
     * calling getTerminalSize().
     *
     * @param x The 0-indexed column to place the cursor at
     * @param y The 0-indexed row to place the cursor at
     */
    public void moveCursor(int x, int y) throws IOException;

    /**
     * Hides or shows the text cursor, but not all terminal (-emulators) supports this. The text cursor is normally a
     * text block or an underscore, sometimes blinking, which shows the user where keyboard-entered text is supposed to
     * show up.
     *
     * @param visible Hides the text cursor if {@code false} and shows it if {@code true}
     */
    public void setCursorVisible(boolean visible) throws IOException;

    /**
     * Prints one character to the terminal at the current cursor location. Please note that the cursor will then move
     * one column to the right, so multiple calls to {@code putCharacter} will print out a text string without the need
     * to reposition the text cursor. If you reach the end of the line while putting characters using this method, you
     * can expect the text cursor to move to the beginning of the next line.
     * </p>
     * You can output CJK (Chinese, Japanese, Korean) characters (as well as other regional scripts) but remember that
     * the terminal that the user is using might not have the required font to render it. Also worth noticing is that
     * CJK (and some others) characters tend to take up 2 columns per character, simply because they are a square in
     * their construction as opposed to the somewhat rectangular shape we fit latin characters in. As it's very
     * difficult to create a monospace font for CJK with a 2:1 height-width proportion, it seems like the implementers
     * back in the days simply gave up and made each character take 2 column. It causes issues for the random terminal
     * programmer because you can't really trust 1 character = 1 column, but I suppose it's "しょうがない".
     *
     * @param c Character to place on the terminal
     */
    public void putCharacter(char c) throws IOException;

    /**
     * Activates an {@code SGR} (Selected Graphic Rendition) code. This code modifies a state inside the terminal
     * that will apply to all characters written afterwards, such as bold, italic, blinking code and so on.
     *
     * @param sgr SGR code to apply
     * @see Terminal.SGR
     * @see http://www.vt100.net/docs/vt510-rm/SGR
     */
    public void enableSGR(SGR sgr) throws IOException;

    /**
     * Deactivates an {@code SGR} (Selected Graphic Rendition) code which has previously been activated through {@code
     * enableSGR(..)}.
     *
     * @param sgr SGR code to apply
     * @see Terminal.SGR
     * @see http://www.vt100.net/docs/vt510-rm/SGR
     */
    public void disableSGR(SGR sgr) throws IOException;

    /**
     * Removes all currently active SGR codes.
     *
     * @see Terminal.SGR
     * @see http://www.vt100.net/docs/vt510-rm/SGR
     */
    public void resetAllSGR() throws IOException;

    /**
     * Changes the foreground color for all the following characters put to the terminal. The foreground color is what
     * color to draw the text in, as opposed to the background color which is the color surrounding the characters.
     * </p>
     * This overload is using the TextColor class to define a color, which is a layer of abstraction above the three
     * different color formats supported (ANSI, indexed and RGB). The other setForegroundColor(..) overloads gives
     * you direct access to set one of those three.
     * </p>
     * Note to implementers of this interface, just make this method call <b>color.applyAsForeground(this);</b>
     *
     * @param color Color to use for foreground
     */
    public void setForegroundColor(TextColor color) throws IOException;

    /**
     * Changes the background color for all the following characters put to the terminal. The background color is the
     * color surrounding the text being printed.
     * </p>
     * This overload is using the TextColor class to define a color, which is a layer of abstraction above the three
     * different color formats supported (ANSI, indexed and RGB). The other setBackgroundColor(..) overloads gives
     * you direct access to set one of those three.
     * </p>
     * Note to implementers of this interface, just make this method call <b>color.applyAsBackground(this);</b>
     *
     * @param color Color to use for the background
     */
    public void setBackgroundColor(TextColor color) throws IOException;

    /**
     * Adds a {@code ResizeListener} to be called when the terminal has changed size. There is no guarantee that this
     * listener will really be invoked when the terminal has changed size, at all depends on the terminal emulator
     * implementation. Normally on Unix systems the WINCH signal will be sent to the process and lanterna can intercept
     * this.
     * </p>
     * There are no guarantees on what thread the call will be made on, so please be careful with what kind of operation
     * you perform in this callback. You should probably not take too long to return.
     *
     * @see ResizeListener
     * @param listener Listener object to be called when the terminal has been changed
     */
    public void addResizeListener(ResizeListener listener);

    /**
     * Removes a {@code ResizeListener} from the list of listeners to be notified when the terminal has changed size
     *
     * @see ResizeListener
     * @param listener Listener object to remove
     */
    public void removeResizeListener(ResizeListener listener);

    /**
     * Returns the size of the terminal, expressed as a {@code TerminalSize} object. Please bear in mind that depending
     * on the {@code Terminal} implementation, this may or may not be accurate. See the implementing classes for more
     * information. Most commonly, calling getTerminalSize() will involve some kind of hack to retrieve the size of the
     * terminal, like moving the cursor to position 5000x5000 and then read back the location, unless the terminal
     * implementation has a more smooth way of getting this data. Keep this in mind and see if you can avoid calling
     * this method too often. There is a helper class, SimpleTerminalResizeListener, that you can use to cache the size
     * and update it only when resize events are received (which depends on resizes being detectable, which they are not
     * on all platforms).
     *
     * @return Size of the terminal
     * @throws java.io.IOException if there was an I/O error trying to retrieve the size of the terminal
     */
    public TerminalSize getTerminalSize() throws IOException;

    /**
     * Retrieves optional information from the terminal by printing the ENQ ({@literal \}u005) character. Terminals and terminal
     * emulators may or may not respond to this command, sometimes it's configurable.
     * @param timeout How long to wait for the talkback message, if there's nothing immediately available on the input
     * stream, you should probably set this to a somewhat small value to prevent unnecessary blockage on the input stream
     * but large enough to accommodate a round-trip to the user's terminal (~300 ms if you are connection across the globe).
     * @param timeoutUnit What unit to use when interpreting the {@code timeout} parameter
     * @return Answerback message from the terminal or empty if there was nothing
     * @throws java.io.IOException If there was an I/O error while trying to read the enquiry reply
     */
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) throws IOException;

    /**
     * Calls {@code flush()} on the underlying {@code OutputStream} object, or whatever other implementation this
     * terminal is built around. Some implementing classes of this interface (like SwingTerminal) doesn't do anything
     * as it doesn't really apply to them.
     */
    public void flush() throws IOException;

    /**
     * SGR - Select Graphic Rendition, changes the state of the terminal as to what kind of text to print after this
     * command. When applying an ENTER SGR code, it normally applies until you send the corresponding EXIT code.
     * RESET_ALL will clear any code currently enabled.
     */
    public enum SGR {
        /**
         * Please note that on some terminal implementations, instead of making the text bold, it will draw the text in
         * a slightly different color
         */
        BOLD,

        /**
         * Reverse mode will flip the foreground and background colors
         */
        REVERSE,

        /**
         * Not widely supported
         */
        UNDERLINE,

        /**
         * Not widely supported
         */
        BLINK,

        /**
         * Rarely supported
         */
        BORDERED,

        /**
         * Exotic extension, please send me a reference screenshot!
         */
        FRAKTUR,

        /**
         * Rarely supported
         */
        CROSSEDOUT,

        /**
         * Rarely supported
         */
        CIRCLED,
        ;
    }
}
