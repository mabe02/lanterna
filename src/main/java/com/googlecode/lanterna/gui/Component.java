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
 * The base component interface, which all components must implement.
 * @author Martin
 */
public interface Component
{
    /**
     * @return Container which currently owns this component
     */
    Container getParent();
    
    /**
     * Returns the window which is directly or indirectly containing this component. If the 
     * component has not been placed on any window yet, it will return {@code null}.
     * @return Window that contains this component or {@code null}
     */
    Window getWindow();
    
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
     * This method is used by the layout system when it needs to shrink components
     * because the available space is too small. Scrollable components will be 
     * shrunk first.
     * @return True if this component has a scrollable area and can contain more
     * text than fits on the screen
     */
    boolean isScrollable();
    
    /**
     * This method is called by the layout system to figure out how much space
     * each component will be assigned. Return the size you would like your 
     * component to use, for best presentation. There is no guarante that the
     * component will actually be assigned this space, you could get both 
     * smaller and larger space assigned.
     * @return The preferred size of this component, given its current state
     */
    TerminalSize getPreferredSize();
    
    /**
     * This method is called by the layout system to figure out what the 
     * absolute minimum size is for this component. If the component cannot be
     * assigned this space, it will be inactivated and not rendered.
     * @return The absolutely minimum space that needs to be available for this
     * component
     */
    TerminalSize getMinimumSize();
    
    /**
     * If called with a non-null parameter, it will override the component's 
     * own preferred size calculation and instead {@code getPreferredSize()} 
     * will return the value passed in. If called with {@code null}, the 
     * calculation will be used again.
     * @param preferredSizeOverride Value {@code getPreferredSize()} should 
     * return instead of calculating it
     */
    void setPreferredSize(TerminalSize preferredSizeOverride);
    
    /**
     * Sets the alignment property on the Component which will serve as a hint to the rendering code
     * as to how to draw the component if it is assigned more space that it asked for. The behavior
     * of this property is different between components, some may use it and some may not.
     * 
     * <p>The default alignment is also up to each component to decide, see the documentation for 
     * the individual component you are using.
     * @param alignment Alignment of the component
     */
    void setAlignment(Alignment alignment);
    
    /**
     * @return Returns the alignment of this component
     */
    Alignment getAlignment();
    
    public static enum Alignment {
        TOP_CENTER,
        BOTTON_CENTER,
        LEFT_CENTER,
        RIGHT_CENTER,
        CENTER,
        FILL,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
    }
}
