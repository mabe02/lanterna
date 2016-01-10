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
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the internal state of the Swing terminal
 * @author martin
 */
class VirtualTerminal {

    private final TextBuffer mainTextBuffer;
    private final TextBuffer privateModeTextBuffer;
    private final TerminalScrollController terminalScrollController;

    private TextBuffer currentBuffer;
    private TerminalSize size;
    private TerminalPosition cursorPosition;
    
    //To avoid adding more synchronization and locking, we'll store a copy of all visible lines in this list. This is 
    //also the list we return (as an iterable) so it may not be reliable as each call to getLines will change it. This
    //isn't 100% safe but hopefully a good trade-off
    private final List<List<TextCharacter>> visibleLinesBuffer;

    VirtualTerminal(
            int backlog,
            TerminalSize initialSize,
            TerminalScrollController scrollController) {

        this.mainTextBuffer = new TextBuffer(backlog);
        this.privateModeTextBuffer = new TextBuffer(0);
        this.terminalScrollController = scrollController;

        this.currentBuffer = mainTextBuffer;
        this.size = initialSize;
        this.cursorPosition = TerminalPosition.TOP_LEFT_CORNER;
        
        this.visibleLinesBuffer = new ArrayList<List<TextCharacter>>(120);
    }

    void resize(TerminalSize newSize) {
        if(size.getRows() < newSize.getRows()) {
            cursorPosition = cursorPosition.withRelativeRow(newSize.getRows() - size.getRows());
        }
        this.size = newSize;
        updateScrollingController();
        correctCursor();
    }

    private void updateScrollingController() {
        int totalSize = Math.max(currentBuffer.getNumberOfLines(), size.getRows());
        int visibleSize = size.getRows();
        this.terminalScrollController.updateModel(totalSize, visibleSize);
    }

    TerminalSize getSize() {
        return size;
    }

    synchronized void setCursorPosition(TerminalPosition cursorPosition) {
        //Make sure the cursor position is within the bounds
        cursorPosition = cursorPosition.withColumn(
                Math.min(Math.max(cursorPosition.getColumn(), 0), size.getColumns() - 1));
        cursorPosition = cursorPosition.withRow(
                Math.min(Math.max(cursorPosition.getRow(), 0), size.getRows() - 1));

        currentBuffer.ensurePosition(size, cursorPosition);
        this.cursorPosition = cursorPosition;
        correctCursor();
    }

    TerminalPosition getTranslatedCursorPosition() {
        return cursorPosition.withRelativeRow(terminalScrollController.getScrollingOffset());
    }

    private void correctCursor() {
        this.cursorPosition =
                new TerminalPosition(
                        Math.min(cursorPosition.getColumn(), size.getColumns() - 1),
                        Math.min(cursorPosition.getRow(), size.getRows() - 1));
        this.cursorPosition =
                new TerminalPosition(
                        Math.max(cursorPosition.getColumn(), 0),
                        Math.max(cursorPosition.getRow(), 0));
    }

    synchronized TextCharacter getCharacter(TerminalPosition position) {
        return currentBuffer.getCharacter(size, position);
    }

    synchronized void putCharacter(TextCharacter terminalCharacter) {
        if(terminalCharacter.getCharacter() == '\n') {
            moveCursorToNextLine();
        }
        else if(terminalCharacter.getCharacter() == '\t') {
            int nrOfSpaces = TabBehaviour.ALIGN_TO_COLUMN_4.getTabReplacement(cursorPosition.getColumn()).length();
            for(int i = 0; i < nrOfSpaces && cursorPosition.getColumn() < size.getColumns() - 1; i++) {
                putCharacter(terminalCharacter.withCharacter(' '));
            }
        }
        else {
            currentBuffer.setCharacter(size, cursorPosition, terminalCharacter);

            //Advance cursor
            cursorPosition = cursorPosition.withRelativeColumn(TerminalTextUtils.isCharCJK(terminalCharacter.getCharacter()) ? 2 : 1);
            if(cursorPosition.getColumn() >= size.getColumns()) {
                moveCursorToNextLine();
            }
            currentBuffer.ensurePosition(size, cursorPosition);
        }
    }
    
    /**
     * Method that updates the cursor position and puts a character atomically. This method is here for thread safety.
     * The cursor position after this call will be the following position after the one specified.
     * @param cursorPosition Position to put the character at
     * @param terminalCharacter Character to put
     */
    synchronized void setCursorAndPutCharacter(TerminalPosition cursorPosition, TextCharacter terminalCharacter) {
        setCursorPosition(cursorPosition);
        putCharacter(terminalCharacter);
    }

    private void moveCursorToNextLine() {
        cursorPosition = cursorPosition.withColumn(0).withRelativeRow(1);
        if(cursorPosition.getRow() >= size.getRows()) {
            cursorPosition = cursorPosition.withRelativeRow(-1);
            if(currentBuffer == mainTextBuffer) {
                currentBuffer.newLine();
                currentBuffer.trimBacklog(size.getRows());
                updateScrollingController();
            }
        }
        currentBuffer.ensurePosition(size, cursorPosition);
    }

    void switchToPrivateMode() {
        currentBuffer = privateModeTextBuffer;
    }

    void switchToNormalMode() {
        currentBuffer = mainTextBuffer;
    }

    void clear() {
        currentBuffer.clear();
        setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
    }

    synchronized Iterable<List<TextCharacter>> getLines() {
        int scrollingOffset = terminalScrollController.getScrollingOffset();
        int visibleRows = size.getRows();
        //Make sure scrolling isn't too far off (can be sometimes when the terminal is being resized and the scrollbar
        //hasn't adjusted itself yet)
        if(currentBuffer.getNumberOfLines() > visibleRows &&
                scrollingOffset + visibleRows > currentBuffer.getNumberOfLines()) {
            scrollingOffset = currentBuffer.getNumberOfLines() - visibleRows;
        }
        
        visibleLinesBuffer.clear();
        for(List<TextCharacter> line: currentBuffer.getVisibleLines(visibleRows, scrollingOffset)) {
            visibleLinesBuffer.add(line);
        }
        return visibleLinesBuffer;
    }
}
