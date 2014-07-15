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
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.List;

/**
 *
 * @author martin
 */
class VirtualTerminal {

    private final TextBuffer mainTextBuffer;
    private final TextBuffer privateModeTextBuffer;
    private final TerminalScrollController terminalScrollController;

    private TextBuffer currentBuffer;
    private TerminalSize size;
    private TerminalPosition cursorPosition;

    public VirtualTerminal(
            int backlog,
            TerminalSize initialSize,
            TerminalScrollController scrollController) {

        this.mainTextBuffer = new TextBuffer(backlog);
        this.privateModeTextBuffer = new TextBuffer(0);
        this.terminalScrollController = scrollController;

        this.currentBuffer = mainTextBuffer;
        this.size = initialSize;
        this.cursorPosition = TerminalPosition.TOP_LEFT_CORNER;
    }

    public void resize(TerminalSize newSize) {
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

    public TerminalSize getSize() {
        return size;
    }

    public void setCursorPosition(TerminalPosition cursorPosition) {
        currentBuffer.ensurePosition(size, cursorPosition);
        this.cursorPosition = cursorPosition;
        correctCursor();
    }

    public TerminalPosition getTranslatedCursorPosition() {
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

    public void putCharacter(TerminalCharacter terminalCharacter) {
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
            cursorPosition = cursorPosition.withRelativeColumn(CJKUtils.isCharCJK(terminalCharacter.getCharacter()) ? 2 : 1);
            if(cursorPosition.getColumn() >= size.getColumns()) {
                moveCursorToNextLine();
            }
            currentBuffer.ensurePosition(size, cursorPosition);
        }
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

    public void switchToPrivateMode() {
        currentBuffer = privateModeTextBuffer;
    }

    public void switchToNormalMode() {
        currentBuffer = mainTextBuffer;
    }

    void clear() {
        currentBuffer.clear();
        setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
    }

    Iterable<List<TerminalCharacter>> getLines() {
        int scrollingOffset = terminalScrollController.getScrollingOffset();
        int visibleRows = size.getRows();
        //Make sure scrolling isn't too far off (can be sometimes when the terminal is being resized and the scrollbar
        //hasn't adjusted itself yet)
        if(currentBuffer.getNumberOfLines() > visibleRows &&
                scrollingOffset + visibleRows > currentBuffer.getNumberOfLines()) {
            scrollingOffset = currentBuffer.getNumberOfLines() - visibleRows;
        }
        return currentBuffer.getVisibleLines(visibleRows, scrollingOffset);
    }
}
