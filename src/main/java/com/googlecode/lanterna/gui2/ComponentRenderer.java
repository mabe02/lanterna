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

/**
 * This interface defines a renderer for a component, an external class that does the sizing and rendering. All
 * components will have a default renderer defined, which can usually be overridden manually and swapped out for a
 * different renderer, but also themes can contain renderer definitions which are automatically assigned to their
 * associated components.
 * @param <T> Type of the component which this renderer is designed for
 * @author Martin
 */
public interface ComponentRenderer<T extends Component> {
    /**
     * Given the supplied component, how large does this renderer want the component to be? Notice that this is the
     * responsibility of the renderer and not the component itself, since the component has no idea what its visual
     * representation looks like.
     * @param component Component to calculate the preferred size of
     * @return The size this renderer would like the component to take up
     */
    TerminalSize getPreferredSize(T component);

    /**
     * Using the supplied graphics object, draws the component passed in.
     * @param graphics Graphics object to use for drawing
     * @param component Component to draw
     */
    void drawComponent(TextGUIGraphics graphics, T component);
}
