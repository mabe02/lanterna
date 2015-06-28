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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TextColor;

/**
 * Object that encapsulates the configuration parameters for the terminal 'device' that a SwingTerminal is emulating.
 * This includes properties such as the shape of the cursor, the color of the cursor, how large scrollback is available
 * and if the cursor should blink or not.
 * @author martin
 */
public class SwingTerminalDeviceConfiguration {

    /**
     * This is a static reference to the default terminal device configuration. Use this one if you are unsure.
     */
    public static final SwingTerminalDeviceConfiguration DEFAULT = new SwingTerminalDeviceConfiguration();

    private final int lineBufferScrollbackSize;
    private final int blinkLengthInMilliSeconds;
    private final CursorStyle cursorStyle;
    private final TextColor cursorColor;
    private final boolean cursorBlinking;

    /**
     * Creates a new terminal device configuration object with all the defaults set
     */
    @SuppressWarnings("WeakerAccess")
    public SwingTerminalDeviceConfiguration() {
        this(2000, 500, CursorStyle.REVERSED, new TextColor.RGB(255, 255, 255), false);
    }

    /**
     * Creates a new terminal device configuration object with all configurable values specified.
     * @param lineBufferScrollbackSize How many lines of scrollback buffer should the terminal save?
     * @param blinkLengthInMilliSeconds How many milliseconds does a 'blink' last
     * @param cursorStyle Style of the terminal text cursor
     * @param cursorColor Color of the terminal text cursor
     * @param cursorBlinking Should the terminal text cursor blink?
     */
    @SuppressWarnings("WeakerAccess")
    public SwingTerminalDeviceConfiguration(int lineBufferScrollbackSize, int blinkLengthInMilliSeconds, CursorStyle cursorStyle, TextColor cursorColor, boolean cursorBlinking) {
        this.lineBufferScrollbackSize = lineBufferScrollbackSize;
        this.blinkLengthInMilliSeconds = blinkLengthInMilliSeconds;
        this.cursorStyle = cursorStyle;
        this.cursorColor = cursorColor;
        this.cursorBlinking = cursorBlinking;
    }

    /**
     * Returns the length of a 'blink', which is the interval time a character with the blink SGR enabled with be drawn
     * with foreground color and background color set to the same.
     * @return Milliseconds of a blink interval
     */
    public int getBlinkLengthInMilliSeconds() {
        return blinkLengthInMilliSeconds;
    }

    /**
     * How many lines of history should be saved so the user can scroll back to them?
     * @return Number of lines in the scrollback buffer
     */
    public int getLineBufferScrollbackSize() {
        return lineBufferScrollbackSize;
    }

    /**
     * Style the text cursor should take
     * @return Text cursor style
     * @see com.googlecode.lanterna.terminal.swing.SwingTerminalDeviceConfiguration.CursorStyle
     */
    public CursorStyle getCursorStyle() {
        return cursorStyle;
    }

    /**
     * What color to draw the text cursor color in
     * @return Color of the text cursor
     */
    public TextColor getCursorColor() {
        return cursorColor;
    }

    /**
     * Should the text cursor be blinking
     * @return {@code true} if the text cursor should be blinking
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCursorBlinking() {
        return cursorBlinking;
    }

    /**
     * Returns a copy of this device configuration but with a different size of the scrollback buffer
     * @param lineBufferScrollbackSize Size of the scrollback buffer (in number of lines) the copy should have
     * @return Copy of this device configuration with a specified size for the scrollback buffer
     */
    public SwingTerminalDeviceConfiguration withLineBufferScrollbackSize(int lineBufferScrollbackSize) {
        if(this.lineBufferScrollbackSize == lineBufferScrollbackSize) {
            return this;
        }
        else {
            return new SwingTerminalDeviceConfiguration(
                    lineBufferScrollbackSize,
                    blinkLengthInMilliSeconds,
                    cursorStyle,
                    cursorColor,
                    cursorBlinking);
        }
    }

    /**
     * Different cursor styles supported by SwingTerminal
     */
    public enum CursorStyle {
        /**
         * The cursor is drawn by inverting the front- and background colors of the cursor position
         */
        REVERSED,
        /**
         * The cursor is drawn by using the cursor color as the background color for the character at the cursor position
         */
        FIXED_BACKGROUND,
        /**
         * The cursor is rendered as a thick horizontal line at the bottom of the character
         */
        UNDER_BAR,
        /**
         * The cursor is rendered as a left-side aligned vertical line
         */
        VERTICAL_BAR,
        ;
    }
}
