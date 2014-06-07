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

import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author martin
 */
class ScreenBuffer {
    private final ScreenCharacter[][] buffer;
    
    public ScreenBuffer(TerminalSize size, ScreenCharacter filler) {
        this(size, new ScreenCharacter[0][], filler);
    }    
    
    private ScreenBuffer(TerminalSize size, ScreenCharacter[][] toCopy, ScreenCharacter filler) {
        int rows = size.getRows();
        int columns = size.getColumns();
        buffer = new ScreenCharacter[rows][];
        for(int y = 0; y < rows; y++) {
            buffer[y] = new ScreenCharacter[columns];
            for(int x = 0; x < columns; x++) {
                if(y < toCopy.length && x < toCopy[y].length) {
                    buffer[y][x] = toCopy[y][x];
                }
                else {
                    buffer[y][x] = filler;
                }
            }
        }
    }
    
    void setAll(ScreenCharacter character) {
        for(ScreenCharacter[] line : buffer) {
            for(int x = 0; x < line.length; x++) {
                line[x] = character;
            }
        }
    }

    ScreenBuffer resize(TerminalSize pendingResize, ScreenCharacter filler) {
        if(pendingResize.getRows() == buffer.length &&
                (buffer.length == 0 || pendingResize.getColumns() == buffer[0].length)) {
            return this;
        }
        return new ScreenBuffer(pendingResize, buffer, filler);
    }

    
    void setCharacterAt(int column, int row, ScreenCharacter screenCharacter) {
        if(column < 0 || row < 0 || row >= buffer.length || column >= buffer[0].length) {
            return;
        }
        
        buffer[row][column] = screenCharacter;
    }
    
    void setCharacterAt(TerminalPosition position, ScreenCharacter screenCharacter) {
        setCharacterAt(position.getColumn(), position.getRow(), screenCharacter);
    }

    ScreenCharacter getCharacterAt(TerminalPosition position) {
        return getCharacterAt(position.getColumn(), position.getRow());
    }
    
    ScreenCharacter getCharacterAt(int x, int y) {
        if(x < 0 || y < 0 || y >= buffer.length || x >= buffer[0].length) {
            return null;
        }
        
        return buffer[y][x];
    }
    
    boolean isVeryDifferent(ScreenBuffer other, int threshold) {
        int differences = 0;
        for(int y = 0; y < buffer.length; y++) {
            for(int x = 0; x < buffer[0].length; x++) {
                if(!buffer[y][x].equals(other.buffer[y][x])) {
                    if(++differences >= threshold) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void copyTo(ScreenBuffer destination) {
        for(int y = 0; y < buffer.length; y++) {
            System.arraycopy(buffer[y], 0, destination.buffer[y], 0, buffer[0].length);
        }
    }
}
