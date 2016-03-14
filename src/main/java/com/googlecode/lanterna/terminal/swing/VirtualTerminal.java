package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

/**
 * This class implements the non-graphical parts of a terminal emulator, including text buffers with history, cursor
 * position and how to modify these two. When you enter new text, this class takes care of inserting this text into
 * the right place and making necessary adjustments with new lines as needed.
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

    /**
     * Creates a new virtual terminal with an initial size set
     * @param initialTerminalSize Starting size of the virtual terminal
     */
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

    synchronized void setBacklogSize(int backlogSize) {
        this.backlogSize = backlogSize;
    }

    synchronized TerminalSize getViewportSize() {
        return viewportSize;
    }

    synchronized void setViewportSize(TerminalSize newSize) {
        this.viewportSize = newSize;
        trimBufferBacklog();
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
            if(cursorPosition.getColumn() == viewportSize.getColumns()) {
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
            if(cursorPosition.getColumn() > viewportSize.getColumns()) {
                moveCursorToNextLine();
            }
        }
        trimBufferBacklog();
    }

    synchronized TerminalPosition getGlobalCursorPosition() {
        return cursorPosition;
    }

    synchronized TerminalPosition getCursorPosition() {
        if(getBufferLineCount() <= viewportSize.getRows()) {
            return cursorPosition;
        }
        else {
            return cursorPosition.withRelativeRow(-(getBufferLineCount() - viewportSize.getRows()));
        }
    }

    synchronized TreeSet<TerminalPosition> getDirtyCells() {
        return new TreeSet<TerminalPosition>(dirtyTerminalCells);
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

    synchronized void switchToPrivateMode() {
        currentTextBuffer = privateModeTextBuffer;
        invalidateWholeBuffer();
    }

    synchronized void switchToNormalMode() {
        currentTextBuffer = regularTextBuffer;
        invalidateWholeBuffer();
    }

    synchronized void clear() {
        currentTextBuffer.clear();
        invalidateWholeBuffer();
        setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
    }

    synchronized void setCursorPosition(TerminalPosition cursorPosition) {
        TerminalPosition position = translateCursorSpaceToGlobalSpace(cursorPosition);
        if(position.getColumn() >= viewportSize.getColumns()) {
            position = position.withRelativeColumn(viewportSize.getColumns() - 1);
        }
        this.cursorPosition = position;
    }

    synchronized TextCharacter getCharacter(TerminalPosition position) {
        TerminalPosition globalPosition = translateCursorSpaceToGlobalSpace(position);
        return currentTextBuffer.getCharacter(globalPosition.getRow(), globalPosition.getColumn());
    }

    synchronized int getBufferLineCount() {
        return currentTextBuffer.getLineCount();
    }

    synchronized void forEachLine(int startRow, int endRow, BufferWalker bufferWalker) {
        final BufferLine emptyLine = new BufferLine() {
            @Override
            public TextCharacter getCharacterAt(int column) {
                return TextCharacter.DEFAULT_CHARACTER;
            }
        };
        ListIterator<List<TextCharacter>> iterator = currentTextBuffer.getLinesFrom(startRow);
        for(int row = startRow; row <= endRow; row++) {
            BufferLine bufferLine = emptyLine;
            if(iterator.hasNext()) {
                final List<TextCharacter> list = iterator.next();
                bufferLine = new BufferLine() {
                    @Override
                    public TextCharacter getCharacterAt(int column) {
                        if(column >= list.size()) {
                            return TextCharacter.DEFAULT_CHARACTER;
                        }
                        return list.get(column);
                    }
                };
            }
            bufferWalker.drawLine(row, bufferLine);
        }
    }

    private void invalidateWholeBuffer() {
        wholeBufferDirty = true;
        dirtyTerminalCells.clear();
    }

    private TerminalPosition translateCursorSpaceToGlobalSpace(TerminalPosition cursorSpacePosition) {
        if(viewportSize.getRows() >= currentTextBuffer.getLineCount()) {
            return cursorSpacePosition;
        }
        return cursorSpacePosition.withRelativeRow(currentTextBuffer.getLineCount() - viewportSize.getRows());
    }

    private void trimBufferBacklog() {
        // Now see if we need to discard lines from the backlog
        int bufferBacklogSize = backlogSize;
        if(currentTextBuffer == privateModeTextBuffer) {
            bufferBacklogSize = 0;
        }
        int trimBacklogRows = currentTextBuffer.getLineCount() - (bufferBacklogSize + viewportSize.getRows());
        if(trimBacklogRows > 0) {
            currentTextBuffer.removeTopLines(trimBacklogRows);
            // Adjust cursor position
            cursorPosition = cursorPosition.withRelativeRow(-trimBacklogRows);
            correctCursor();
            if(!wholeBufferDirty) {
                // Adjust all "dirty" positions
                TreeSet<TerminalPosition> newDirtySet = new TreeSet<TerminalPosition>();
                for(TerminalPosition dirtyPosition: dirtyTerminalCells) {
                    TerminalPosition adjustedPosition = dirtyPosition.withRelativeRow(-trimBacklogRows);
                    if(adjustedPosition.getRow() >= 0) {
                        newDirtySet.add(adjustedPosition);
                    }
                }
                dirtyTerminalCells.clear();
                dirtyTerminalCells.addAll(newDirtySet);
            }
        }
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

    interface BufferLine {
        TextCharacter getCharacterAt(int column);
    }

    interface BufferWalker {
        void drawLine(int rowNumber, BufferLine bufferLine);
    }
}
