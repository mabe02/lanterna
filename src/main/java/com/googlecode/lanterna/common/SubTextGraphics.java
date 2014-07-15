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
package com.googlecode.lanterna.common;

import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * Created by martin on 15/07/14.
 */
class SubTextGraphics extends AbstractTextGraphics {
    private final AbstractTextGraphics underlyingTextGraphics;
    private final TerminalPosition topLeft;
    private final TerminalSize writeableAreaSize;

    SubTextGraphics(AbstractTextGraphics underlyingTextGraphics, TerminalPosition topLeft, TerminalSize writeableAreaSize) {
        this.underlyingTextGraphics = underlyingTextGraphics;
        this.topLeft = topLeft;
        this.writeableAreaSize = writeableAreaSize;
    }

    @Override
    protected void setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter) {
        TerminalSize writableArea = getWritableArea();
        if(columnIndex < 0 || columnIndex >= writableArea.getColumns() ||
                rowIndex < 0 || rowIndex >= writableArea.getRows()) {
            return;
        }
        underlyingTextGraphics.setCharacter(columnIndex + topLeft.getColumn(), rowIndex + topLeft.getRow(), textCharacter);
    }

    @Override
    public TerminalSize getWritableArea() {
        return writeableAreaSize;
    }
}
