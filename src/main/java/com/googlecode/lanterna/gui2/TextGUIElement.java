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

/**
 * This interface is the base part in the Lanterna Text GUI component hierarchy
 * @author Martin
 */
public interface TextGUIElement {
    void draw(TextGUIGraphics graphics);
    boolean isInvalid();
    
    /**
     * Translates a position local to the container to the root container's coordinate space
     * @param position Position to translate (relative to the container's top-left corner)
     * @return Position in root container space
     */
    TerminalPosition toRootContainer(TerminalPosition position);
    
    /**
     * Returns the container which is holding this container, or {@code null} if it's not assigned to anything.
     * @return Parent container or null
     */
    Composite getParent();

    /**
     * Returns the root container that this container belongs to. In a window-based GUI system, this will be a Window.
     * @return The root container this component is placed on, or {@code null} if none
     */
    RootContainer getRootContainer();
}
