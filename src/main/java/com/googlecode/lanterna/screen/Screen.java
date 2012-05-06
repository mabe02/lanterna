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
 * Copyright (C) 2010-2011 mabe02
 */

package com.googlecode.lanterna.screen;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import com.googlecode.lanterna.LanternException;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Style;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * A layer to put on top of a Terminal object, giving you a kind of screen buffer
 * to use, which is a lot easier to work with. Drawing text or graphics to the
 * terminal is kind of like writing to a bitmap.
 * @author mabe02
 */
public class Screen implements InputProvider
{
    private final Object mutex;
    private final Terminal terminal;
    private final LinkedList<TerminalSize> resizeQueue;
    private TerminalPosition cursorPosition;
    private TerminalSize terminalSize;
    private ScreenCharacter [][] visibleScreen;
    private ScreenCharacter [][] backbuffer;
    private boolean wholeScreenInvalid;
    private boolean hasBeenActivated;
    
    //How to deal with \t characters
    private TabBehaviour tabBehaviour;

    public Screen(Terminal terminal) throws LanternException
    {
        this(terminal, terminal.queryTerminalSize());
    }

    public Screen(Terminal terminal, TerminalSize terminalSize)
    {
        this(terminal, terminalSize.getColumns(), terminalSize.getRows());
    }

    public Screen(Terminal terminal, int terminalWidth, int terminalHeight)
    {
        this.mutex = new Object();
        this.terminal = terminal;
        this.terminalSize = new TerminalSize(terminalWidth, terminalHeight);
        this.visibleScreen = new ScreenCharacter[terminalHeight][terminalWidth];
        this.backbuffer = new ScreenCharacter[terminalHeight][terminalWidth];
        this.resizeQueue = new LinkedList<TerminalSize>();
        this.wholeScreenInvalid = false;
        this.hasBeenActivated = false;
        this.cursorPosition = new TerminalPosition(0, 0);
        this.tabBehaviour = TabBehaviour.ALIGN_TO_COLUMN_8;

        this.terminal.addResizeListener(new TerminalResizeListener());

        ScreenCharacter background = new ScreenCharacter(new ScreenCharacter(' ', Terminal.Color.DEFAULT, Terminal.Color.DEFAULT, false));
        for(int y = 0; y < terminalHeight; y++) {
            for(int x = 0; x < terminalWidth; x++) {
                visibleScreen[y][x] = new ScreenCharacter(background);
                backbuffer[y][x] = new ScreenCharacter(background);
            }
        }
    }

    public TerminalPosition getCursorPosition()
    {
        return cursorPosition;
    }

    public void setCursorPosition(TerminalPosition position)
    {
        if(position != null)
            this.cursorPosition = new TerminalPosition(position);
    }

    public void setCursorPosition(int column, int row)
    {
        this.cursorPosition = new TerminalPosition(column, row);
    }

    public void setTabBehaviour(TabBehaviour tabBehaviour) {
        if(tabBehaviour != null)
            this.tabBehaviour = tabBehaviour;
    }

    public TabBehaviour getTabBehaviour() {
        return tabBehaviour;
    }
    
    public Key readInput() throws LanternException
    {
        return terminal.readInput();
    }

    public TerminalSize getTerminalSize()
    {
        synchronized(mutex) {
            return terminalSize;
        }
    }
    
    public void hackSendFakeResize() throws LanternException
    {
        terminal.hackSendFakeResize();
    }

    public void startScreen() throws LanternException
    {
        if(hasBeenActivated)
            return;

        hasBeenActivated = true;
        terminal.enterPrivateMode();
        terminal.clearScreen();
        refresh();
    }

    public void stopScreen() throws LanternException
    {
        if(!hasBeenActivated)
            return;

        terminal.exitPrivateMode();
        hasBeenActivated = false;
    }

    public void putString(int x, int y, String string, Terminal.Color foregroundColor,
            Terminal.Color backgroundColor, boolean bold, boolean underline, boolean negative)
    {
        putString(x, y, string, foregroundColor, backgroundColor, bold, underline, negative, false);
    }
    
    public void putString(int x, int y, String string, Terminal.Color foregroundColor,
            Terminal.Color backgroundColor, EnumSet<Terminal.Style> styles)
    {
    	putString(x, y, string, foregroundColor, backgroundColor,
    		styles.contains(Style.Bold), styles.contains(Style.Underline),
    		styles.contains(Style.Reverse), styles.contains(Style.Blinking));
    }
    
