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
package com.googlecode.lanterna.common;

import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;

/**
 * This interface exposes functionality to 'draw' text graphics on a section of the terminal. It has several
 * implementation for the different levels, including one for Terminal, one for Screen and one which is used by the
 * TextGUI system to draw components. They are all very similar and has a lot of common functionality in
 * AbstractTextGraphics.
 * <p/>
 * The basic concept behind a TextGraphics implementation is that it keeps a state on four things:
 * <ul>
 *     <li>Position</li>
 *     <li>Foreground color</li>
 *     <li>Background color</li>
 *     <li>Modifiers</li>
 *     <li>Tab-expanding behaviour</li>
 * </ul>
 * These call all be altered through ordinary set* methods, but some will be altered as the result of performing one of
 * the 'drawing' operations. See the documentation to each method for further information (for example, putString).
 * <p/>
 * Don't hold on to your TextGraphics objects for too long; ideally create them and let them be GC:ed when you are done
 * with them. The reason is that not all implementations will handle the underlying terminal changing size.
 * @author Martin
 */
public interface TextGraphics {
    /**
     * Returns the size of the area that this text graphic can write to. Any attempts of placing characters outside of
     * this area will be silently ignored.
     * @return Size of the writable area that this TextGraphics can write too
     */
    TerminalSize getSize();

    /**
     * Creates a new TextGraphics of the same type as this one, using the same underlying subsystem. Using this method,
     * you need to specify a section of the current TextGraphics valid area that this new TextGraphic shall be
     * restricted to. If you call {@code newTextGraphics(TerminalPosition.TOP_LEFT_CORNER, textGraphics.getSize())} then
     * the resulting object will be identical to this one, but having a separated state for colors, position and
     * modifiers.
     * @param topLeftCorner Position of this TextGraphics's writable area that is to become the top-left corner (0x0) of
     *                      the new TextGraphics
     * @param size How large area, counted from the topLeftCorner, the new TextGraphics can write to. This cannot be
     *             larger than the current TextGraphics's writable area (adjusted by topLeftCorner)
     * @return A new TextGraphics with the same underlying subsystem, that can write to only the specified area
     * @throws java.lang.IllegalArgumentException If the size the of new TextGraphics exceeds the dimensions of this
     * TextGraphics in any way.
     */
    TextGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException;

    /**
     * Moves the position state of the TextGraphics to the specified coordinates
     * @param column Column index of the new position
     * @param row Row index of the new position
     * @return Itself
     */
    TextGraphics setPosition(int column, int row);

    /**
     * Moves the position state of the TextGraphics to the specified coordinates
     * @param newPosition New position
     * @return Itself
     */
    TextGraphics setPosition(TerminalPosition newPosition);

    /**
     * Moves the position state of this TextGraphics to a new position relative to the current position. Do not mix this
     * up with {@code setPosition(int column, int row)} that specifies the next position directly.
     * @param columns Number of columns to move to the right from the current position
     * @param rows Number of rows to move down from the current position
     * @return Itself
     */
    TextGraphics movePosition(int columns, int rows);

    /**
     * Returns the current position the TextGraphics has
     * @return Current position of the TextGraphics
     */
    TerminalPosition getPosition();

    /**
     * Returns the current background color
     * @return Current background color
     */
    TextColor getBackgroundColor();

    /**
     * Updates the current background color
     * @param backgroundColor New background color
     * @return Itself
     */
    TextGraphics setBackgroundColor(TextColor backgroundColor);

    /**
     * Returns the current foreground color
     * @return Current foreground color
     */
    TextColor getForegroundColor();

    /**
     * Updates the current foreground color
     * @param foregroundColor New foreground color
     * @return Itself
     */
    TextGraphics setForegroundColor(TextColor foregroundColor);

    /**
     * Adds zero or more modifiers to the set of currently active modifiers
     * @param modifiers Modifiers to add to the set of currently active modifiers
     * @return Itself
     */
    TextGraphics enableModifiers(Terminal.SGR... modifiers);

    /**
     * Removes zero or more modifiers from the set of currently active modifiers
     * @param modifiers Modifiers to remove from the set of currently active modifiers
     * @return Itself
     */
    TextGraphics disableModifiers(Terminal.SGR... modifiers);

    /**
     * Removes all active modifiers
     * @return Itself
     */
    TextGraphics clearModifiers();

    /**
     * Retrieves the current tab behaviour, which is what the TextGraphics will use when expanding \t characters to
     * spaces.
     * @return Current behaviour in use for expanding tab to spaces
     */
    TabBehaviour getTabBehaviour();

    /**
     * Sets the behaviour to use when expanding tab characters (\t) to spaces
     * @param tabBehaviour Behaviour to use when expanding tabs to spaces
     */
    void setTabBehaviour(TabBehaviour tabBehaviour);

    /**
     * Fills the entire writable area with a single character, using current foreground color, background color and modifiers.
     * @param c Character to fill the writable area with
     */
    void fillScreen(char c);

