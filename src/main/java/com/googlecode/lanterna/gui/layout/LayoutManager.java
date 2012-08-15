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
package com.googlecode.lanterna.gui.layout;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.List;

/**
 * Classes that implements this interface are for keeping and maintaining the 
 * component layout of a container. They will be responsible for, given a 
 * certain usable area of the terminal, position the components in a certain
 * way depending on the implemention.
 * 
 * @author Martin
 */
public interface LayoutManager
{
    /**
     * Adds a component to the layout manager
     * @param component Component to add to the layout manager
     * @param parameters Parameters to associate with the component on this layout
     */
    void addComponent(Component component, LayoutParameter... parameters);
    
    /**
     * Removes a component from the layout
     * @param component Component to remove
     */
    void removeComponent(Component component);

    /**
     * Calculates the optimal size of the container this layout manager is
     * controlling by asking every component controlled by the manager how big
     * they would like to be. Depending on implementation, the layout manager
     * may or may not add or remove space to the combined number.
     * @return Size of the preferred area the layout would like
     */
    TerminalSize getPreferredSize();

    /**
     * Given a size, lay out all the components on this area
     * @param layoutArea Size that the layout is allowed to use
     * @return List of all components, laid out on the allowed area
     */
    List<? extends LaidOutComponent> layout(TerminalSize layoutArea);

    /**
     * @return True if there is a component within this layout that would like
     * to use as much vertical space as is possible on the screen
     */
    boolean maximisesVertically();
    
    /**
     * @return True if there is a component within this layout that would like
     * to use as much horizontal space as is possible on the screen
     */
    boolean maximisesHorisontally();

    /**
     * This interface represents a component that has been placed on an area. 
     * You can use the methods exposed by it to retrieve details about each
     * component within the layout and where the LayoutManager believes they
     * should be placed.
     */
    public static interface LaidOutComponent {
        /**
         * @return The component that was positioned
         */
        Component getComponent();
        /**
         * @return Size the component is allowed to use
         */
        TerminalSize getSize();
        
        /**
         * @return Position, relative to the assigned area's top left corner 
         * (which would be 0x0 here), of the top-left corner of the component.
         */
        TerminalPosition getTopLeftPosition();
    }
}