    public void putString(int x, int y, String string, Terminal.Color foregroundColor,
        Terminal.Color backgroundColor, boolean bold, boolean underline, boolean negative, boolean blinking)
    {
    	int tabPosition = string.indexOf('\t');
        while(tabPosition != -1) {
            int tabX = x + tabPosition;
            String tabReplacementHere = getTabReplacement(x);
            string = string.substring(0, tabPosition) + tabReplacementHere + string.substring(tabPosition + 1);
            tabPosition += tabReplacementHere.length();
            tabPosition = string.indexOf('\t', tabPosition);
        }
  	
    	for(int i = 0; i < string.length(); i++)
    		putCharacter(x + i, y, 
                        new ScreenCharacter(string.charAt(i), 
                                            foregroundColor, 
                                            backgroundColor,
                                            bold, 
                                            underline, 
                                            negative, 
                                            blinking));
    }

    void putCharacter(int x, int y, ScreenCharacter character)
    {
        synchronized(mutex) {
            if(y < 0 || y >= backbuffer.length || x < 0 || x >= backbuffer[0].length)
                return;

            backbuffer[y][x] = new ScreenCharacter(character);
        }
    }

    /**
     * This method will check if there are any resize commands pending and
     * apply them to the Screen
     * @return true if the size is the same as before, false if the screen was resized
     */
    public boolean verifySize()
    {
        synchronized(mutex) {
            TerminalSize newSize;
            synchronized(resizeQueue) {
                if(resizeQueue.size() == 0)
                    return true;

                newSize = resizeQueue.getLast();
                resizeQueue.clear();
            }

            int height = newSize.getRows();
            int width = newSize.getColumns();
            ScreenCharacter [][]newBackBuffer = new ScreenCharacter[height][width];
            ScreenCharacter [][]newVisibleScreen = new ScreenCharacter[height][width];
            ScreenCharacter newAreaCharacter = new ScreenCharacter('X', Terminal.Color.GREEN, Terminal.Color.BLACK, false);
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    if(x < backbuffer[0].length && y < backbuffer.length)
                        newBackBuffer[y][x] = backbuffer[y][x];
                    else
                        newBackBuffer[y][x] = new ScreenCharacter(newAreaCharacter);

                    if(x < visibleScreen[0].length && y < visibleScreen.length)
                        newVisibleScreen[y][x] = visibleScreen[y][x];
                    else
                        newVisibleScreen[y][x] = new ScreenCharacter(newAreaCharacter);
                }
            }
            
