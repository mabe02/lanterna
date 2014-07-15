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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.screen;
import com.googlecode.lanterna.common.TextCharacter;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * This is a helper class to assist you in composing your output on a {@code Screen}. It provides methods for drawing
 * full strings as well as keeping a color and modifier state so that you don't have to specify them for every operation.
 * It also has a position state which moves as you as putting characters, so you can think of this as a pen.
 * @author Martin
 */
class ScreenTextGraphics extends com.googlecode.lanterna.common.AbstractTextGraphics {
    private final Screen screen;

    ScreenTextGraphics(Screen screen) {
        super();
        this.screen = screen;
    }

    @Override
    protected void setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter) {
        //Let the screen do culling
        screen.setCharacter(columnIndex, rowIndex, textCharacter);
    }

    @Override
    public TerminalSize getWritableArea() {
        return screen.getTerminalSize();
    }
}
