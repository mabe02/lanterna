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
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.terminal.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.IOException;
import java.util.*;

/**
 * This is the default and only implementation of Screen in Lanterna, giving you the ability to modify the terminal
 * content in a buffered way and then calling {@code refresh()} to have your changes take effect.
 *
 * @author Martin
 */
public class DefaultScreen extends TerminalScreen {

    private static final ScreenCharacter DEFAULT_CHARACTER = new ScreenCharacter(' ');

    private TerminalPosition cursorPosition;
    private ScreenBuffer backBuffer;
    private ScreenBuffer frontBuffer;

    private boolean isStarted;
    private boolean fullRedrawHint;

    //How to deal with \t characters
    private TabBehaviour tabBehaviour;

    /**
     * Creates a new Screen on top of a supplied terminal, will query the terminal for its size. The screen is initially
     * blank.
     *
     * @param terminal
     * @throws java.io.IOException
     */
    public DefaultScreen(Terminal terminal) throws IOException {
        this(terminal, null);
    }

    /**
     * Creates a new Screen on top of a supplied terminal and will set the size of the screen to a supplied value. The
     * screen is initially blank.
     *
     * @param terminal
     * @param terminalSize
     * @throws java.io.IOException
     */
    public DefaultScreen(Terminal terminal, TerminalSize terminalSize) throws IOException {
        super(terminal, terminalSize);
        this.frontBuffer = new ScreenBuffer(getTerminalSize(), DEFAULT_CHARACTER);
        this.backBuffer = new ScreenBuffer(getTerminalSize(), DEFAULT_CHARACTER);
        this.cursorPosition = new TerminalPosition(0, 0);
        this.tabBehaviour = TabBehaviour.ALIGN_TO_COLUMN_4;
        this.isStarted = false;
        this.fullRedrawHint = true;
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
        TerminalSize terminalSize = getTerminalSize();
        if(position.getColumn() >= 0 && position.getColumn() < terminalSize.getColumns()
                && position.getRow() >= 0 && position.getRow() < terminalSize.getRows()) {           
            this.cursorPosition = position;
        }
    }

    /**
     * Sets the behaviour for what to do about tab characters.
     *
     * @param tabBehaviour
     * @see TabBehaviour
     */
    @Override
    public void setTabBehaviour(TabBehaviour tabBehaviour) {
        if(tabBehaviour != null) {
            this.tabBehaviour = tabBehaviour;
        }
    }

    /**
     * Gets the behaviour for what to do about tab characters.
     *
     * @return
     * @see TabBehaviour
     */
    @Override
    public TabBehaviour getTabBehaviour() {
        return tabBehaviour;
    }

    @Override
    public synchronized void startScreen() throws IOException {
        if(isStarted) {
            return;
        }

        isStarted = true;
        getTerminal().enterPrivateMode();
        getTerminal().getTerminalSize();
        getTerminal().clearScreen();
        clear();
        if(cursorPosition != null) {
            getTerminal().setCursorVisible(true);
            getTerminal().setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
        } else {
            getTerminal().setCursorVisible(false);
        }
    }

    @Override
    public synchronized void stopScreen() throws IOException {
        if(!isStarted) {
            return;
        }

        while(readInput() != null) {
            //Drain the input queue before exiting private mode and closing the Screen.
        }
        getTerminal().exitPrivateMode();
        isStarted = false;
    }

    @Override
    public synchronized void clear() {
        backBuffer.setAll(DEFAULT_CHARACTER);
        fullRedrawHint = true;
    }

    @Override
    public synchronized TerminalSize doResizeIfNecessary() {
        TerminalSize pendingResize = getAndClearPendingResize();
        if(pendingResize == null) {
            return null;
        }

        backBuffer = backBuffer.resize(pendingResize, DEFAULT_CHARACTER);
        frontBuffer = frontBuffer.resize(pendingResize, DEFAULT_CHARACTER);
        fullRedrawHint = true;
        return pendingResize;
    }

    @Override
    public synchronized void setCharacter(int column, int row, ScreenCharacter screenCharacter) {
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
        
        //Pad CJK character with a trailing space
        if(CJKUtils.isCharCJK(screenCharacter.getCharacter())) {
            backBuffer.setCharacterAt(column + 1, row, screenCharacter.withCharacter(' '));
        }
        //If there's a CJK character immediately to our left, reset it
        if(column > 0 && CJKUtils.isCharCJK(backBuffer.getCharacterAt(column - 1, row).getCharacter())) {
            backBuffer.setCharacterAt(column - 1, row, backBuffer.getCharacterAt(column - 1, row).withCharacter(' '));
        }
    }

