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
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

/**
 * Simple implementation of TextImage that keeps the content as a two-dimensional TextCharacter array. If you copy 
 * between two BasicTextImage:s, they can use {@code System.arrayCopy} to make the operation faster(?).
 * @author martin
 */
public class BasicTextImage implements TextImage {
    private final TerminalSize size;
    private final TextCharacter[][] buffer;
    
    /**
     * Creates a new BasicTextImage with the specified size and fills it initially with space characters using the 
     * default foreground and background color
     * @param columns Size of the image in number of columns
     * @param rows Size of the image in number of rows
     */
    public BasicTextImage(int columns, int rows) {
        this(new TerminalSize(columns, rows));
    }
    
    /**
     * Creates a new BasicTextImage with the specified size and fills it initially with space characters using the 
     * default foreground and background color
     * @param size Size to make the image
     */
    public BasicTextImage(TerminalSize size) {
        this(size, new TextCharacter(' ', TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT));
    }
    
    /**
     * Creates a new BasicTextImage with a given size and a TextCharacter to initially fill it with
     * @param size Size of the image
     * @param initialContent What character to set as the initial content
     */
    public BasicTextImage(TerminalSize size, TextCharacter initialContent) {
        this(size, new TextCharacter[0][], initialContent);
    }    
    
    /**
     * Creates a new BasicTextImage by copying a region of a two-dimensional array of TextCharacter:s. If the area to be 
     * copied to larger than the source array, a filler character is used.
     * @param size Size to create the new BasicTextImage as (and size to copy from the array)
     * @param toCopy Array to copy initial data from
     * @param initialContent Filler character to use if the source array is smaller than the requested size
     */
    private BasicTextImage(TerminalSize size, TextCharacter[][] toCopy, TextCharacter initialContent) {
        if(size == null || toCopy == null || initialContent == null) {
            throw new IllegalArgumentException("Cannot create BasicTextImage with null " +
                    (size == null ? "size" : (toCopy == null ? "toCopy" : "filler")));
        }
        this.size = size;
        
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
                    buffer[y][x] = initialContent;
                }
            }
        }
    }

    @Override
    public TerminalSize getSize() {
        return size;
    }
    
    @Override
    public void setAll(TextCharacter character) {
        if(character == null) {
            throw new IllegalArgumentException("Cannot call BasicTextImage.setAll(..) with null character");
        }
        for(TextCharacter[] line : buffer) {
            for(int x = 0; x < line.length; x++) {
                line[x] = character;
            }
        }
    }

    @Override
    public BasicTextImage resize(TerminalSize newSize, TextCharacter filler) {
        if(newSize == null || filler == null) {
            throw new IllegalArgumentException("Cannot resize BasicTextImage with null " +
                    (newSize == null ? "newSize" : "filler"));
        }
        if(newSize.getRows() == buffer.length &&
                (buffer.length == 0 || newSize.getColumns() == buffer[0].length)) {
            return this;
        }
        return new BasicTextImage(newSize, buffer, filler);
    }

    @Override
    public void setCharacterAt(TerminalPosition position, TextCharacter character) {
        if(position == null) {
            throw new IllegalArgumentException("Cannot call BasicTextImage.setCharacterAt(..) with null position");
        }
        setCharacterAt(position.getColumn(), position.getRow(), character);
    }
    
    @Override
    public void setCharacterAt(int column, int row, TextCharacter character) {
        if(character == null) {
            throw new IllegalArgumentException("Cannot call BasicTextImage.setCharacterAt(..) with null character");
        }
        if(column < 0 || row < 0 || row >= buffer.length || column >= buffer[0].length) {
            return;
        }
        
        buffer[row][column] = character;
    }

    @Override
    public TextCharacter getCharacterAt(TerminalPosition position) {
        if(position == null) {
            throw new IllegalArgumentException("Cannot call BasicTextImage.getCharacterAt(..) with null position");
        }
        return getCharacterAt(position.getColumn(), position.getRow());
    }
    
    @Override
    public TextCharacter getCharacterAt(int column, int row) {
        if(column < 0 || row < 0 || row >= buffer.length || column >= buffer[0].length) {
            return null;
        }
        
        return buffer[row][column];
    }
    
    @Override
    public void copyTo(TextImage destination) {
        copyTo(destination, 0, buffer.length, 0, buffer[0].length, 0, 0);
    }

    @Override
    public void copyTo(
            TextImage destination,
            int startRowIndex,
            int rows,
            int startColumnIndex,
            int columns,
            int destinationRowOffset,
            int destinationColumnOffset) {

        // If the source image position is negative, offset the whole image
        if(startColumnIndex < 0) {
            destinationColumnOffset += -startColumnIndex;
            columns += startColumnIndex;
            startColumnIndex = 0;
        }
        if(startRowIndex < 0) {
            startRowIndex += -startRowIndex;
            rows = startRowIndex;
            startRowIndex = 0;
        }
        //Make sure we can't copy more than is available
        columns = Math.min(buffer[0].length - startColumnIndex, columns);
        rows = Math.min(buffer.length - startRowIndex, rows);

        //Adjust target lengths as well
        columns = Math.min(destination.getSize().getColumns() - startColumnIndex, columns);
        rows = Math.min(destination.getSize().getRows() - startRowIndex, rows);

        if(columns <= 0 || rows <= 0) {
            return;
        }

        TerminalSize destinationSize = destination.getSize();
        if(destination instanceof BasicTextImage) {
            int targetRow = destinationRowOffset;
            for(int y = startRowIndex; y < startRowIndex + rows && targetRow < destinationSize.getRows(); y++) {
                System.arraycopy(buffer[y], startColumnIndex, ((BasicTextImage)destination).buffer[targetRow++], destinationColumnOffset, columns);
            }
        }
        else {
            //Manually copy character by character
            for(int y = startRowIndex; y < startRowIndex + rows; y++) {
                for(int x = startColumnIndex; x < startColumnIndex + columns; x++) {
                    destination.setCharacterAt(
                            x - startColumnIndex + destinationColumnOffset, 
                            y - startRowIndex + destinationRowOffset, 
                            buffer[y][x]);
                }
            }
        }
    }

    @Override
    public TextGraphics newTextGraphics() {
        return new AbstractTextGraphics() {
            @Override
            public TextGraphics setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter) {
                BasicTextImage.this.setCharacterAt(columnIndex, rowIndex, textCharacter);
                return this;
            }

            @Override
            public TerminalSize getSize() {
                return size;
            }
        };
    }
}
