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

    void readjust(int width, int height) {
        int bufferWidth = lineBuffer.getLast().size();

        if(width > bufferWidth) {
            for(List<TerminalCharacter> line: lineBuffer) {
                for(int i = 0; i < width - bufferWidth; i++) {
                    line.add(fillCharacter);
                }
            }
        }
        else if(width < bufferWidth) {
            for(List<TerminalCharacter> line: lineBuffer) {
                for(int i = 0; i < bufferWidth - width; i++) {
                    line.remove(line.size() - 1);
                }
            }
        }

        //Fill the buffer height if necessary
        while(height > lineBuffer.size()) {
            lineBuffer.addFirst(newLine(width, fillCharacter));
        }
        //Cut down history, if necessary
        while(lineBuffer.size() - height > backlog) {
            lineBuffer.removeFirst();
        }
    }

    List<List<TerminalCharacter>> getVisibleLines(int rows, int scrollOffset) {
        return lineBuffer.subList(lineBuffer.size() - rows, lineBuffer.size());
    }

    void newLine(TerminalSize terminalSize) {
        lineBuffer.addLast(newLine(terminalSize.getColumns(), fillCharacter));
        readjust(terminalSize.getColumns(), terminalSize.getRows());
    }
    
    void setCharacter(TerminalSize terminalSize, TerminalPosition currentPosition, TerminalCharacter terminalCharacter) {
        lineBuffer.get(lineBuffer.size() - terminalSize.getRows() + currentPosition.getRow()).set(currentPosition.getColumn(), terminalCharacter);
    }
}
