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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TextColor;

/**
 * Object that encapsulates the configuration parameters for the terminal 'device' that a SwingTerminal is emulating.
 * This includes properties such as the shape of the cursor, the color of the cursor, how large scrollback is available
 * and if the cursor should blink or not.
 * @author martin
 */
public class TerminalEmulatorDeviceConfiguration {

    /**
     * This is a static reference to the default terminal device configuration. Use this one if you are unsure.
     * @return A terminal device configuration object with all settings set to default
     */
    public static TerminalEmulatorDeviceConfiguration getDefault() {
        return new TerminalEmulatorDeviceConfiguration();
    }

    private final int lineBufferScrollbackSize;
    private final int blinkLengthInMilliSeconds;
    private final CursorStyle cursorStyle;
    private final TextColor cursorColor;
    private final boolean cursorBlinking;
    private final boolean clipboardAvailable;

    /**
     * Creates a new terminal device configuration object with all the defaults set
     */
    @SuppressWarnings("WeakerAccess")
    public TerminalEmulatorDeviceConfiguration() {
        this(2000, 500, CursorStyle.REVERSED, new TextColor.RGB(255, 255, 255), false, true);
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
    public TerminalEmulatorDeviceConfiguration(
            int lineBufferScrollbackSize,
            int blinkLengthInMilliSeconds,
            CursorStyle cursorStyle,
            TextColor cursorColor,
            boolean cursorBlinking) {

        this(lineBufferScrollbackSize, blinkLengthInMilliSeconds, cursorStyle, cursorColor, cursorBlinking, true);
    }

    /**
     * Creates a new terminal device configuration object with all configurable values specified.
     * @param lineBufferScrollbackSize How many lines of scrollback buffer should the terminal save?
     * @param blinkLengthInMilliSeconds How many milliseconds does a 'blink' last
     * @param cursorStyle Style of the terminal text cursor
     * @param cursorColor Color of the terminal text cursor
     * @param cursorBlinking Should the terminal text cursor blink?
     * @param clipboardAvailable Should the terminal support pasting text from the clipboard?
     */
    @SuppressWarnings("WeakerAccess")
    public TerminalEmulatorDeviceConfiguration(
            int lineBufferScrollbackSize,
            int blinkLengthInMilliSeconds,
            CursorStyle cursorStyle,
            TextColor cursorColor,
            boolean cursorBlinking,
            boolean clipboardAvailable) {

        this.lineBufferScrollbackSize = lineBufferScrollbackSize;
        this.blinkLengthInMilliSeconds = blinkLengthInMilliSeconds;
        this.cursorStyle = cursorStyle;
        this.cursorColor = cursorColor;
        this.cursorBlinking = cursorBlinking;
        this.clipboardAvailable = clipboardAvailable;
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
     * @see TerminalEmulatorDeviceConfiguration.CursorStyle
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

    public boolean isClipboardAvailable() {
        return clipboardAvailable;
    }

    /**
     * Copies the current configuration. The new object has the given value.
     * @param blinkLengthInMilliSeconds How many milliseconds does a 'blink' last
     * @return A copy of the current configuration with the changed value.
     */
    public TerminalEmulatorDeviceConfiguration withBlinkLengthInMilliSeconds(int blinkLengthInMilliSeconds) {
        if (this.blinkLengthInMilliSeconds == blinkLengthInMilliSeconds) {
            return this;
        } else {
            return new TerminalEmulatorDeviceConfiguration(
                    this.lineBufferScrollbackSize,
                    blinkLengthInMilliSeconds,
                    this.cursorStyle,
                    this.cursorColor,
                    this.cursorBlinking,
                    this.clipboardAvailable);
        }
    }

    /**
     * Copies the current configuration. The new object has the given value.
     * @param lineBufferScrollbackSize How many lines of scrollback buffer should the terminal save?
     * @return  A copy of the current configuration with the changed value.
     */
    public TerminalEmulatorDeviceConfiguration withLineBufferScrollbackSize(int lineBufferScrollbackSize) {
        if(this.lineBufferScrollbackSize == lineBufferScrollbackSize) {
            return this;
        } else {
            return new TerminalEmulatorDeviceConfiguration(
                    lineBufferScrollbackSize,
                    this.blinkLengthInMilliSeconds,
                    this.cursorStyle,
                    this.cursorColor,
                    this.cursorBlinking,
                    this.clipboardAvailable);
        }
    }

    /**
     * Copies the current configuration. The new object has the given value.
     * @param cursorStyle Style of the terminal text cursor
     * @return A copy of the current configuration with the changed value.
     */
    public TerminalEmulatorDeviceConfiguration withCursorStyle(CursorStyle cursorStyle) {
        if(this.cursorStyle == cursorStyle) {
            return this;
        } else {
            return new TerminalEmulatorDeviceConfiguration(
                    this.lineBufferScrollbackSize,
                    this.blinkLengthInMilliSeconds,
                    cursorStyle,
                    this.cursorColor,
                    this.cursorBlinking,
                    this.clipboardAvailable);
        }
    }

    /**
     * Copies the current configuration. The new object has the given value.
     * @param cursorColor Color of the terminal text cursor
     * @return A copy of the current configuration with the changed value.
     */
    public TerminalEmulatorDeviceConfiguration withCursorColor(TextColor cursorColor) {
        if(this.cursorColor == cursorColor) {
            return this;
        } else {
            return new TerminalEmulatorDeviceConfiguration(
                    this.lineBufferScrollbackSize,
                    this.blinkLengthInMilliSeconds,
                    this.cursorStyle,
                    cursorColor,
                    this.cursorBlinking,
                    this.clipboardAvailable);
        }
    }

    /**
     * Copies the current configuration. The new object has the given value.
     * @param cursorBlinking Should the terminal text cursor blink?
     * @return A copy of the current configuration with the changed value.
     */
    public TerminalEmulatorDeviceConfiguration withCursorBlinking(boolean cursorBlinking) {
        if(this.cursorBlinking == cursorBlinking) {
            return this;
        } else {
            return new TerminalEmulatorDeviceConfiguration(
                    this.lineBufferScrollbackSize,
                    this.blinkLengthInMilliSeconds,
                    this.cursorStyle,
                    this.cursorColor,
                    cursorBlinking,
                    this.clipboardAvailable);
        }
    }

    /**
     * Copies the current configuration. The new object has the given value.
     * @param clipboardAvailable Should the terminal support pasting text from the clipboard?
     * @return A copy of the current configuration with the changed value.
     */
    public TerminalEmulatorDeviceConfiguration withClipboardAvailable(boolean clipboardAvailable) {
        if(this.clipboardAvailable == clipboardAvailable) {
            return this;
        } else {
            return new TerminalEmulatorDeviceConfiguration(
                    this.lineBufferScrollbackSize,
                    this.blinkLengthInMilliSeconds,
                    this.cursorStyle,
                    this.cursorColor,
                    this.cursorBlinking,
                    clipboardAvailable);
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
