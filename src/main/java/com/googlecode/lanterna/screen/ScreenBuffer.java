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

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * Defines a buffer used by AbstractScreen and its subclasses to keep its state of what's currently displayed and what 
 * the edit buffer looks like. A ScreenBuffer is essentially a two-dimensional array of TextCharacter with some utility
 * methods to inspect and manipulate it in a safe way.
 * @author martin
 */
public class ScreenBuffer {
    private final TextCharacter[][] buffer;
    
    /**
     * Creates a new ScreenBuffer with a given size and a TextCharacter to initially fill it with
     * @param size Size of the buffer
     * @param filler What character to set as the initial content of the buffer
     */
    public ScreenBuffer(TerminalSize size, TextCharacter filler) {
        this(size, new TextCharacter[0][], filler);
    }    
    
    /**
     * Creates a new ScreenBuffer by copying a region of a two-dimensional array of TextCharacter:s. If the area to be 
     * copied to larger than the source array, a filler character is used.
     * @param size Size to create the new ScreenBuffer as (and size to copy from the array)
     * @param toCopy Array to copy initial data from
     * @param filler Filler character to use if the source array is smaller than the requested size
     */
    private ScreenBuffer(TerminalSize size, TextCharacter[][] toCopy, TextCharacter filler) {
        if(size == null || toCopy == null || filler == null) {
            throw new IllegalArgumentException("Cannot create ScreenBuffer with null " +
                    (size == null ? "size" : (toCopy == null ? "toCopy" : "filler")));
        }
        int rows = size.getRows();
        int columns = size.getColumns();
        buffer = new TextCharacter[rows][];
        for(int y = 0; y < rows; y++) {
            buffer[y] = new TextCharacter[columns];
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
    
    /**
     * Sets the entire buffer content to one specified character
     * @param character The character to set the buffer to
     */
    public void setAll(TextCharacter character) {
        if(character == null) {
            throw new IllegalArgumentException("Cannot call ScreenBuffer.setAll(..) with null character");
        }
        for(TextCharacter[] line : buffer) {
            for(int x = 0; x < line.length; x++) {
                line[x] = character;
            }
        }
    }

    /**
     * Returns a copy of this buffer resized to a new size and using a specified filler character if the new size is 
     * larger and we need to fill in empty areas. The copy will be independent from the one this method is invoked on,
     * so modifying one will not affect the other.
     * @param newSize Size of the new buffer
     * @param filler Filler character to use on the new areas when enlarging the buffer (is not used when shrinking)
     * @return Copy of this buffer, but resized
     */
    public ScreenBuffer resize(TerminalSize newSize, TextCharacter filler) {
        if(newSize == null || filler == null) {
            throw new IllegalArgumentException("Cannot resize ScreenBuffer with null " +
                    (newSize == null ? "newSize" : "filler"));
        }
        if(newSize.getRows() == buffer.length &&
                (buffer.length == 0 || newSize.getColumns() == buffer[0].length)) {
            return this;
        }
        return new ScreenBuffer(newSize, buffer, filler);
    }
    
    /**
     * Sets the character at a specific position in the buffer to a particular TextCharacter. If the position is outside
     * of the buffers size, this method does nothing.
     * @param column Column coordinate of the character
     * @param row Row coordinate of the character
     * @param screenCharacter What TextCharacter to assign at the specified position
     */
    public void setCharacterAt(int column, int row, TextCharacter screenCharacter) {
        if(screenCharacter == null) {
            throw new IllegalArgumentException("Cannot call ScreenBuffer.setCharacterAt(..) with null character");
        }
        if(column < 0 || row < 0 || row >= buffer.length || column >= buffer[0].length) {
            return;
        }
        
        buffer[row][column] = screenCharacter;
    }

    /**
     * Returns the character stored at a particular position in this buffer
     * @param position Position to retrieve the character from
     * @return TextCharacter stored at the specified position
     */
    public TextCharacter getCharacterAt(TerminalPosition position) {
        return getCharacterAt(position.getColumn(), position.getRow());
    }
    
    /**
     * Returns the character stored at a particular position in this buffer
     * @param column Column coordinate of the character
     * @param row Row coordinate of the character
     * @return TextCharacter stored at the specified position
     */
    public TextCharacter getCharacterAt(int column, int row) {
        if(column < 0 || row < 0 || row >= buffer.length || column >= buffer[0].length) {
            return null;
        }
        
        return buffer[row][column];
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

    /**
     * Copies this ScreenBuffer content to another ScreenBuffer. If the destination ScreenBuffer is larger than this 
     * ScreenBuffer, the areas outside of the area that is written to will be untouched.
     * @param destination ScreenBuffer to copy to
     */
    public void copyTo(ScreenBuffer destination) {
        copyTo(destination, 0, buffer.length, 0, buffer[0].length, 0, 0);
    }

    /**
     * Copies this ScreenBuffer content to another ScreenBuffer. If the destination ScreenBuffer is larger than this 
     * ScreenBuffer, the areas outside of the area that is written to will be untouched.
     * @param destination ScreenBuffer to copy to
     * @param startRowIndex Which row in this buffer to copy from
     * @param rows How many rows to copy
     * @param startColumnIndex Which column in this buffer to copy from
     * @param columns How many columns to copy
     * @param destinationRowOffset Offset (in number of rows) in the target buffer where we want to first copied row to be
     * @param destinationColumnOffset Offset (in number of columns) in the target buffer where we want to first copied column to be
     */
    public void copyTo(
            ScreenBuffer destination,
            int startRowIndex,
            int rows,
            int startColumnIndex,
            int columns,
            int destinationRowOffset,
            int destinationColumnOffset) {

        for(int y = startRowIndex; y < startRowIndex + rows; y++) {
            System.arraycopy(buffer[y], startColumnIndex, destination.buffer[y - startRowIndex + destinationRowOffset], destinationColumnOffset, columns);
        }
    }
}
