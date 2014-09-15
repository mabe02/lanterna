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
import com.googlecode.lanterna.TerminalSize;

import java.util.Set;

/**
 *
 * @author Martin
 */
public interface Component extends TextGUIElement {
    /**
     * Returns the top-left corner of this component, measured from its parent.
     * @return Position of this component
     */
    TerminalPosition getPosition();

    /**
     * This method will be called by the layout manager when it has decided where the component is to be located. If you
     * call this method yourself, prepare for unexpected results.
     * @param position Top-left position of the component, relative to its parent
     */
    void setPosition(TerminalPosition position);

    /**
     * Returns how large this component is. If the layout manager has not yet laid this component out, it will return
     * an empty size (0x0)
     * @return How large this component is
     */
    TerminalSize getSize();

    /**
     * This method will be called by the layout manager when it has decided how large the component will be. If you call
     * this method yourself, prepare for unexpected results.
     * @param size Current size of the component
     */
    void setSize(TerminalSize size);

    /**
     * Returns the ideal size this component would like to have, in order to draw itself properly. There are no
     * guarantees the GUI system will decide to give it this size though.
     * @return Size we would like to be
     */
    TerminalSize getPreferredSize();

    /**
     * Adds a border around the component.
     * @param border Border to use around the component
     * @return Itself
     */
    Component withBorder(Border border);

    /**
     * Sets the layout manager parameters
     * @param parameters List of layout manager parameters
     */
    void setLayoutManagerParameters(LayoutManager.Parameter... parameters);

    /**
     * Returns the set of parameters this component wants to pass to the layout manager of its parent
     * @return Parameters for the layout manager
     */
    Set<LayoutManager.Parameter> getLayoutManagerParameters();

    /**
     * Returns the container which is holding this component, or {@code null} if it's not assigned to anything.
     * @return Parent container or null
     */
    Container getParent();

    /**
     * Assigns the component to a new container. If the component already belongs to a different container, it will be
     * removed from there first. Calling {@code setParent(null)} is the same as removing the component from the
     * container.
     * @param parent New parent container or {@code null} if you want to remove the component from its current parent
     */
    void setParent(Container parent);

    /**
     * Removes the component from its parent and frees up any resources (threads, etc) associated with the component.
     * After this call, the component cannot be used anymore. It is not required to call dispose on components when you
     * want to remove them or want to close the window they are part of, this is in general cared for automatically.
     * Calling dispose() manually is only required for certain components (like AnimatedLabel) when you want to free up
     * resources like background threads. When you close a window, Lanterna will call dispose on all child components of
     * that window.
     */
    void dispose();
}
