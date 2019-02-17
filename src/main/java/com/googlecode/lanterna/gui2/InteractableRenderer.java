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

import com.googlecode.lanterna.TerminalPosition;

/**
 * Extended interface for component renderers used with interactable components. Because only the renderer knows what
 * the component looks like, the component itself cannot know where to place the text cursor, so this method is instead
 * delegated to this interface that extends the regular component renderer.
 *
 * @author Martin
 * @param <T> Type of the component this {@code InteractableRenderer} is designed for
 */
public interface InteractableRenderer<T extends Component & Interactable> extends ComponentRenderer<T> {
    TerminalPosition getCursorLocation(T component);
}
