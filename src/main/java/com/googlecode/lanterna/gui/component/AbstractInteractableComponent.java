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

import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.terminal.TerminalPosition;

/**
 *
 * @author Martin
 */
public abstract class AbstractInteractableComponent extends AbstractComponent implements Interactable
{
    private boolean hasFocus;
    private TerminalPosition hotspot;

    public AbstractInteractableComponent()
    {
        hotspot = new TerminalPosition(0, 0);
    }

    public final void onEnterFocus(FocusChangeDirection direction)
    {
        hasFocus = true;
        afterEnteredFocus(direction);
    }

    public final void onLeaveFocus(FocusChangeDirection direction)
    {
        hasFocus = false;
        afterLeftFocus(direction);
    }

    protected void afterEnteredFocus(FocusChangeDirection direction)
    {
    }

    protected void afterLeftFocus(FocusChangeDirection direction)
    {
    }

    public boolean hasFocus()
    {
        return hasFocus;
    }

    public TerminalPosition getHotspot()
    {
        return hotspot;
    }

    protected void setHotspot(TerminalPosition point)
    {
        this.hotspot = point;
    }
}
