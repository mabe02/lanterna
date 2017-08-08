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
package com.googlecode.lanterna.graphics;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

/**
 * Simple implementation of TextImage that keeps the content as a two-dimensional TextCharacter array. Copy operations
 * between two BasicTextImage classes are semi-optimized by using System.arraycopy instead of iterating over each
 * character and copying them over one by one.
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
            Arrays.fill(line, character);
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

        // Double width character adjustments
        if(column > 0 && buffer[row][column - 1].isDoubleWidth()) {
            buffer[row][column - 1] = buffer[row][column - 1].withCharacter(' ');
        }

        // Assign the character at location we specified
        buffer[row][column] = character;

        // Double width character adjustments
        if(character.isDoubleWidth() && column + 1 < buffer[0].length) {
            buffer[row][column+1] = character.withCharacter(' ');
        }
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
            destinationRowOffset += -startRowIndex;
            rows += startRowIndex;
            startRowIndex = 0;
        }

        // If the destination offset is negative, adjust the source start indexes
        if(destinationColumnOffset < 0) {
            startColumnIndex -= destinationColumnOffset;
            columns += destinationColumnOffset;
            destinationColumnOffset = 0;
        }
        if(destinationRowOffset < 0) {
            startRowIndex -= destinationRowOffset;
            rows += destinationRowOffset;
            destinationRowOffset = 0;
        }

        //Make sure we can't copy more than is available
        columns = Math.min(buffer[0].length - startColumnIndex, columns);
        rows = Math.min(buffer.length - startRowIndex, rows);

        //Adjust target lengths as well
        columns = Math.min(destination.getSize().getColumns() - destinationColumnOffset, columns);
        rows = Math.min(destination.getSize().getRows() - destinationRowOffset, rows);

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
            public TextCharacter getCharacter(int column, int row) {
                return BasicTextImage.this.getCharacterAt(column, row);
            }

            @Override
            public TerminalSize getSize() {
                return size;
            }
        };
    }

    private TextCharacter[] newBlankLine() {
        TextCharacter[] line = new TextCharacter[size.getColumns()];
        Arrays.fill(line, TextCharacter.DEFAULT_CHARACTER);
        return line;
    }

    @Override
    public void scrollLines(int firstLine, int lastLine, int distance) {
        if (firstLine < 0) { firstLine = 0; }
        if (lastLine >= size.getRows()) { lastLine = size.getRows() - 1; }
        if (firstLine < lastLine) {
            if (distance > 0) {
                // scrolling up: start with first line as target:
                int curLine = firstLine;
                // copy lines from further "below":
                for (; curLine <= lastLine - distance; curLine++) {
                    buffer[curLine] = buffer[curLine+distance];
                }
                // blank out the remaining lines:
                for (; curLine <= lastLine; curLine++) {
                    buffer[curLine] = newBlankLine();
                }
            }
            else if (distance < 0) {
               // scrolling down: start with last line as target:
               int curLine = lastLine; distance = -distance;
               // copy lines from further "above":
               for (; curLine >= firstLine + distance; curLine--) {
                   buffer[curLine] = buffer[curLine-distance];
               }
               // blank out the remaining lines:
               for (; curLine >= firstLine; curLine--) {
                   buffer[curLine] = newBlankLine();
               }
           } /* else: distance == 0 => no-op */
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(size.getRows()*(size.getColumns()+1)+50);
        sb.append('{').append(size.getColumns()).append('x').append(size.getRows()).append('}').append('\n');
        for (TextCharacter[] line : buffer) {
            for (TextCharacter tc : line) {
                sb.append(tc.getCharacter());
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
