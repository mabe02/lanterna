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

import com.googlecode.lanterna.gui.listener.ComponentListener;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * The base component interface, which all components must implement
 * @author Martin
 */
public interface Component
{
    /**
     * @return Container which currently has this component
     */
    Container getParent();
    void addComponentListener(ComponentListener cl);
    void removeComponentListener(ComponentListener cl);
    
    /**
     * This is the main 'paint' method for a component, it's called when 
     * lanterna wants the component to re-draw itself. Use the supplied 
     * TextGraphics object to draw the component. In order to find out how
     * the size of your compnent (as decided by the layout), use the graphics
     * objects and query for its size.
     * @param graphics TextGraphics object to use for drawing the component and
     * getting information about the drawing context
     */
    void repaint(TextGraphics graphics);
    
    /**
     * If false, the GUI system will ignore this component in the layout and 
     * drawing stage
     * @param visible true if you want the component to be visible, false to 
     * make it hidden
     */
    void setVisible(boolean visible);
    
    /**
     * @return true if the component is visible, false otherwise
     */
    boolean isVisible();
    
    /**
     * This method is called by the layout system to figure out how much space
     * each component will be assigned. Return the size you would like your 
     * component to use, for best presentation. There is no guarante that the
     * component will actually be assigned this space, you could get both 
     * smaller and larger space assigned.
     * @return The preferred size of this component, given its current state
     */
    TerminalSize getPreferredSize();
}
