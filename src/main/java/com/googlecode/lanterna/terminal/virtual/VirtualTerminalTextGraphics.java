/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.terminal.virtual;

import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.AbstractTextGraphics;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Implementation of {@link TextGraphics} for {@link VirtualTerminal}
 * @author Martin
 */
class VirtualTerminalTextGraphics extends AbstractTextGraphics {
    private final DefaultVirtualTerminal virtualTerminal;

    VirtualTerminalTextGraphics(DefaultVirtualTerminal virtualTerminal) {
        this.virtualTerminal = virtualTerminal;
    }

    @Override
    public TextGraphics setCharacter(int column, int row, TextCharacter textCharacter) {
        TerminalSize size = getSize();
        TerminalRectangle.whenContains(0, 0, size.columns, size.rows, column, row, () -> {
            synchronized (virtualTerminal) {
                virtualTerminal.setCursorPosition(TerminalPosition.of(column, row));
                virtualTerminal.putCharacter(textCharacter);
            }
        });
        return this;
    }

    @Override
    public TextCharacter getCharacter(TerminalPosition position) {
        return virtualTerminal.getCharacter(position);
    }

    @Override
    public TextCharacter getCharacter(int column, int row) {
        return getCharacter(TerminalPosition.of(column, row));
    }

    @Override
    public TerminalSize getSize() {
        return virtualTerminal.getTerminalSize();
    }
}