    /**
     * Draws a line from the current position to a specified position, using a supplied character. After
     * the operation, this {@code TextGraphics}'s position will be at the end-point of the line. The current
     * foreground color, background color and modifiers will be applied.
     * @param toPoint Where to draw the line
     * @param character Character to use for the line
     */
    void drawLine(TerminalPosition toPoint, char character);

    /**
     * Draws the outline of a triangle on the screen, using a supplied character. The triangle will begin at this
     * object's current position, go through both supplied points and then back again to the original position. The
     * position of this {@code TextGraphics} after this operation will be the same as where it was before.
     * The current foreground color, background color and modifiers will be applied.
     * @param p1 First point on the screen to draw the triangle with, starting from the current position
     * @param p2 Second point on the screen to draw the triangle with, going from p1 and going back to the original start
     * @param character What character to use when drawing the lines of the triangle
     */
    void drawTriangle(TerminalPosition p1, TerminalPosition p2, char character);

    /**
     * Draws a filled triangle, using a supplied character. The triangle will begin at this
     * object's current position, go through both supplied points and then back again to the original position. The
     * position of this {@code TextGraphics} after this operation will be the same as where it was before.
     * The current foreground color, background color and modifiers will be applied.
     * @param p1 First point on the screen to draw the triangle with, starting from the current position
     * @param p2 Second point on the screen to draw the triangle with, going from p1 and going back to the original start
     * @param character What character to use when drawing the lines of the triangle
     */
    void fillTriangle(TerminalPosition p1, TerminalPosition p2, char character);

    /**
     * Draws the outline of a rectangle with a particular character (and the currently active colors and
     * modifiers). The top-left corner will be at the current position of this {@code TextGraphics} (inclusive) and it
     * will extend to position + size (exclusive). The current position of this {@code TextGraphics} after this
     * operation will be the same as where it started.
     * <p/>
     * For example, calling drawRectangle with size being the size of the terminal when the writer's position is in the
     * top-left (0x0) corner will draw a border around the terminal.
     * <p/>
     * The current foreground color, background color and modifiers will be applied.
     * @param size Size (in columns and rows) of the area to draw
     * @param character What character to use when drawing the outline of the rectangle
     */
    void drawRectangle(TerminalSize size, char character);

    /**
     * Takes a rectangle and fills it with a particular character (and the currently active colors and
     * modifiers). The top-left corner will be at the current position of this {@code TextGraphics} (inclusive) and it
     * will extend to position + size (exclusive).
     * The current foreground color, background color and modifiers will be applied.
     * @param size Size (in columns and rows) of the area to draw
     * @param character What character to use when filling the rectangle
     */
    void fillRectangle(TerminalSize size, char character);

    /**
     * Puts a string on the screen at the current position with the current colors and modifiers. If the string
     * contains newlines (\r and/or \n), the method will stop at the character before that; you have to manage
     * multi-line strings yourself! The current position after this operation will be the old position shifted right
     * string.length() number of columns. The current foreground color, background color and modifiers will be applied.
     *
     * @param string Text to put on the screen
     * @return Itself
     */
    TextGraphics putString(String string);

    /**
     * Shortcut to calling:
     * <pre>
     *  putString(new TerminalPosition(column, row), string);
     * </pre>
     * @param column What column to put the string at
     * @param row What row to put the string at
     * @param string String to put on the screen
     * @return Itself
     */
    TextGraphics putString(int column, int row, String string);

    /**
     * Shortcut to calling:
     * <pre>
     *  setPosition(position);
     *  putString(string);
     * </pre>
     * @param position Position to put the string at
     * @param string String to put on the screen
     * @return Itself
     */
    TextGraphics putString(TerminalPosition position, String string);

    /**
     * Shortcut to calling:
     * <pre>
     *  putString(new TerminalPosition(column, row), string, modifiers, optionalExtraModifiers);
     * </pre>
     * @param column What column to put the string at
     * @param row What row to put the string at
     * @param string String to put on the screen
     * @param extraModifier Modifier to apply to the string
     * @param optionalExtraModifiers Optional extra modifiers to apply to the string
     * @return Itself
     */
    TextGraphics putString(int column, int row, String string, Terminal.SGR extraModifier, Terminal.SGR... optionalExtraModifiers);

    /**
     * Shortcut to calling:
     * <pre>
     *  setPosition(position);
     *  putString(string, modifiers, optionalExtraModifiers);
     * </pre>
     * @param position Position to put the string at
     * @param string String to put on the screen
     * @param extraModifier Modifier to apply to the string
     * @param optionalExtraModifiers Optional extra modifiers to apply to the string
     * @return Itself
     */
    TextGraphics putString(TerminalPosition position, String string, Terminal.SGR extraModifier, Terminal.SGR... optionalExtraModifiers);

    /**
     * Prints a string from the current position and optionally enables a few extra SGR modifiers. The extra SGR
     * modifiers, those that wasn't already active on the writer when this method is called, will only be applied to
     * this operation and will be disabled again when the call is done.
     * @param string String to put on the screen
     * @param extraModifier Extra modifier to apply to the string
     * @param optionalExtraModifiers Optional extra modifiers to apply to the string
     * @return Itself
     */
    TextGraphics putString(String string, Terminal.SGR extraModifier, Terminal.SGR... optionalExtraModifiers);
}
