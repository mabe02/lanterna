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

/**
 *
 * @author Martin
 */
public interface Container extends Component {

    /**
     * Returns the number of components inside this Container
     * @return Number of components this Container keeps
     */
    int getNumberOfComponents();
    
    boolean containsComponent(Component component);

    /**
     * Returns the index of the component, within this container
     * @param component Component to look for
     * @return Index of the component, if it was found, -1 otherwise
     */
    int getComponentIndex(Component component);

    /**
     * Returns the component at the specified index, or {@code null} if index was one number larger than the number of
     * components in this container. If the index is further out of bounds, or negative, this method throws
     * ArrayIndexOutOfBounds.
     * @param index Index to get the component at
     * @return The component at the specified index, or {@code null} if there was no component
     */
    Component getComponentAt(int index);
    
    void addComponent(Component component);
    
    void removeComponent(Component component);
}
