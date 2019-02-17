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

/**
 * This interface is the base part in the Lanterna Text GUI component hierarchy
 * @author Martin
 */
public interface TextGUIElement {
    /**
     * Draws the GUI element using the supplied TextGUIGraphics object. This is the main method to implement when you
     * want to create your own GUI components.
     * @param graphics Graphics object to use when drawing the component
     */
    void draw(TextGUIGraphics graphics);

    /**
     * Checks if this element (or any of its child components, if any) has signaled that what it's currently displaying
     * is out of date and needs re-drawing.
     * @return {@code true} if the component is invalid and needs redrawing, {@code false} otherwise
     */
    boolean isInvalid();
}
