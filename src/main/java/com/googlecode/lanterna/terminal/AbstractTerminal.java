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
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Containing a some very fundamental functionality that should be common (and usable) to all terminal implementations.
 * All the Terminal implementers within Lanterna extends from this class.
 *
 * @author Martin
 */
public abstract class AbstractTerminal implements Terminal {

    private final List<TerminalResizeListener> resizeListeners;
    private TerminalSize lastKnownSize;

    protected AbstractTerminal() {
        this.resizeListeners = new ArrayList<TerminalResizeListener>();
        this.lastKnownSize = null;
    }

    @Override
    public void addResizeListener(TerminalResizeListener listener) {
        if (listener != null) {
            resizeListeners.add(listener);
        }
    }

    @Override
    public void removeResizeListener(TerminalResizeListener listener) {
        if (listener != null) {
            resizeListeners.remove(listener);
        }
    }

    /**
     * Call this method when the terminal has been resized or the initial size of the terminal has been discovered. It
     * will trigger all resize listeners, but only if the size has changed from before.
     *
     * @param columns Number of columns in the new size
     * @param rows Number of rows in the new size
     */
    protected synchronized void onResized(int columns, int rows) {
        onResized(new TerminalSize(columns, rows));
    }

    /**
     * Call this method when the terminal has been resized or the initial size of the terminal has been discovered. It
     * will trigger all resize listeners, but only if the size has changed from before.
     *
     * @param newSize Last discovered terminal size
     */
    protected synchronized void onResized(TerminalSize newSize) {
        if (lastKnownSize == null || !lastKnownSize.equals(newSize)) {
            lastKnownSize = newSize;
            for (TerminalResizeListener resizeListener : resizeListeners) {
                resizeListener.onResized(this, lastKnownSize);
            }
        }
    }

    @Override
    public TextGraphics newTextGraphics() throws IOException {
        return new TerminalTextGraphics(this);
    }
}
