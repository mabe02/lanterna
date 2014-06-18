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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Containing a some very fundamental functionality that should be common for all terminal implementations.
 *
 * @author Martin
 */
public abstract class AbstractTerminal implements Terminal {

    private final List<ResizeListener> resizeListeners;
    private TerminalSize lastKnownSize;

    public AbstractTerminal() {
        this.resizeListeners = new ArrayList<ResizeListener>();
        this.lastKnownSize = null;
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        if (listener != null) {
            resizeListeners.add(listener);
        }
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        if (listener != null) {
            resizeListeners.remove(listener);
        }
    }

    /**
     * Call this method when the terminal has been resized or the initial size of the terminal has been discovered. It
     * will trigger all resize listeners, but only if the size has changed from before.
     *
     * @param columns
     * @param rows
     */
    protected synchronized void onResized(int columns, int rows) {
        TerminalSize newSize = new TerminalSize(columns, rows);
        if (lastKnownSize == null || !lastKnownSize.equals(newSize)) {
            lastKnownSize = newSize;
            for (ResizeListener resizeListener : resizeListeners) {
                resizeListener.onResized(this, lastKnownSize);
            }
        }
    }

    @Override
    public void setForegroundColor(TextColor color) throws IOException {
        color.applyAsForeground(this);
    }

    /**
     * Changes the foreground color for all the following characters put to the terminal. The foreground color is what
     * color to draw the text in, as opposed to the background color which is the color surrounding the characters. This
     * way of setting the foreground color, compared with the other applyForegroundColor(..) overloads, is the most safe
     * and compatible.
     *
     * @param color Color to use for foreground
     */
    protected abstract void applyForegroundColor(TextColor.ANSI color) throws IOException;

    /**
     * Changes the foreground color for all the following characters put to the terminal. The foreground color is what
     * color to draw the text in, as opposed to the background color which is the color surrounding the characters.<br>
     * <b>Warning:</b> This method will use the XTerm 256 color extension, it may not be supported on all terminal
     * emulators! The index values are resolved as this:<br>
     * 0 .. 15 - System color, these are taken from the schema. 16 .. 231 - Forms a 6x6x6 RGB color cube.<br>
     * 232 .. 255 - A gray scale ramp without black and white.<br>
     *
     * <p>
     * For more details on this, please see <a
     * href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit message to Konsole.
     *
     * @param index Color index from the XTerm 256 color space
     */
    protected abstract void applyForegroundColor(int index) throws IOException;

    /**
     * Changes the foreground color for all the following characters put to the terminal. The foreground color is what
     * color to draw the text in, as opposed to the background color which is the color surrounding the characters.<br>
     * <b>Warning:</b> Only a few terminal support 24-bit color control codes, please avoid using this unless you know
     * all users will have compatible terminals. For details, please see
     * <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit log.
     *
     * @param r Red intensity, from 0 to 255
     * @param g Green intensity, from 0 to 255
     * @param b Blue intensity, from 0 to 255
     */
    protected abstract void applyForegroundColor(int r, int g, int b) throws IOException;

    @Override
    public void setBackgroundColor(TextColor color) throws IOException {
        color.applyAsBackground(this);
    }

    /**
     * Changes the background color for all the following characters put to the terminal. The background color is the
     * color surrounding the text being printed. This way of setting the background color, compared with the other
     * applyBackgroundColor(..) overloads, is the most safe and compatible.
     *
     * @param color Color to use for the background
     */
    protected abstract void applyBackgroundColor(TextColor.ANSI color) throws IOException;

    /**
     * Changes the background color for all the following characters put to the terminal. The background color is the
     * color surrounding the text being printed.<br>
     * <b>Warning:</b> This method will use the XTerm 256 color extension, it may not be supported on all terminal
     * emulators! The index values are resolved as this:<br>
     * 0 .. 15 - System color, these are taken from the schema. 16 .. 231 - Forms a 6x6x6 RGB color cube.<br>
     * 232 .. 255 - A gray scale ramp without black and white.<br>
     *
     * <p>
     * For more details on this, please see <a
     * href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit message to Konsole.
     *
     * @param index Index of the color to use, from the XTerm 256 color extension
     */
    protected abstract void applyBackgroundColor(int index) throws IOException;

    /**
     * Changes the background color for all the following characters put to the terminal. The background color is the
     * color surrounding the text being printed.<br>
     * <b>Warning:</b> Only a few terminal support 24-bit color control codes, please avoid using this unless you know
     * all users will have compatible terminals. For details, please see
     * <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit log.
     *
     * @param r Red intensity, from 0 to 255
     * @param g Green intensity, from 0 to 255
     * @param b Blue intensity, from 0 to 255
     */
    protected abstract void applyBackgroundColor(int r, int g, int b) throws IOException;

    /**
     * Used internally to get the last size known to the terminal
     * @return
     */
    protected TerminalSize getLastKnownSize() {
        return lastKnownSize;
    }
}
