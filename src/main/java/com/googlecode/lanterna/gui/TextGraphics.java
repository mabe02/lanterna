/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.gui.Theme.Definition;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public interface TextGraphics {

    /**
     * Applies theme-specific settings according to the category supplied. This
     * may modify the foreground color, background color and/or styles. Any
     * string drawn after this call will have these settings applied
     * @param category Category to use
     */
    void applyTheme(Theme.Category category);

    /**
     * Applies theme-specific settings according to the definition supplied. This
     * may modify the foreground color, background color and/or styles. Any
     * string drawn after this call will have these settings applied
     * @param themeItem Definition to use
     */
    void applyTheme(Theme.Definition themeItem);

    /**
     * Draws a string to the terminal, with the first character starting at
     * specified coordinates and which an option list of styles applied. All
     * coordinates are local to the top-left corner of the TextGraphics object
     * @param column Column of the first character in the string
     * @param row Row of the first character in the string
     * @param string String to print to terminal
     * @param styles Which styles to apply to the string
     */
    void drawString(int column, int row, String string, ScreenCharacterStyle... styles);

    /**
     * Replaces the content of the entire TextGraphic object with one character
     * @param character Character to fill the area with
     */
    void fillArea(char character);

    /**
     * Replaces the content of a rectangle within the TextGraphic drawing area
     * with a specified character
     * @param character Character to fill the area with
     */
    void fillRectangle(char character, TerminalPosition topLeft, TerminalSize rectangleSize);

    Color getBackgroundColor();

    Color getForegroundColor();

    /**
     * Height, in rows, of the TextGraphics drawing area. Attemps to draw
     * outside of this will be ignored
     * @return Size of the TextGraphics area, in rows
     */
    int getHeight();

    /**
     * Size of the area the {@code TextGraphics} can edit, as a {@code TerminalSize} object,
     * any attempts to draw outside of this area will be ignored.
     * @return Size of the area the {@code TextGraphics} can edit, as a {@code TerminalSize} object
     */
    TerminalSize getSize();

    Theme getTheme();

    /**
     * Width, in columns, of the TextGraphics drawing area. Attemps to draw
     * outside of this will be ignored
     * @return Size of the TextGraphics area, in columns
     */
    int getWidth();

    void setBackgroundColor(Color backgroundColor);

    void setBoldMask(boolean enabledBoldMask);

    void setForegroundColor(Color foregroundColor);

    /**
     * Creates a new TextGraphics object using the same area or smaller. Use the
     * terminalPosition variable to determine what the new TextGraphics object
     * will cover.
     * @param terminalPosition In local coordinates of the current TextGraphics,
     * the left-left coordinates of the new TextGraphics
     * @return A new TextGraphics object covering the same or smaller area as
     * this
     */
    TextGraphics subAreaGraphics(final TerminalPosition terminalPosition);

    /**
     * Creates a new TextGraphics object using the same area or smaller. Use the
     * topLeft and subAreaSize variable to determine what the new TextGraphics
     * object will cover.
     * @param topLeft  In local coordinates of the current TextGraphics,
     * the left-left coordinates of the new TextGraphics
     * @param subAreaSize Size of the area the new TextGraphics will cover
     * @return A new TextGraphics object covering the same or smaller area as
     * this
     */
    TextGraphics subAreaGraphics(final TerminalPosition topLeft, final TerminalSize subAreaSize);

    /**
     * Translates local coordinates of this TextGraphics object to global
     * @param pointInArea Point in local coordinates
     * @return The point in global coordinates
     */
    TerminalPosition translateToGlobalCoordinates(TerminalPosition pointInArea);
}
