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
 * Layout manager that places components where they are manually specified to be and sizes them to the size they are 
 * manually assigned to. When using the AbsoluteLayout, please use setPosition(..) and setSize(..) manually on each
 * component to choose where to place them. Components that have not had their position and size explicitly set will
 * not be visible.
 *
 * @author martin
 */
public class AbsoluteLayout implements LayoutManager {
    @Override
    public TerminalSize getPreferredSize(List<Component> components) {
        TerminalSize size = TerminalSize.ZERO;
        for(Component component: components) {
            size = size.max(
                    new TerminalSize(
                            component.getPosition().getColumn() + component.getSize().getColumns(),
                            component.getPosition().getRow() + component.getSize().getRows()));
                    
        }
        return size;
    }

    @Override
    public void doLayout(TerminalSize area, List<Component> components) {
        //Do nothing
    }

    @Override
    public boolean hasChanged() {
        return false;
    }
}
