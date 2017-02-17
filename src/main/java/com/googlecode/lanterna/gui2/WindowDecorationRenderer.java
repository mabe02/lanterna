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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * Interface that defines a class that draws window decorations, i.e. a surrounding layer around the window that usually
 * looks like a border to make it easier for a user to visually separate the windows.
 * @see DefaultWindowDecorationRenderer
 * @author Martin
 */
public interface WindowDecorationRenderer {
    /**
     * Draws the window decorations for a particular window and returns a new TextGraphics that is locked to the area
     * inside of the window decorations where the content of the window should be drawn
     * @param textGUI Which TextGUI is calling
     * @param graphics Graphics to use for drawing
     * @param window Window to draw
     * @return A new TextGraphics that is limited to the area inside the decorations just drawn
     */
    TextGUIGraphics draw(WindowBasedTextGUI textGUI, TextGUIGraphics graphics, Window window);

    /**
     * Retrieves the full size of the window, including all window decorations, given all components inside the window.
     * @param window Window to calculate size for
     * @param contentAreaSize Size of the content area in the window
     * @return Full size of the window, including decorations
     */
    TerminalSize getDecoratedSize(Window window, TerminalSize contentAreaSize);

    /**
     * Returns how much to step right and down from the top left position of the window decorations to the top left
     * position of the actual window
     * @param window Window to get the offset for
     * @return Position of the top left corner of the window, relative to the top left corner of the window decoration
     */
    TerminalPosition getOffset(Window window);
}
