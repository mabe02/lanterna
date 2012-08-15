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
 *
 * @author Martin
 */
public interface ContainerLayout
{
    void addComponent(Component component, Object modifiers);
    void removeComponent(Component component);

    TerminalSize getPreferredSize();

    List<LaidOutComponent> layout(TerminalSize layoutArea);

    void setPadding(int paddingSize);

    boolean maximisesVertically();
    boolean maximisesHorisontally();

    public interface LaidOutComponent
    {
        Component getComponent();
        TerminalSize getSize();
        TerminalPosition getTopLeftPosition();
    }
}
