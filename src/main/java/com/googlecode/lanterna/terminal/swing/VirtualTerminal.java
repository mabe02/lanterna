package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.util.List;
import java.util.TreeSet;

/**
 * Created by Martin on 2016-02-21.
 */
class VirtualTerminal {
    private final TextBuffer regularTextBuffer;
    private final TextBuffer privateModeTextBuffer;
    private final TreeSet<TerminalPosition> dirtyTerminalCells;

    private TextBuffer currentTextBuffer;
    private boolean wholeBufferDirty;
    private TerminalSize viewportSize;
    private int backlogSize;

    // Position is stored in "global coordinates", where 0x0 is the top-left corner of the scrollback buffer
    private TerminalPosition cursorPosition;

    VirtualTerminal(TerminalSize initialTerminalSize) {
        this.regularTextBuffer = new TextBuffer();
        this.privateModeTextBuffer = new TextBuffer();
        this.dirtyTerminalCells = new TreeSet<TerminalPosition>();

        // Start with regular mode
        this.currentTextBuffer = regularTextBuffer;
        this.wholeBufferDirty = false;
        this.viewportSize = initialTerminalSize;
        this.cursorPosition = TerminalPosition.TOP_LEFT_CORNER;
        this.backlogSize = 1000;
    }

    synchronized TerminalSize getViewportSize() {
        return viewportSize;
    }

    synchronized void setViewportSize(TerminalSize newSize) {
        // TODO: do we need to do this in the new implementation?
        //if(viewportSize.getRows() < newSize.getRows()) {
        //    cursorPosition = cursorPosition.withRelativeRow(newSize.getRows() - size.getRows());
        //}
        this.viewportSize = newSize;
        correctCursor();
    }

    synchronized void putCharacter(TextCharacter terminalCharacter) {
        if(terminalCharacter.getCharacter() == '\n') {
            moveCursorToNextLine();
        }
        else if(terminalCharacter.getCharacter() == '\t') {
            int nrOfSpaces = TabBehaviour.ALIGN_TO_COLUMN_4.getTabReplacement(cursorPosition.getColumn()).length();
            for(int i = 0; i < nrOfSpaces && cursorPosition.getColumn() < viewportSize.getColumns() - 1; i++) {
                putCharacter(terminalCharacter.withCharacter(' '));
            }
        }
        else {
            boolean doubleWidth = TerminalTextUtils.isCharDoubleWidth(terminalCharacter.getCharacter());
            // If we're at the last column and the user tries to print a double-width character, reset the cell and move
            // to the next line
            if(cursorPosition.getColumn() == viewportSize.getColumns() - 1 && doubleWidth) {
                currentTextBuffer.setCharacter(cursorPosition.getRow(), cursorPosition.getColumn(), TextCharacter.DEFAULT_CHARACTER);
                moveCursorToNextLine();
            }

            // Update the buffer
            int i = currentTextBuffer.setCharacter(cursorPosition.getRow(), cursorPosition.getColumn(), terminalCharacter);
            if(!wholeBufferDirty) {
                dirtyTerminalCells.add(new TerminalPosition(cursorPosition.getColumn(), cursorPosition.getRow()));
                if(i == 1) {
                    dirtyTerminalCells.add(new TerminalPosition(cursorPosition.getColumn() + 1, cursorPosition.getRow()));
                }
                else if(i == 2) {
                    dirtyTerminalCells.add(new TerminalPosition(cursorPosition.getColumn() - 1, cursorPosition.getRow()));
                }
                if(dirtyTerminalCells.size() > (viewportSize.getColumns() * viewportSize.getRows() * 0.9)) {
                    invalidateWholeBuffer();
                }
            }

            //Advance cursor
            cursorPosition = cursorPosition.withRelativeColumn(doubleWidth ? 2 : 1);
            if(cursorPosition.getColumn() >= viewportSize.getColumns()) {
                moveCursorToNextLine();
            }
            // TODO: ensure there is enough lines here!
        }
    }

    synchronized TerminalPosition getCursorPosition() {
        if(viewportSize.getRows() >= currentTextBuffer.getLineCount()) {
            return cursorPosition;
        }
        return cursorPosition.withRelativeRow(-(currentTextBuffer.getLineCount() - viewportSize.getRows()));
    }

    synchronized TreeSet<TerminalPosition> getAndResetDirtyCells() {
        TreeSet<TerminalPosition> copy = new TreeSet<TerminalPosition>(dirtyTerminalCells);
        dirtyTerminalCells.clear();
        return copy;
    }

    synchronized boolean isWholeBufferDirtyThenReset() {
        boolean copy = wholeBufferDirty;
        wholeBufferDirty = false;
        return copy;
    }

    synchronized TerminalPosition getTranslatedCursorPosition() {
        //TODO: Implementation
        return TerminalPosition.TOP_LEFT_CORNER;
    }

    synchronized void switchToPrivateMode() {
        //TODO: Implementation
        currentTextBuffer = privateModeTextBuffer;
    }

    synchronized void switchToNormalMode() {
        //TODO: Implementation
        currentTextBuffer = regularTextBuffer;
    }

    synchronized void clear() {
        //TODO: Implementation
        currentTextBuffer.clear();
        invalidateWholeBuffer();
        setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
    }

    synchronized void setCursorPosition(TerminalPosition cursorPosition) {
        this.cursorPosition = translateCursorSpaceToGlobalSpace(cursorPosition);
    }

    synchronized TextCharacter getCharacter(TerminalPosition position) {
        TerminalPosition globalPosition = translateCursorSpaceToGlobalSpace(position);
        return currentTextBuffer.getCharacter(globalPosition.getRow(), globalPosition.getColumn());
    }

    synchronized Iterable<List<TextCharacter>> getLines() {
        //TODO: This is just temporary
        return currentTextBuffer.getLines();
    }

    private void invalidateWholeBuffer() {
        wholeBufferDirty = true;
        dirtyTerminalCells.clear();
    }

    private TerminalPosition translateCursorSpaceToGlobalSpace(TerminalPosition cursorSpacePosition) {
        if(viewportSize.getRows() >= currentTextBuffer.getLineCount()) {
            return cursorSpacePosition;
        }
        return cursorSpacePosition.withRelativeColumn(currentTextBuffer.getLineCount() - viewportSize.getRows());
    }

    private void correctCursor() {
        this.cursorPosition =
                new TerminalPosition(
                        Math.max(cursorPosition.getColumn(), 0),
                        Math.max(cursorPosition.getRow(), 0));
        this.cursorPosition = cursorPosition.withColumn(Math.min(cursorPosition.getColumn(), viewportSize.getColumns() - 1));
    }

    private void moveCursorToNextLine() {
        cursorPosition = cursorPosition.withColumn(0).withRelativeRow(1);
        if(cursorPosition.getRow() >= currentTextBuffer.getLineCount()) {
            currentTextBuffer.newLine();
        }
    }
}
