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

import java.util.ArrayList;
import java.util.List;

/**
 * Containing a some very fundamental implementations that should be common for all terminals
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
    public void applyForegroundColor(TextColor color) {
        color.applyAsForeground(this);
    }

    @Override
    public void applyBackgroundColor(TextColor color) {
        color.applyAsBackground(this);
    }

    /**
     * Used internally to get the last size known to the terminal
     * @return 
     */
    protected TerminalSize getLastKnownSize() {
        return lastKnownSize;
    }
}
