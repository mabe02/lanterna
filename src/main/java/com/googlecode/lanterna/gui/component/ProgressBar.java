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
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class ProgressBar extends AbstractComponent
{
    private final int preferredWidth;
    private double progress;

    public ProgressBar(int preferredWidth)
    {
        this.preferredWidth = preferredWidth;
        this.progress = 0.0;
    }

    public TerminalSize getPreferredSize()
    {
        return new TerminalSize(preferredWidth, 1);
    }

    public void repaint(TextGraphics graphics)
    {
        int totalWidth = graphics.getWidth();
        int highlightedBlocks = (int)(totalWidth * progress);
        graphics.applyTheme(Category.ListItemSelected);
        graphics.fillRectangle(ACS.BLOCK_SOLID, new TerminalPosition(0, 0), new TerminalSize(highlightedBlocks, 1));
        graphics.applyTheme(Category.ListItem);
        graphics.fillRectangle(ACS.BLOCK_SOLID, new TerminalPosition(highlightedBlocks, 0), new TerminalSize(totalWidth - highlightedBlocks, 1));
    }

    public double getProgress()
    {
        return progress;
    }

    public void setProgress(double progress)
    {
        this.progress = progress;
    }
}
