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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextImage;

import java.io.IOException;

/**
 * This class implements some of the Screen logic that is not directly tied to the actual implementation of how the
 * Screen translate to the terminal. It keeps data structures for the front- and back buffers, the cursor location and
 * some other simpler states.
 * @author martin
 */
public abstract class AbstractScreen implements Screen {
    private TerminalPosition cursorPosition;
    private ScreenBuffer backBuffer;
    private ScreenBuffer frontBuffer;
    private final TextCharacter defaultCharacter;

    //How to deal with \t characters
    private TabBehaviour tabBehaviour;

    //Current size of the screen
    private TerminalSize terminalSize;

    //Pending resize of the screen
    private TerminalSize latestResizeRequest;

    public AbstractScreen(TerminalSize initialSize) {
        this(initialSize, DEFAULT_CHARACTER);
    }

    /**
     * Creates a new Screen on top of a supplied terminal, will query the terminal for its size. The screen is initially
     * blank. You can specify which character you wish to be used to fill the screen initially; this will also be the
     * character used if the terminal is enlarged and you don't set anything on the new areas.
     *
     * @param initialSize Size to initially create the Screen with (can be resized later)
     * @param defaultCharacter What character to use for the initial state of the screen and expanded areas
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public AbstractScreen(TerminalSize initialSize, TextCharacter defaultCharacter) {
        this.frontBuffer = new ScreenBuffer(initialSize, defaultCharacter);
        this.backBuffer = new ScreenBuffer(initialSize, defaultCharacter);
        this.defaultCharacter = defaultCharacter;
        this.cursorPosition = new TerminalPosition(0, 0);
        this.tabBehaviour = TabBehaviour.ALIGN_TO_COLUMN_4;
        this.terminalSize = initialSize;
        this.latestResizeRequest = null;
    }

    /**
     * @return Position where the cursor will be located after the screen has been refreshed or {@code null} if the
     * cursor is not visible
     */
    @Override
    public TerminalPosition getCursorPosition() {
        return cursorPosition;
    }

    /**
     * Moves the current cursor position or hides it. If the cursor is hidden and given a new position, it will be
     * visible after this method call.
     *
     * @param position 0-indexed column and row numbers of the new position, or if {@code null}, hides the cursor
     */
    @Override
    public void setCursorPosition(TerminalPosition position) {
        if(position == null) {
            //Skip any validation checks if we just want to hide the cursor
            this.cursorPosition = null;
            return;
        }
        if(position.getColumn() < 0) {
            position = position.withColumn(0);
        }
        if(position.getRow() < 0) {
            position = position.withRow(0);
        }
        if(position.getColumn() >= terminalSize.getColumns()) {
            position = position.withColumn(terminalSize.getColumns() - 1);
        }
        if(position.getRow() >= terminalSize.getRows()) {
            position = position.withRow(terminalSize.getRows() - 1);
        }
        this.cursorPosition = position;
    }

    @Override
    public void setTabBehaviour(TabBehaviour tabBehaviour) {
        if(tabBehaviour != null) {
            this.tabBehaviour = tabBehaviour;
        }
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return tabBehaviour;
    }

    @Override
    public void setCharacter(TerminalPosition position, TextCharacter screenCharacter) {
        setCharacter(position.getColumn(), position.getRow(), screenCharacter);
    }

    @Override
    public TextGraphics newTextGraphics() {
        return new ScreenTextGraphics(this) {
            @Override
            public TextGraphics drawImage(TerminalPosition topLeft, TextImage image, TerminalPosition sourceImageTopLeft, TerminalSize sourceImageSize) {
                backBuffer.copyFrom(image, sourceImageTopLeft.getRow(), sourceImageSize.getRows(), sourceImageTopLeft.getColumn(), sourceImageSize.getColumns(), topLeft.getRow(), topLeft.getColumn());
                return this;
            }
        };
    }