    @Override
    public synchronized ScreenCharacter getFrontCharacter(TerminalPosition position) {
        return getCharacterFromBuffer(frontBuffer, position);
    }

    @Override
    public synchronized ScreenCharacter getBackCharacter(TerminalPosition position) {
        return getCharacterFromBuffer(backBuffer, position);
    }
    
    private ScreenCharacter getCharacterFromBuffer(ScreenBuffer buffer, TerminalPosition position) {
        //If we are picking the padding of a CJK character, pick the actual CJK character instead of the padding
        if(position.getColumn() > 0 && CJKUtils.isCharCJK(buffer.getCharacterAt(position.withRelativeColumn(-1)).getCharacter())) {
            return buffer.getCharacterAt(position.withRelativeColumn(-1));
        }
        return buffer.getCharacterAt(position);
    }

    @Override
    public void refresh() throws IOException {
        refresh(RefreshType.AUTOMATIC);
    }

    @Override
    public synchronized void refresh(RefreshType refreshType) throws IOException {
        if(!isStarted) {
            return;
        }
        if((refreshType == RefreshType.AUTOMATIC && fullRedrawHint) || refreshType == RefreshType.COMPLETE) {
            refreshFull();
            fullRedrawHint = false;
        }
        else if(refreshType == RefreshType.AUTOMATIC) {
            double threshold = getTerminalSize().getRows() * getTerminalSize().getColumns() * 0.25;
            if(backBuffer.isVeryDifferent(frontBuffer, (int)threshold)) {
                refreshFull();
            }
            else {
                refreshByDelta();
            }
        }
        else {
            refreshByDelta();
        }
        if(cursorPosition != null) {
            getTerminal().setCursorVisible(true);
            //If we are trying to move the cursor to the padding of a CJK character, put it on the actual character instead
            if(cursorPosition.getColumn() > 0 && CJKUtils.isCharCJK(frontBuffer.getCharacterAt(cursorPosition.withRelativeColumn(-1)).getCharacter())) {
                getTerminal().setCursorPosition(cursorPosition.getColumn() - 1, cursorPosition.getRow());
            }
            else {
                getTerminal().setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
            }            
        } else {
            getTerminal().setCursorVisible(false);
        }
        getTerminal().flush();
        backBuffer.copyTo(frontBuffer);
    }

    private void refreshByDelta() throws IOException {
        Map<TerminalPosition, ScreenCharacter> updateMap = new TreeMap<TerminalPosition, ScreenCharacter>(new ScreenPointComparator());
        TerminalSize terminalSize = getTerminalSize();
        for(int y = 0; y < terminalSize.getRows(); y++) {
            for(int x = 0; x < terminalSize.getColumns(); x++) {
                ScreenCharacter backBufferCharacter = backBuffer.getCharacterAt(x, y);
                if(!backBufferCharacter.equals(frontBuffer.getCharacterAt(x, y))) {
                    updateMap.put(new TerminalPosition(x, y), backBufferCharacter);
                }
                if(CJKUtils.isCharCJK(backBufferCharacter.getCharacter())) {
                    x++;    //Skip the trailing padding
                }
            }
        }

        if(updateMap.isEmpty()) {
            return;
        }
        TerminalPosition currentPosition = updateMap.keySet().iterator().next();
        getTerminal().setCursorPosition(currentPosition.getColumn(), currentPosition.getRow());

        ScreenCharacter firstScreenCharacterToUpdate = updateMap.values().iterator().next();
        EnumSet<Terminal.SGR> currentSGR = firstScreenCharacterToUpdate.getModifiers();
        getTerminal().resetAllSGR();
        for(Terminal.SGR sgr: currentSGR) {
            getTerminal().enableSGR(sgr);
        }
        TextColor currentForegroundColor = firstScreenCharacterToUpdate.getForegroundColor();
        TextColor currentBackgroundColor = firstScreenCharacterToUpdate.getBackgroundColor();
        getTerminal().setForegroundColor(currentForegroundColor);
        getTerminal().setBackgroundColor(currentBackgroundColor);
        for(TerminalPosition position: updateMap.keySet()) {
            if(!position.equals(currentPosition)) {
                getTerminal().setCursorPosition(position.getColumn(), position.getRow());
            }
            ScreenCharacter newCharacter = updateMap.get(position);
            if(!currentForegroundColor.equals(newCharacter.getForegroundColor())) {
                getTerminal().setForegroundColor(newCharacter.getForegroundColor());
                currentForegroundColor = newCharacter.getForegroundColor();
            }
            if(!currentBackgroundColor.equals(newCharacter.getBackgroundColor())) {
                getTerminal().setBackgroundColor(newCharacter.getBackgroundColor());
                currentBackgroundColor = newCharacter.getBackgroundColor();
            }
            for(Terminal.SGR sgr: Terminal.SGR.values()) {
                if(currentSGR.contains(sgr) && !newCharacter.getModifiers().contains(sgr)) {
                    getTerminal().disableSGR(sgr);
                    currentSGR.remove(sgr);
                }
                else if(!currentSGR.contains(sgr) && newCharacter.getModifiers().contains(sgr)) {
                    getTerminal().enableSGR(sgr);
                    currentSGR.add(sgr);
                }
            }
            getTerminal().putCharacter(newCharacter.getCharacter());
            if(CJKUtils.isCharCJK(newCharacter.getCharacter())) {
                //CJK characters advances two columns
                currentPosition = currentPosition.withRelativeColumn(2);
            }
            else {
                //Normal characters advances one column
                currentPosition = currentPosition.withRelativeColumn(1);
            }
        }
    }

