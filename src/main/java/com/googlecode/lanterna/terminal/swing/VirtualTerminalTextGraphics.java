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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.graphics.AbstractTextGraphics;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Implementation of TextGraphics for the SwingTerminal, which is able to access directly into the TextBuffer and set
 * values in there directly.
 * @author Martin
 */
class VirtualTerminalTextGraphics extends AbstractTextGraphics {
    private final VirtualTerminal virtualTerminal;

    VirtualTerminalTextGraphics(VirtualTerminal virtualTerminal) {
        this.virtualTerminal = virtualTerminal;
    }

    @Override
    public TextGraphics setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter) {
        TerminalSize size = getSize();
        if(columnIndex < 0 || columnIndex >= size.getColumns() ||
                rowIndex < 0 || rowIndex >= size.getRows()) {
            return this;
        }
        virtualTerminal.setCursorAndPutCharacter(new TerminalPosition(columnIndex, rowIndex), textCharacter);
        return this;
    }

    @Override
    public TerminalSize getSize() {
        return virtualTerminal.getSize();
    }
}
