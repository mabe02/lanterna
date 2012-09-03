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

package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class EmptySpace extends AbstractComponent
{
    private final TerminalSize size;

    public EmptySpace()
    {
        this(1, 1);
    }

    public EmptySpace(final int width, final int height)
    {
        this.size = new TerminalSize(width, height);
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        return size;
    }

    @Override
    public void repaint(TextGraphics graphics)
    {
        graphics.applyTheme(Category.DIALOG_AREA);
        graphics.fillArea(' ');
    }
}
