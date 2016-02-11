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

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Contains an entire text buffer used by Swing terminal
 * @author martin
 */
class TextBuffer {
    private final int backlog;
    private final LinkedList<List<TextCharacter>> lineBuffer;
    private final TextCharacter fillCharacter;

    TextBuffer(int backlog) {
        this.backlog = backlog;
        this.lineBuffer = new LinkedList<List<TextCharacter>>();
        this.fillCharacter = TextCharacter.DEFAULT_CHARACTER;

        //Initialize the content to one line
        newLine();
    }

    void clear() {
        lineBuffer.clear();
        newLine();
    }

    void newLine() {
        ArrayList<TextCharacter> line = new ArrayList<TextCharacter>(200);
        line.add(fillCharacter);
        lineBuffer.addFirst(line);
    }


    Iterable<List<TextCharacter>> getVisibleLines(final int visibleRows, final int scrollOffset) {
        final int length = Math.min(visibleRows, lineBuffer.size());
        return new Iterable<List<TextCharacter>>() {
            @Override
            public Iterator<List<TextCharacter>> iterator() {
                return new Iterator<List<TextCharacter>>() {
                    private final ListIterator<List<TextCharacter>> listIterator = lineBuffer.subList(scrollOffset, scrollOffset + length).listIterator(length);
                    @Override
                    public boolean hasNext() { return listIterator.hasPrevious(); }
                    @Override
                    public List<TextCharacter> next() { return listIterator.previous(); }
                    @Override
                    public void remove() { listIterator.remove(); }
                };
            }
        };
    }

    int getNumberOfLines() {
        return lineBuffer.size();
    }

    void trimBacklog(int terminalHeight) {
        while(lineBuffer.size() - terminalHeight > backlog) {
            lineBuffer.removeLast();
        }
    }

    void ensurePosition(TerminalSize terminalSize, TerminalPosition position) {
        getLine(terminalSize, position);
    }

    public TextCharacter getCharacter(TerminalSize terminalSize, TerminalPosition position) {
        return getLine(terminalSize, position).get(position.getColumn());
    }

    void setCharacter(TerminalSize terminalSize, TerminalPosition currentPosition, TextCharacter terminalCharacter) {
        List<TextCharacter> line = getLine(terminalSize, currentPosition);

        //If we are replacing a CJK character with a non-CJK character, make the following character empty
        if(TerminalTextUtils.isCharCJK(line.get(currentPosition.getColumn()).getCharacter()) &&
                !TerminalTextUtils.isCharCJK(terminalCharacter.getCharacter())) {
            line.set(currentPosition.getColumn() + 1, terminalCharacter.withCharacter(' '));
        }

        //Set the character in the buffer
        line.set(currentPosition.getColumn(), terminalCharacter);

        //Pad CJK character with a trailing space
        if(TerminalTextUtils.isCharCJK(terminalCharacter.getCharacter()) && currentPosition.getColumn() + 1 < line.size()) {
            ensurePosition(terminalSize, currentPosition.withRelativeColumn(1));
            line.set(currentPosition.getColumn() + 1, terminalCharacter.withCharacter(' '));
        }
        //If there's a CJK character immediately to our left, reset it
        if(currentPosition.getColumn() > 0 && TerminalTextUtils.isCharCJK(line.get(currentPosition.getColumn() - 1).getCharacter())) {
            line.set(currentPosition.getColumn() - 1, line.get(currentPosition.getColumn() - 1).withCharacter(' '));
        }
    }

    private List<TextCharacter> getLine(TerminalSize terminalSize, TerminalPosition position) {
        while(position.getRow() >= lineBuffer.size()) {
            newLine();
        }
        int lineIndex = Math.min(terminalSize.getRows(), lineBuffer.size()) - 1 - position.getRow();
        List<TextCharacter> line = lineBuffer.get(lineIndex);
        while(line.size() <= position.getColumn()) {
            line.add(fillCharacter);
        }
        return line;
    }
}
