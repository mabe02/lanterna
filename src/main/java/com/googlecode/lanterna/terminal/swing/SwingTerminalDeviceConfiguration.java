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
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TextColor;

/**
 *
 * @author martin
 */
public class SwingTerminalDeviceConfiguration {

    public static final SwingTerminalDeviceConfiguration DEFAULT = new SwingTerminalDeviceConfiguration();

    private final int lineBufferScrollbackSize;
    private final int blinkLengthInMilliSeconds;
    private final CursorStyle cursorStyle;
    private final TextColor cursorColor;
    private final boolean cursorBlinking;

    @SuppressWarnings("WeakerAccess")
    public SwingTerminalDeviceConfiguration() {
        this(2000, 500, CursorStyle.REVERSED, TextColor.ANSI.WHITE, false);
    }

    @SuppressWarnings("WeakerAccess")
    public SwingTerminalDeviceConfiguration(int lineBufferScrollbackSize, int blinkLengthInMilliSeconds, CursorStyle cursorStyle, TextColor cursorColor, boolean cursorBlinking) {
        this.lineBufferScrollbackSize = lineBufferScrollbackSize;
        this.blinkLengthInMilliSeconds = blinkLengthInMilliSeconds;
        this.cursorStyle = cursorStyle;
        this.cursorColor = cursorColor;
        this.cursorBlinking = cursorBlinking;
    }

    public int getBlinkLengthInMilliSeconds() {
        return blinkLengthInMilliSeconds;
    }

    public int getLineBufferScrollbackSize() {
        return lineBufferScrollbackSize;
    }

    public CursorStyle getCursorStyle() {
        return cursorStyle;
    }

    public TextColor getCursorColor() {
        return cursorColor;
    }

    public boolean isCursorBlinking() {
        return cursorBlinking;
    }

    public SwingTerminalDeviceConfiguration withBlinkLengthInMilliSeconds(int blinkLengthInMilliSeconds) {
        if(this.blinkLengthInMilliSeconds == blinkLengthInMilliSeconds) {
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

    public SwingTerminalDeviceConfiguration withCursorStyle(CursorStyle cursorStyle) {
        if(this.cursorStyle == cursorStyle) {
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

    public SwingTerminalDeviceConfiguration withCursorColor(TextColor cursorColor) {
        if(this.cursorColor == cursorColor) {
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

    public SwingTerminalDeviceConfiguration withCursorBlinking(boolean cursorBlinking) {
        if(this.cursorBlinking == cursorBlinking) {
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

    public static enum CursorStyle {
        REVERSED,
        FIXED_BACKGROUND,
        DOUBLE_UNDERBAR,
        ;
    }
}
