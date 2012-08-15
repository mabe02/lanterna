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
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.List;

/**
 *
 * @author Martin
 */
public class BorderLayout implements LayoutManager {

    @Override
    public void addComponent(Component component, LayoutParameter... parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeComponent(Component component) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TerminalSize getPreferredSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<? extends LaidOutComponent> layout(TerminalSize layoutArea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean maximisesVertically() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean maximisesHorisontally() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
