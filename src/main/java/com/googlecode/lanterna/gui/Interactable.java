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

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;

/**
 *
 * @author Martin
 */
public interface Interactable
{
    public void keyboardInteraction(Key key, InteractableResult result);
    public void onEnterFocus(FocusChangeDirection direction);
    public void onLeaveFocus(FocusChangeDirection direction);
    public TerminalPosition getHotspot();

    public enum Result
    {
        DO_NOTHING,
        NEXT_INTERACTABLE,
        PREVIOUS_INTERACTABLE
    }

    public enum FocusChangeDirection
    {
        DOWN_OR_RIGHT,
        UP_OR_LEFT
    }
}
