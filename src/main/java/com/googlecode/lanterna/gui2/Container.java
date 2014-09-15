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
public interface Container {
    void addComponent(Component component);
    void removeComponent(Component component);
    void removeAllComponents();
    
    boolean containsComponent(Component component);
    int getComponentIndex(Component component);
    Component getComponentAt(int index);
    int getNumberOfComponents();

    /**
     * Sets the layout manager responsible to placing and sizing the components in the window
     * @param layoutManager Layout manager this container should use
     */
    void setLayoutManager(LayoutManager layoutManager);
}
