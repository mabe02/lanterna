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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;

/**
 * This class is a simple implementation of Terminal.ResizeListener which will keep track of the size of the terminal
 * and let you know if the terminal has been resized since you last checked. This can be useful to avoid threading 
 * problems with the resize callback when your application is using a main event loop.
 * 
 * @author martin
 */
@SuppressWarnings("WeakerAccess")
public class SimpleTerminalResizeListener implements TerminalResizeListener {

    boolean wasResized;
    TerminalSize lastKnownSize;

    /**
     * Creates a new SimpleTerminalResizeListener
     * @param initialSize Before any resize event, this listener doesn't know the size of the terminal. By supplying a
     * value here, you control what getLastKnownSize() will return if invoked before any resize events has reached us.
     */
    public SimpleTerminalResizeListener(TerminalSize initialSize) {
        this.wasResized = false;
        this.lastKnownSize = initialSize;
    }
    
    /**
     * Checks if the terminal was resized since the last time this method was called. If this is the first time calling
     * this method, the result is going to be based on if the terminal has been resized since this listener was attached
     * to the Terminal.
     * 
     * @return true if the terminal was resized, false otherwise
     */
    public synchronized boolean isTerminalResized() {
        if(wasResized) {
            wasResized = false;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the last known size the Terminal is supposed to have.
     * 
     * @return Size of the terminal, as of the last resize update
     */
    public TerminalSize getLastKnownSize() {
        return lastKnownSize;
    }
    
    @Override
    public synchronized void onResized(Terminal terminal, TerminalSize newSize) {
        this.wasResized = true;
        this.lastKnownSize = newSize;
    }
    
}