            backbuffer = newBackBuffer;
            visibleScreen = newVisibleScreen;
            wholeScreenInvalid = true;
            terminalSize = new TerminalSize(newSize);
            return false;
        }
    }

    public void refresh() throws LanternException
    {
        if(!hasBeenActivated)
            return;
        
        synchronized(mutex) {
            Map<TerminalPosition, ScreenCharacter> updateMap = new TreeMap<TerminalPosition, ScreenCharacter>(new ScreenPointComparator());
            
            for(int y = 0; y < terminalSize.getRows(); y++)
            {
                for(int x = 0; x < terminalSize.getColumns(); x++)
                {
                    ScreenCharacter c = backbuffer[y][x];
                    if(!c.equals(visibleScreen[y][x]) || wholeScreenInvalid) {
                        visibleScreen[y][x] = new ScreenCharacter(c);
                        updateMap.put(new TerminalPosition(x, y), c);
                    }
                }
            }

            Writer terminalWriter = new Writer();
            terminalWriter.reset();
            TerminalPosition previousPoint = null;
            for(TerminalPosition nextUpdate: updateMap.keySet()) {
                if(previousPoint == null || previousPoint.getRow() != nextUpdate.getRow() ||
                        previousPoint.getColumn() + 1 != nextUpdate.getColumn()) {
                    terminalWriter.setCursorPosition(nextUpdate.getColumn(), nextUpdate.getRow());
                }
                terminalWriter.writeCharacter(updateMap.get(nextUpdate));
                previousPoint = nextUpdate;
            }
            terminalWriter.setCursorPosition(getCursorPosition().getColumn(), getCursorPosition().getRow());
            wholeScreenInvalid = false;
        }
        terminal.flush();
    }

    @Deprecated
    public int getWidth()
    {
        return getTerminalSize().getColumns();
    }

    @Deprecated
    public int getHeight()
    {
        return getTerminalSize().getRows();
    }

    private String getTabReplacement(int x) {
        int align = 0;
        switch(tabBehaviour) {
            case CONVERT_TO_ONE_SPACE:
                return " ";
            case CONVERT_TO_FOUR_SPACES:
                return "    ";
            case CONVERT_TO_EIGHT_SPACES:
                return "        ";
            case ALIGN_TO_COLUMN_4:
                align = 4 - (x % 4);
                break;
            case ALIGN_TO_COLUMN_8:
                align = 8 - (x % 8);
                break;
        }
        StringBuilder replace = new StringBuilder();
        for(int i = 0; i < align; i++)
            replace.append(" ");
        return replace.toString();
    }

    private static class ScreenPointComparator implements Comparator<TerminalPosition>
    {
        public int compare(TerminalPosition o1, TerminalPosition o2)
        {
            if(o1.getRow() == o2.getRow())
                if(o1.getColumn() == o2.getColumn())
                    return 0;
                else
                    return new Integer(o1.getColumn()).compareTo(o2.getColumn());
            else
                return new Integer(o1.getRow()).compareTo(o2.getRow());
        }
    }

    private class TerminalResizeListener implements Terminal.ResizeListener
    {
        public void onResized(TerminalSize newSize)
        {
            synchronized(resizeQueue) {
                resizeQueue.add(newSize);
            }
        }
    }

    private class Writer
    {
        private Terminal.Color currentForegroundColor;
        private Terminal.Color currentBackgroundColor;
        private boolean currentlyIsBold;
        private boolean currentlyIsUnderline;
        private boolean currentlyIsNegative;
        private boolean currentlyIsBlinking;
        
        public Writer()
        {
            currentForegroundColor = Terminal.Color.DEFAULT;
            currentBackgroundColor = Terminal.Color.DEFAULT;
            currentlyIsBold = false;
            currentlyIsUnderline = false;
            currentlyIsNegative = false;
            currentlyIsBlinking = false;
        }

        void setCursorPosition(int x, int y) throws LanternException
        {
            terminal.moveCursor(x, y);
        }

        void writeCharacter(ScreenCharacter character) throws LanternException
        {
            if (currentlyIsBlinking != character.isBlinking()) {
                if (character.isBlinking()) {
                    terminal.applySGR(Terminal.SGR.ENTER_BLINK);
                    currentlyIsBlinking = true;
                }
                else {
                    terminal.applySGR(Terminal.SGR.RESET_ALL);
                    terminal.applyBackgroundColor(character.getBackgroundColor());
                    terminal.applyForegroundColor(character.getForegroundColor());

                    // emulating "stop_blink_mode" so that previous formatting is preserved
                    currentlyIsBold = false;
                    currentlyIsUnderline = false;
                    currentlyIsNegative = false;
                    currentlyIsBlinking = false;
                }
            }
            if(currentForegroundColor != character.getForegroundColor()) {
                terminal.applyForegroundColor(character.getForegroundColor());
                currentForegroundColor = character.getForegroundColor();
            }
            if(currentBackgroundColor != character.getBackgroundColor()) {
                terminal.applyBackgroundColor(character.getBackgroundColor());
                currentBackgroundColor = character.getBackgroundColor();
            }
            if(currentlyIsBold != character.isBold()) {
                if(character.isBold()) {
                    terminal.applySGR(Terminal.SGR.ENTER_BOLD);
                    currentlyIsBold = true;
                }
                else {
                    terminal.applySGR(Terminal.SGR.EXIT_BOLD);
                    currentlyIsBold = false;
                }
            }
            if(currentlyIsUnderline != character.isUnderline()) {
                if(character.isUnderline()) {
                    terminal.applySGR(Terminal.SGR.ENTER_UNDERLINE);
                    currentlyIsUnderline = true;
                }
                else {
                    terminal.applySGR(Terminal.SGR.EXIT_UNDERLINE);
                    currentlyIsUnderline = false;
                }
            }
            if(currentlyIsNegative != character.isNegative()) {
                if(character.isNegative()) {
                    terminal.applySGR(Terminal.SGR.ENTER_REVERSE);
                    currentlyIsNegative = true;
                }
                else {
                    terminal.applySGR(Terminal.SGR.EXIT_REVERSE);
                    currentlyIsNegative = false;
                }
            }
            terminal.putCharacter(character.getCharacter());
        }

        void reset() throws LanternException
        {
            terminal.applySGR(Terminal.SGR.RESET_ALL);
            terminal.moveCursor(0, 0);

            currentBackgroundColor = Terminal.Color.DEFAULT;
            currentForegroundColor = Terminal.Color.DEFAULT;
            currentlyIsBold = false;
            currentlyIsNegative = false;
            currentlyIsUnderline = false;
        }
    }
}
