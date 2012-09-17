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

/**
 *
 * @author Martin
 */
class DefaultLaidOutComponent implements LayoutManager.LaidOutComponent {
    final Component component;
    final TerminalSize size;
    final TerminalPosition topLeftPosition;

    public DefaultLaidOutComponent(Component component, TerminalSize size, TerminalPosition topLeftPosition) {
        this.component = component;
        this.size = size;
        this.topLeftPosition = topLeftPosition;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public TerminalSize getSize() {
        return size;
    }

    @Override
    public TerminalPosition getTopLeftPosition() {
        return topLeftPosition;
    }

    @Override
    public String toString() {
        return "[" + component + " @ " + topLeftPosition + " size " + size + "]";
    }
    
}
