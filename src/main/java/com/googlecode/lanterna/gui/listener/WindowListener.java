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
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.gui.listener;

import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.input.Key;

/**
 * This listener class is for listening to events happening to a {@code Window}. If you want to 
 * listen to window events but don't care about all the different event types, you can extend
 * the {@code WindowAdapter} class instead.
 * @see Window
 * @see WindowAdapter
 * @author Martin
 */
public interface WindowListener {
    
    /**
     * Called when a window has been internally modified and needs to be repainted by the GUI system.
     * @param window Window that was invalidated
     */
    void onWindowInvalidated(Window window);
    
    /**
     * Called by the window when it has been added and showed up on a {@code GUIScreen}.
     * @param window Window that has been displayed
     */
    void onWindowShown(Window window);
    
    /**
     * Called by the window when it has been closed and removed from the {@code GUIScreen}.
     * @param window Window that was closed and removed
     */
    void onWindowClosed(Window window);
    
    /**
     * Called by the window when there was a keyboard input event that no component could handle
     * @param window Window that recieved the input event
     * @param key Key that couldn't be handled
     */
    void onUnhandledKeyboardInteraction(Window window, Key key);
    
    /**
     * Called by the window whenever the input focus has changed from one component to another
     * @param window Window that switched input focus
     * @param fromComponent Component that lost focus, or {@code null} if no component was previously focused
     * @param toComponent Component that received focus, or {@code null} if no component has focus
     */
    void onFocusChanged(Window window, Interactable fromComponent, Interactable toComponent);
}
