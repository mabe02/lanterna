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
import java.util.List;

/**
 * A layout manager is a class that takes an area of usable space and a list of components to fit on that space. This
 * is very similar to how AWT/Swing/SWT works. Lanterna contains a number of layout managers built-in that will arrange
 * components in various ways, but you can also write your own. The typical way of providing customization and tuning,
 * so the layout manager can distinguish between components and treat them in different ways, is to create a class
 * and/or objects based on the {@code LayoutData} object, which can be assigned to each {@code Component}.
 * @see AbsoluteLayout
 * @see BorderLayout
 * @see GridLayout
 * @see LinearLayout
 * @author Martin
 */
public interface LayoutManager {

    /**
     * This method returns the dimensions it would prefer to have to be able to layout all components while giving all
     * of them as much space as they are asking for.
     * @param components List of components
     * @return Size the layout manager would like to have
     */
    TerminalSize getPreferredSize(List<Component> components);

    /**
     * Given a size constraint, update the location and size of each component in the component list by laying them out
     * in the available area. This method will call {@code setPosition(..)} and {@code setSize(..)} on the Components.
     * @param area Size available to this layout manager to lay out the components on
     * @param components List of components to lay out
     */
    void doLayout(TerminalSize area, List<Component> components);

    /**
     * Returns true if the internal state of this LayoutManager has changed since the last call to doLayout. This will
     * tell the container that it needs to call doLayout again.
     * @return {@code true} if this layout manager's internal state has changed since the last call to {@code doLayout}
     */
    boolean hasChanged();
}
