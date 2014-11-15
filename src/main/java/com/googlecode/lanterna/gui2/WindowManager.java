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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.Collection;

/**
 *
 * @author Martin
 */
public interface WindowManager {
    void addWindow(Window window);
    void removeWindow(Window window);
    
    Collection<Window> getWindows();
    Window getActiveWindow();
    boolean isInvalid();

    WindowDecorationRenderer getWindowDecorationRenderer(Window window);

    TerminalPosition getTopLeftPosition(Window window, TerminalSize screenSize);

    TerminalSize getSize(Window window, TerminalPosition topLeftPosition, TerminalSize screenSize);
    
    /**
     * Adds a listener to this WindowManager to fire events on.
     * @param listener Listener to add
     */
    void addListener(Listener listener);
    
    /**
     * Removes a listener from this WindowManager so that it will no longer receive events
     * @param listener Listener to remove
     */
    void removeListener(Listener listener);
    
    /**
     * Listener interface for WindowManager, firing on events related to window manager operations
     */
    public static interface Listener {
        /**
         * Fired when a window is added to this window manager
         * @param manager Window manager that is firing the event
         * @param window Window that was added
         */
        void onWindowAdded(WindowManager manager, Window window);
        
        /**
         * Fired when a window is removed from this window manager
         * @param manager Window manager that is firing the event
         * @param window Window that was removed
         */
        void onWindowRemoved(WindowManager manager, Window window);
    }
}
