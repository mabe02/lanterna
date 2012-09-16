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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.input.InputProvider;

/**
 * This is the main terminal interface, at the lowest level supported by Lanterna.
 * You can implement your own implementation of this if you want to target an
 * exotic text terminal specification or another graphical environment (like SWT).
 * @author Martin
 */
public interface Terminal extends InputProvider
{
    /**
     * Calling this method will, where supported, give your terminal a private
     * area to use, separate from what was there before. Some terminal emulators
     * will preserve the terminal history and restore it when you exit private 
     * mode. 
     * @throws LanternaException 
     */
    public void enterPrivateMode();
    
    /**
     * If you have previously entered private mode, this method will exit this
     * and, depending on implementation, maybe restore what the terminal looked
     * like before private mode was entered.
     * @throws LanternaException 
     */    
    public void exitPrivateMode();
    
    /**
     * Removes all the characters, colors and graphics from the screep and leaves
     * you with a big empty space. Text cursor position is undefined after this 
     * call (depends on platform and terminal) so you should always call 
     * {@code moveCursor} next.
     * @throws LanternaException 
     */    
    public void clearScreen();
    
    /**
     * Moves the text cursor to a new location
     * @param x The 0-indexed column to place the cursor at
     * @param y The 0-indexed row to place the cursor at
     * @throws LanternaException 
     */    
    public void moveCursor(int x, int y);
    
    /**
     * Hides or shows the text cursor
     * @param visible Hides the text cursor if {@code false} and shows it if {@code true}
     */
    public void setCursorVisible(boolean visible);
    
    /**
     * Prints one character to the terminal at the current cursor location. Please
     * note that the cursor will then move one column to the right but if reached
     * the end of the line may move to the beginning of the next line.
     * @param c
     * @throws LanternaException 
     */
    public void putCharacter(char c);
    
    /**
     * Activates an {@code SGR} code for all the following characters put to the 
     * terminal.
     * @param options List of SGR codes
     * @throws LanternaException 
     * @see Terminal.SGR
     */
    public void applySGR(SGR... options);
    
    /**
     * Changes the foreground color for all the following characters put to the 
     * terminal. The foreground color is what color to draw the text in.
     * @param color Color to use for foreground
     * @throws LanternaException 
     */    
    public void applyForegroundColor(Color color);
    
    /**
     * Changes the foreground color for all the following characters put to the
     * terminal. The foreground color is what color to draw the text in.<br>
     * <b>Warning:</b> Only a few terminal support 24-bit color control codes, 
     * please avoid using this unless you know all users will have compatible
     * terminals. For details, please see 
     * <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit log.
     * 
     * @param r Red intensity, from 0 to 255
     * @param g Green intensity, from 0 to 255
     * @param b Blue intensity, from 0 to 255
     */
    public void applyForegroundColor(int r, int g, int b);
    
    /**
     * Changes the foreground color for all the following characters put to the
     * terminal. The foreground color is what color to draw the text in.<br>
     * <b>Warning:</b> This method will use the XTerm 256 color extension, it 
     * may not be supported on all terminal emulators! The index values are 
     * resolved as this:<br>
     *   0 ..  15 - System color, these are taken from the schema.
     *  16 .. 231 - Forms a 6x6x6 RGB color cube.<br>
     * 232 .. 255 - A gray scale ramp without black and white.<br>
     * 
     * <p>For more details on this, please see <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit message to Konsole.
     * @param index Color index from the XTerm 256 color space
     */
    public void applyForegroundColor(int index);
    
    /**
     * Changes the background color for all the following characters put to the 
     * terminal. The background color is the color surrounding the text being 
     * printed.
     * @param color Color to use for the background
     * @throws LanternaException 
     */    
    public void applyBackgroundColor(Color color);
    