    private void refreshFull() throws IOException {
        getTerminal().setForegroundColor(TextColor.ANSI.DEFAULT);
        getTerminal().setBackgroundColor(TextColor.ANSI.DEFAULT);
        getTerminal().clearScreen();
        getTerminal().resetAllSGR();

        EnumSet<Terminal.SGR> currentSGR = EnumSet.noneOf(Terminal.SGR.class);
        TextColor currentForegroundColor = TextColor.ANSI.DEFAULT;
        TextColor currentBackgroundColor = TextColor.ANSI.DEFAULT;
        for(int y = 0; y < getTerminalSize().getRows(); y++) {
            getTerminal().setCursorPosition(0, y);
            int currentColumn = 0;
            for(int x = 0; x < getTerminalSize().getColumns(); x++) {
                ScreenCharacter newCharacter = backBuffer.getCharacterAt(x, y);
                if(newCharacter.equals(DEFAULT_CHARACTER)) {
                    continue;
                }

                if(!currentForegroundColor.equals(newCharacter.getForegroundColor())) {
                    getTerminal().setForegroundColor(newCharacter.getForegroundColor());
                    currentForegroundColor = newCharacter.getForegroundColor();
                }
                if(!currentBackgroundColor.equals(newCharacter.getBackgroundColor())) {
                    getTerminal().setBackgroundColor(newCharacter.getBackgroundColor());
                    currentBackgroundColor = newCharacter.getBackgroundColor();
                }
                for(Terminal.SGR sgr: Terminal.SGR.values()) {
                    if(currentSGR.contains(sgr) && !newCharacter.getModifiers().contains(sgr)) {
                        getTerminal().disableSGR(sgr);
                        currentSGR.remove(sgr);
                    }
                    else if(!currentSGR.contains(sgr) && newCharacter.getModifiers().contains(sgr)) {
                        getTerminal().enableSGR(sgr);
                        currentSGR.add(sgr);
                    }
                }
                if(currentColumn != x) {
                    getTerminal().setCursorPosition(x, y);
                    currentColumn = x;
                }
                getTerminal().putCharacter(newCharacter.getCharacter());
                if(CJKUtils.isCharCJK(newCharacter.getCharacter())) {
                    //CJK characters take up two columns
                    currentColumn += 2;
                }
                else {
                    //Normal characters take up one column
                    currentColumn += 1;
                }
            }
        }
    }

    private static class ScreenPointComparator implements Comparator<TerminalPosition> {
        @Override
        public int compare(TerminalPosition o1, TerminalPosition o2) {
            if(o1.getRow() == o2.getRow()) {
                if(o1.getColumn() == o2.getColumn()) {
                    return 0;
                } else {
                    return new Integer(o1.getColumn()).compareTo(o2.getColumn());
                }
            } else {
                return new Integer(o1.getRow()).compareTo(o2.getRow());
            }
        }
    }
}
