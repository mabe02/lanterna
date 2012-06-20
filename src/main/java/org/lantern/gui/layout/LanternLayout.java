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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui.layout;

import java.util.List;
import org.lantern.gui.Component;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public interface LanternLayout
{
    public void addComponent(Component component, Object modifiers);
    public void removeComponent(Component component);

    public TerminalSize getPreferredSize();

    public List<LaidOutComponent> layout(TerminalSize layoutArea);

    public void setPadding(int paddingSize);

    public interface LaidOutComponent
    {
        public Component getComponent();
        public TerminalSize getSize();
        public TerminalPosition getTopLeftPosition();
    }
}