    @Override
    public synchronized void setCharacter(int column, int row, TextCharacter screenCharacter) {
        //It would be nice if we didn't have to care about tabs at this level, but we have no such luxury
        if(screenCharacter.getCharacter() == '\t') {
            //Swap out the tab for a space
            screenCharacter = screenCharacter.withCharacter(' ');

            //Now see how many times we have to put spaces...
            for(int i = 0; i < tabBehaviour.replaceTabs("\t", column).length(); i++) {
                backBuffer.setCharacterAt(column + i, row, screenCharacter);
            }
        }
        else {
            //This is the normal case, no special character
            backBuffer.setCharacterAt(column, row, screenCharacter);
        }
    }

    @Override
    public synchronized TextCharacter getFrontCharacter(TerminalPosition position) {
        return getFrontCharacter(position.getColumn(), position.getRow());
    }

    @Override
    public TextCharacter getFrontCharacter(int column, int row) {
        return getCharacterFromBuffer(frontBuffer, column, row);
    }

    @Override
    public synchronized TextCharacter getBackCharacter(TerminalPosition position) {
        return getBackCharacter(position.getColumn(), position.getRow());
    }

    @Override
    public TextCharacter getBackCharacter(int column, int row) {
        return getCharacterFromBuffer(backBuffer, column, row);
    }

    @Override
    public void refresh() throws IOException {
        refresh(RefreshType.AUTOMATIC);
    }

    @Override
    public void close() throws IOException {
        stopScreen();
    }

    @Override
    public synchronized void clear() {
        backBuffer.setAll(defaultCharacter);
    }

    @Override
    public synchronized TerminalSize doResizeIfNecessary() {
        TerminalSize pendingResize = getAndClearPendingResize();
        if(pendingResize == null) {
            return null;
        }

        backBuffer = backBuffer.resize(pendingResize, defaultCharacter);
        frontBuffer = frontBuffer.resize(pendingResize, defaultCharacter);
        return pendingResize;
    }

    @Override
    public TerminalSize getTerminalSize() {
        return terminalSize;
    }

    /**
     * Returns the front buffer connected to this screen, don't use this unless you know what you are doing!
     * @return This Screen's front buffer
     */
    protected ScreenBuffer getFrontBuffer() {
        return frontBuffer;
    }

    /**
     * Returns the back buffer connected to this screen, don't use this unless you know what you are doing!
     * @return This Screen's back buffer
     */
    protected ScreenBuffer getBackBuffer() {
        return backBuffer;
    }

    private synchronized TerminalSize getAndClearPendingResize() {
        if(latestResizeRequest != null) {
            terminalSize = latestResizeRequest;
            latestResizeRequest = null;
            return terminalSize;
        }
        return null;
    }

    /**
     * Tells this screen that the size has changed and it should, at next opportunity, resize itself and its buffers
     * @param newSize New size the 'real' terminal now has
     */
    protected void addResizeRequest(TerminalSize newSize) {
        latestResizeRequest = newSize;
    }

    private TextCharacter getCharacterFromBuffer(ScreenBuffer buffer, int column, int row) {
        if(column > 0) {
            //If we are picking the padding of a CJK character, pick the actual CJK character instead of the padding
            TextCharacter leftOfSpecifiedCharacter = buffer.getCharacterAt(column - 1, row);
            if(leftOfSpecifiedCharacter == null) {
                //If the character left of us doesn't exist, we don't exist either
                return null;
            }
            else if(TerminalTextUtils.isCharCJK(leftOfSpecifiedCharacter.getCharacter())) {
                return leftOfSpecifiedCharacter;
            }
        }
        return buffer.getCharacterAt(column, row);
    }
    
    @Override
    public String toString() {
        return getBackBuffer().toString();
    }

    /**
     * Performs the scrolling on its back-buffer.
     */
    @Override
    public void scrollLines(int firstLine, int lastLine, int distance) {
        getBackBuffer().scrollLines(firstLine, lastLine, distance);
    }
}
