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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import java.util.List;

/**
 *
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
}
