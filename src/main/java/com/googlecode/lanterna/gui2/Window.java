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

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;

import java.util.Set;

/**
 * Window is a base unit in the TextGUI system, it represents a collection of components grouped together, usually
 * surrounded by a border and a title. Modern computer system GUIs are normally based around the metaphor of windows,
 * so I don't think you should have any problems understanding what this means.
 * @author Martin
 */
public interface Window {
    /**
     * @return title of the window
     */
    String getTitle();

    /**
     * Is the window visible or not; note that window managers may choose to ignore this
     * @return Whether the window wants to be visible or not
     */
    boolean isVisible();

    /**
     * This method is used to determine if the window requires re-drawing. The most common cause for this is the some
     * of its components has changed and we need a re-draw to make these changes visible.
     * @return {@code true} if the window would like to be re-drawn, {@code false} if the window doesn't need
     */
    boolean isInvalid();

    /**
     * Returns the size this window would like to be
     * @return Desired size of this window
     */
    TerminalSize getPreferredSize();

    /**
     * Called by the GUI system (or something imitating the GUI system) to draw the window. The TextGUIGraphics object
     * should be used to perform the drawing operations.
     * @param graphics TextGraphics object to draw with
     */
    void draw(TextGUIGraphics graphics);

    /**
     * Called by the GUI system's window manager when it has decided that this window should receive the keyboard input.
     * The window will decide what to do with this input, usually sending it to one of its sub-components, but if it
     * isn't able to find any handler for this input it should return {@code false} so that the window manager can take
     * further decisions on what to do with it.
     * @param key Keyboard input
     * @return {@code true} If the window could handle the input, false otherwise
     */
    boolean handleInput(KeyStroke key);

    /**
     * Closes the window, which will remove it from the GUI
     */
    void close();

    /**
     * Returns a collection of hints for the Window manager. It's then up to the window manager to decide which ones it
     * understands and which ones it will honour. Please see each individual WindowManager for a list of valid values.
     * @return Hints for the window manager
     */
    Set<WindowManager.Hint> getWindowManagerHints();
}
