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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.screen;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.AbstractTextGraphics;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * This is an implementation of TextGraphics that targets the output to a Screen. The ScreenTextGraphics object is valid
 * after screen resizing.
 * @author Martin
 */
class ScreenTextGraphics extends AbstractTextGraphics {
    private final Screen screen;

    /**
     * Creates a new {@code ScreenTextGraphics} targeting the specified screen
     * @param screen Screen we are targeting
     */
    ScreenTextGraphics(Screen screen) {
        super();
        this.screen = screen;
    }

    @Override
    public TextGraphics setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter) {
        //Let the screen do culling
        screen.setCharacter(columnIndex, rowIndex, textCharacter);
        return this;
    }

    @Override
    public TextCharacter getCharacter(int column, int row) {
        return screen.getBackCharacter(column, row);
    }

    @Override
    public TerminalSize getSize() {
        return screen.getTerminalSize();
    }
}
