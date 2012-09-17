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

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.gui.listener.ContainerListener;

/**
 * This interface must be implemented by any component this is to have 
 * subcomponents.
 * @author Martin
 */
public interface Container extends Component
{
    void addContainerListener(ContainerListener cl);
    void removeContainerListener(ContainerListener cl);
    
    /**
     * Adds a new subcomponent to this container.
     * @param component Component to add to this container
     * @param layoutParameters Optional parameters to give hits to the layout manager
     */
    void addComponent(Component component, LayoutParameter... layoutParameters);
    
    /**
     * Removes a component from this container.
     * @param component Component to remove from this container
     * @return {@code true} if a component was found and removed, {@code false} otherwise
     */
    boolean removeComponent(Component component);
    
    /**
     * @return How many component this container currently has
     */
    int getComponentCount();
    
    /**
     * @param index Index to look up a component at
     * @return Component at the specified index
     */
    Component getComponentAt(int index);
    
    /**
     * This method can used to see if a particular component is contained with this objects list of
     * immediate children. It will not search through sub-containers, you'll need to do that manually
     * if you need this functionality.
     * @param component Component to search for
     * @return {@code true} is the component is found in this containers list of direct children,
     * otherwise {@code false}
     */
    boolean containsComponent(Component component);
}
