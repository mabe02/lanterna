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
package com.googlecode.lanterna.input;

import com.googlecode.lanterna.TerminalPosition;

/**
 * ScreenInfoAction, a KeyStroke in disguise, this class contains the reported position of the screen cursor.
 */
public class ScreenInfoAction extends KeyStroke {
    private final TerminalPosition position;

    /**
     * Constructs a ScreenInfoAction based on a location on the screen
     * @param position the TerminalPosition reported from terminal
     */
    public ScreenInfoAction(TerminalPosition position) {
        super(KeyType.CursorLocation);
        this.position = position;
    }

    /**
     * The location of the mouse cursor when this event was generated.
     * @return Location of the mouse cursor
     */
    public TerminalPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "ScreenInfoAction{position=" + position + '}';
    }
}