    /**
     * Changes the background color for all the following characters put to the
     * terminal. The background color is the color surrounding the text being 
     * printed.<br>
     * <b>Warning:</b> Only a few terminal support 24-bit color control codes, 
     * please avoid using this unless you know all users will have compatible
     * terminals. For details, please see 
     * <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit log.
     * 
     * @param r Red intensity, from 0 to 255
     * @param g Green intensity, from 0 to 255
     * @param b Blue intensity, from 0 to 255
     */
    public void applyBackgroundColor(int r, int g, int b);
    
    /**
     * Changes the background color for all the following characters put to the
     * terminal. The background color is the color surrounding the text being 
     * printed.<br>
     * <b>Warning:</b> This method will use the XTerm 256 color extension, it 
     * may not be supported on all terminal emulators! The index values are 
     * resolved as this:<br>
     *   0 ..  15 - System color, these are taken from the schema.
     *  16 .. 231 - Forms a 6x6x6 RGB color cube.<br>
     * 232 .. 255 - A gray scale ramp without black and white.<br>
     * 
     * <p>For more details on this, please see <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit message to Konsole.
     * @param index Index of the color to use, from the XTerm 256 color extension
     */
    public void applyBackgroundColor(int index);
    
    /**
     * Adds a {@code ResizeListener} to be called when the terminal has changed
     * size. 
     * @see ResizeListener
     * @param listener Listener object to be called when the terminal has been changed
     */
    public void addResizeListener(ResizeListener listener);
    
    /**
     * Removes a {@code ResizeListener} from the list of listeners to be notified
     * when the terminal has changed size
     * @see ResizeListener
     * @param listener Listener object to remove
     */
    public void removeResizeListener(ResizeListener listener);
    
    /**
     * Will ask the terminal of its current size dimensions, represented by a 
     * {@code TerminalSize} object. Please note that the default way of figuring 
     * this information out is asynchorous and so you will be given the last
     * known dimensions. With proper resize listeners set up, this will only be
     * a problem for figuring out the initial size of the terminal.
     * @return a {@code TerminalSize} object representing the size of the terminal
     * @throws LanternaException 
     * @see TerminalSize
     * @deprecated Being deprecated since 2.0.1 in favor of getTerminalSize()
     */
    @Deprecated
    public TerminalSize queryTerminalSize();
    
    /**
     * Returns the size of the terminal, expressed as a {@code TerminalSize}
     * object. Please bear in mind that depending on the {@code Terminal}
     * implementation, this may or may not be accurate. See the implementing
     * classes for more information.
     * @return Size of the terminal
     */
    public TerminalSize getTerminalSize();
    
    /**
     * Calls {@code flush()} on the underlying {@code OutputStream} object, or
     * whatever other implementation this terminal is built around. 
     * @throws LanternaException 
     */
    public void flush();

    /**
     * SGR - Select Graphic Rendition, changes the state of the terminal as to
     * what kind of text to print after this command. When applying an ENTER SGR 
     * code, it normally applies until you send the corresponding EXIT code. 
     * RESET_ALL will clear any code currently enabled.
     */
    public enum SGR
    {
        /**
         * Removes any code SGR code currently enabled
         */
        RESET_ALL,
        
        /**
         * Please note that on some terminal implementations, instead of making
         * the text bold, it will draw the text in a slightly different color
         */
        ENTER_BOLD,
        ENTER_REVERSE,
        ENTER_UNDERLINE,
        /**
         * This code may not be supported by all terminals/terminal emulators
         */
        ENTER_BLINK,
        EXIT_BOLD,
        EXIT_REVERSE,
        EXIT_UNDERLINE,
        EXIT_BLINK
    }

    public enum Color
    {
        BLACK(0),
        RED(1),
        GREEN(2),
        YELLOW(3),
        BLUE(4),
        MAGENTA(5),
        CYAN(6),
        WHITE(7),
        DEFAULT(9);

        private int index;

        private Color(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    /**
     * Listener interface that can be used to be alerted on terminal resizing
     */
    public interface ResizeListener
    {
        public void onResized(TerminalSize newSize);
    }
}
