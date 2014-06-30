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
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author martin
 */
class TextBuffer {
    private final int backlog;
    private final LinkedList<List<TerminalCharacter>> lineBuffer;
    private final TerminalCharacter fillCharacter;

    TextBuffer(int backlog, TerminalSize initialSize) {
        this(backlog, initialSize, TerminalCharacter.DEFAULT_CHARACTER);
    }

    TextBuffer(int backlog, TerminalSize initialSize, TerminalCharacter fillCharacter) {
        this.backlog = backlog;
        this.lineBuffer = new LinkedList<List<TerminalCharacter>>();
        this.fillCharacter = fillCharacter;

        //Initialize the content to empty
        for(int y = 0; y < initialSize.getRows(); y++) {
            lineBuffer.add(newLine(initialSize.getColumns(), fillCharacter));
        }
    }

    private List<TerminalCharacter> newLine(int width, TerminalCharacter character) {
        List<TerminalCharacter> row = new ArrayList<TerminalCharacter>();
        for(int x = 0; x < width; x++) {
            row.add(character);
        }
        return row;
    }

    void readjust(int columns, int rows) {
        while(lineBuffer.size() < rows) {
            newLine(columns);
        }
        if(lineBuffer.isEmpty()) {
            return;
        }
        for(List<TerminalCharacter> line: lineBuffer) {
            readjust(line, columns);
        }

        //Fill the buffer height if necessary
        while(rows > lineBuffer.size()) {
            lineBuffer.addFirst(newLine(columns, fillCharacter));
        }
        //Cut down history, if necessary
        while(lineBuffer.size() - rows > backlog) {
            lineBuffer.removeFirst();
        }
    }

    List<List<TerminalCharacter>> getVisibleLines(int rows, int scrollOffset) {
        int lastIndex = lineBuffer.size() - scrollOffset;
        int firstIndex = lastIndex - rows;
        if(firstIndex < 0) {
            System.err.println("Error, firstIndex is above the top by " + firstIndex + " rows (" + rows + " -> " + scrollOffset + " -> " + lineBuffer.size() + ")");
            lastIndex = 0 - firstIndex;
            firstIndex = 0 - firstIndex;
        }
        if(lastIndex > lineBuffer.size()) {
            System.err.println("Error, lastIndex shot through the bottom by " + (lineBuffer.size() - lastIndex) + " rows");
            lastIndex = lineBuffer.size();
        }
        return lineBuffer.subList(firstIndex, lastIndex);
    }
    
    int getNumberOfLines() {
        return lineBuffer.size();
    }

    void newLine(TerminalSize terminalSize) {
        newLine(terminalSize.getColumns());
        readjust(terminalSize.getColumns(), terminalSize.getRows());
    }
    
    private void newLine(int columns) {
        lineBuffer.addLast(newLine(columns, fillCharacter));
    }
    
    void setCharacter(TerminalSize terminalSize, TerminalPosition currentPosition, TerminalCharacter terminalCharacter) {
        List<TerminalCharacter> line = lineBuffer.get(lineBuffer.size() - terminalSize.getRows() + currentPosition.getRow());
        readjust(line, terminalSize.getColumns());
        line.set(currentPosition.getColumn(), terminalCharacter);
        
        //Pad CJK character with a trailing space
        if(CJKUtils.isCharCJK(terminalCharacter.getCharacter()) && currentPosition.getColumn() + 1 < line.size()) {
            line.set(currentPosition.getColumn() + 1, terminalCharacter.withCharacter(' '));
        }
        //If there's a CJK character immediately to our left, reset it
        if(currentPosition.getColumn() > 0 && CJKUtils.isCharCJK(line.get(currentPosition.getColumn() - 1).getCharacter())) {
            line.set(currentPosition.getColumn() - 1, line.get(currentPosition.getColumn() - 1).withCharacter(' '));
        }
    }

    private void readjust(List<TerminalCharacter> line, int columns) {
        for(int i = 0; i < columns - line.size(); i++) {
            line.add(fillCharacter);
        }
        for(int i = 0; i < line.size() - columns; i++) {
            line.remove(line.size() - 1);
        }
    }
}
