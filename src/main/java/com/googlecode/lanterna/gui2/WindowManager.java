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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.util.List;

/**
 * Window manager is a class that is plugged in to a {@code WindowBasedTextGUI} to manage the position and placement
 * of windows. The window manager doesn't contain the list of windows so it normally does not need to maintain much
 * state but it is passed all required objects as the window model changes.
 * @see DefaultWindowManager
 * @author Martin
 */
public interface WindowManager {

    /**
     * Will be polled by the the {@link WindowBasedTextGUI} to see if the window manager believes an update is required.
     * For example, it could be that there is no input, no events and none of the components are invalid, but the window
     * manager decides for some other reason that the GUI needs to be updated, in that case you should return
     * {@code true} here. Please note that returning {@code false} will not prevent updates from happening, it's just
     * stating that the window manager isn't aware of some internal state change that would require an update.
     * @return {@code true} if the window manager believes the GUI needs to be update, {@code false} otherwise
     */
    boolean isInvalid();

    /**
     * Returns the {@code WindowDecorationRenderer} for a particular window
     * @param window Window to get the decoration renderer for
     * @return {@code WindowDecorationRenderer} for the window
     */
    WindowDecorationRenderer getWindowDecorationRenderer(Window window);

    /**
     * Called whenever a window is added to the {@code WindowBasedTextGUI}. This gives the window manager an opportunity
     * to setup internal state, if required, or decide on an initial position of the window
     * @param textGUI GUI that the window was added too
     * @param window Window that was added
     * @param allWindows All windows, including the new window, in the GUI
     */
    void onAdded(WindowBasedTextGUI textGUI, Window window, List<Window> allWindows);

    /**
     * Called whenever a window is removed from a {@code WindowBasedTextGUI}. This gives the window manager an
     * opportunity to clear internal state if needed.
     * @param textGUI GUI that the window was removed from
     * @param window Window that was removed
     * @param allWindows All windows, excluding the removed window, in the GUI
     */
    @SuppressWarnings("EmptyMethod")
    void onRemoved(WindowBasedTextGUI textGUI, Window window, List<Window> allWindows);

    /**
     * Called by the GUI system before iterating through all windows during the drawing process. The window manager
     * should ensure the position and decorated size of all windows at this point by using
     * {@code Window.setPosition(..)} and {@code Window.setDecoratedSize(..)}. Be sure to inspect the window hints
     * assigned to the window, in case you want to try to honour them. Use the
     * {@link #getWindowDecorationRenderer(Window)} method to get the currently assigned window decoration rendering
     * class which can tell you the decorated size of a window given it's content size.
     *
     * @param textGUI Text GUI that is about to draw the windows
     * @param allWindows All windows that are going to be drawn, in the order they will be drawn
     * @param screenSize Size of the terminal that is available to draw on
     */
    void prepareWindows(WindowBasedTextGUI textGUI, List<Window> allWindows, TerminalSize screenSize);
}
