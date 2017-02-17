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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.Scrollable;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import java.io.IOException;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is the default concrete implementation of the Screen interface, a buffered layer sitting on top of a Terminal.
 * If you want to get started with the Screen layer, this is probably the class you want to use. Remember to start the
 * screen before you can use it and stop it when you are done with it. This will place the terminal in private mode
 * during the screen operations and leave private mode afterwards.
 * @author martin
 */
public class TerminalScreen extends AbstractScreen {
    private final Terminal terminal;
    private boolean isStarted;
    private boolean fullRedrawHint;
    private ScrollHint scrollHint;

    /**
     * Creates a new Screen on top of a supplied terminal, will query the terminal for its size. The screen is initially
     * blank. The default character used for unused space (the newly initialized state of the screen and new areas after
     * expanding the terminal size) will be a blank space in 'default' ANSI front- and background color.
     * <p>
     * Before you can display the content of this buffered screen to the real underlying terminal, you must call the
     * {@code startScreen()} method. This will ask the terminal to enter private mode (which is required for Screens to
     * work properly). Similarly, when you are done, you should call {@code stopScreen()} which will exit private mode.
     *
     * @param terminal Terminal object to create the DefaultScreen on top of
     * @throws java.io.IOException If there was an underlying I/O error when querying the size of the terminal
     */
    public TerminalScreen(Terminal terminal) throws IOException {
        this(terminal, DEFAULT_CHARACTER);
    }

    /**
     * Creates a new Screen on top of a supplied terminal, will query the terminal for its size. The screen is initially
     * blank. The default character used for unused space (the newly initialized state of the screen and new areas after
     * expanding the terminal size) will be a blank space in 'default' ANSI front- and background color.
     * <p>
     * Before you can display the content of this buffered screen to the real underlying terminal, you must call the
     * {@code startScreen()} method. This will ask the terminal to enter private mode (which is required for Screens to
     * work properly). Similarly, when you are done, you should call {@code stopScreen()} which will exit private mode.
     *
     * @param terminal Terminal object to create the DefaultScreen on top of.
     * @param defaultCharacter What character to use for the initial state of the screen and expanded areas
     * @throws java.io.IOException If there was an underlying I/O error when querying the size of the terminal
     */
    public TerminalScreen(Terminal terminal, TextCharacter defaultCharacter) throws IOException {
        super(terminal.getTerminalSize(), defaultCharacter);
        this.terminal = terminal;
        this.terminal.addResizeListener(new TerminalScreenResizeListener());
        this.isStarted = false;
        this.fullRedrawHint = true;
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
        this.fullRedrawHint = true;
        TerminalPosition cursorPosition = getCursorPosition();
        if(cursorPosition != null) {
            getTerminal().setCursorVisible(true);
            getTerminal().setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
        } else {
            getTerminal().setCursorVisible(false);
        }
    }

    @Override
    public void stopScreen() throws IOException {
        stopScreen(true);
    }
    
    public synchronized void stopScreen(boolean flushInput) throws IOException {
        if(!isStarted) {
            return;
        }

        if (flushInput) {
            //Drain the input queue
            KeyStroke keyStroke;
            do {
                keyStroke = pollInput();
            }
            while(keyStroke != null && keyStroke.getKeyType() != KeyType.EOF);
        }

        getTerminal().exitPrivateMode();
        isStarted = false;
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
        else if(refreshType == RefreshType.AUTOMATIC &&
                (scrollHint == null || scrollHint == ScrollHint.INVALID)) {
            double threshold = getTerminalSize().getRows() * getTerminalSize().getColumns() * 0.75;
            if(getBackBuffer().isVeryDifferent(getFrontBuffer(), (int) threshold)) {
                refreshFull();
            }
            else {
                refreshByDelta();
            }
        }
        else {
            refreshByDelta();
        }
        getBackBuffer().copyTo(getFrontBuffer());
        TerminalPosition cursorPosition = getCursorPosition();
        if(cursorPosition != null) {
            getTerminal().setCursorVisible(true);
            //If we are trying to move the cursor to the padding of a CJK character, put it on the actual character instead
            if(cursorPosition.getColumn() > 0 && TerminalTextUtils.isCharCJK(getFrontBuffer().getCharacterAt(cursorPosition.withRelativeColumn(-1)).getCharacter())) {
                getTerminal().setCursorPosition(cursorPosition.getColumn() - 1, cursorPosition.getRow());
            }
            else {
                getTerminal().setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
            }
        } else {
            getTerminal().setCursorVisible(false);
        }
        getTerminal().flush();
    }

    private void useScrollHint() throws IOException {
        if (scrollHint == null) { return; }

        try {
            if (scrollHint == ScrollHint.INVALID) { return; }
            Terminal term = getTerminal();
            if (term instanceof Scrollable) {
                // just try and see if it cares:
                scrollHint.applyTo( (Scrollable)term );
                // if that didn't throw, then update front buffer:
                scrollHint.applyTo( getFrontBuffer() );
            }
        }
        catch (UnsupportedOperationException uoe) { /* ignore */ }
        finally { scrollHint = null; }
    }

    private void refreshByDelta() throws IOException {
        Map<TerminalPosition, TextCharacter> updateMap = new TreeMap<TerminalPosition, TextCharacter>(new ScreenPointComparator());
        TerminalSize terminalSize = getTerminalSize();

        useScrollHint();

        for(int y = 0; y < terminalSize.getRows(); y++) {
            for(int x = 0; x < terminalSize.getColumns(); x++) {
                TextCharacter backBufferCharacter = getBackBuffer().getCharacterAt(x, y);
                if(!backBufferCharacter.equals(getFrontBuffer().getCharacterAt(x, y))) {
                    updateMap.put(new TerminalPosition(x, y), backBufferCharacter);
                }
                if(TerminalTextUtils.isCharCJK(backBufferCharacter.getCharacter())) {
                    x++;    //Skip the trailing padding
                }
            }
        }

        if(updateMap.isEmpty()) {
            return;
        }
        TerminalPosition currentPosition = updateMap.keySet().iterator().next();
        getTerminal().setCursorPosition(currentPosition.getColumn(), currentPosition.getRow());

        TextCharacter firstScreenCharacterToUpdate = updateMap.values().iterator().next();
        EnumSet<SGR> currentSGR = firstScreenCharacterToUpdate.getModifiers();
        getTerminal().resetColorAndSGR();
        for(SGR sgr: currentSGR) {
            getTerminal().enableSGR(sgr);
        }
        TextColor currentForegroundColor = firstScreenCharacterToUpdate.getForegroundColor();
        TextColor currentBackgroundColor = firstScreenCharacterToUpdate.getBackgroundColor();
        getTerminal().setForegroundColor(currentForegroundColor);
        getTerminal().setBackgroundColor(currentBackgroundColor);
        for(TerminalPosition position: updateMap.keySet()) {
            if(!position.equals(currentPosition)) {
                getTerminal().setCursorPosition(position.getColumn(), position.getRow());
                currentPosition = position;
            }
            TextCharacter newCharacter = updateMap.get(position);
            if(!currentForegroundColor.equals(newCharacter.getForegroundColor())) {
                getTerminal().setForegroundColor(newCharacter.getForegroundColor());
                currentForegroundColor = newCharacter.getForegroundColor();
            }
            if(!currentBackgroundColor.equals(newCharacter.getBackgroundColor())) {
                getTerminal().setBackgroundColor(newCharacter.getBackgroundColor());
                currentBackgroundColor = newCharacter.getBackgroundColor();
            }
            for(SGR sgr: SGR.values()) {
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
            if(TerminalTextUtils.isCharCJK(newCharacter.getCharacter())) {
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
        getTerminal().resetColorAndSGR();
        scrollHint = null; // discard any scroll hint for full refresh

        EnumSet<SGR> currentSGR = EnumSet.noneOf(SGR.class);
        TextColor currentForegroundColor = TextColor.ANSI.DEFAULT;
        TextColor currentBackgroundColor = TextColor.ANSI.DEFAULT;
        for(int y = 0; y < getTerminalSize().getRows(); y++) {
            getTerminal().setCursorPosition(0, y);
            int currentColumn = 0;
            for(int x = 0; x < getTerminalSize().getColumns(); x++) {
                TextCharacter newCharacter = getBackBuffer().getCharacterAt(x, y);
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
                for(SGR sgr: SGR.values()) {
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
                if(TerminalTextUtils.isCharCJK(newCharacter.getCharacter())) {
                    //CJK characters take up two columns
                    currentColumn += 2;
                    x++;
                }
                else {
                    //Normal characters take up one column
                    currentColumn += 1;
                }
            }
        }
    }
    
    /**
     * Returns the underlying {@code Terminal} interface that this Screen is using. 
     * <p>
     * <b>Be aware:</b> directly modifying the underlying terminal will most likely result in unexpected behaviour if
     * you then go on and try to interact with the Screen. The Screen's back-buffer/front-buffer will not know about
     * the operations you are going on the Terminal and won't be able to properly generate a refresh unless you enforce
     * a {@code Screen.RefreshType.COMPLETE}, at which the entire terminal area will be repainted according to the 
     * back-buffer of the {@code Screen}.
     * @return Underlying terminal used by the screen
     */
    @SuppressWarnings("WeakerAccess")
    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return terminal.readInput();
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        return terminal.pollInput();
    }

    @Override
    public synchronized void clear() {
        super.clear();
        fullRedrawHint = true;
        scrollHint = ScrollHint.INVALID;
    }

    @Override
    public synchronized TerminalSize doResizeIfNecessary() {
        TerminalSize newSize = super.doResizeIfNecessary();
        if(newSize != null) {
            fullRedrawHint = true;
        }
        return newSize;
    }
    
    /**
     * Perform the scrolling and save scroll-range and distance in order
     * to be able to optimize Terminal-update later.
     */
    @Override
    public void scrollLines(int firstLine, int lastLine, int distance) {
        // just ignore certain kinds of garbage:
        if (distance == 0 || firstLine > lastLine) { return; }

        super.scrollLines(firstLine, lastLine, distance);

        // Save scroll hint for next refresh:
        ScrollHint newHint = new ScrollHint(firstLine,lastLine,distance);
        if (scrollHint == null) {
            // no scroll hint yet: use the new one:
            scrollHint = newHint;
        } else //noinspection StatementWithEmptyBody
            if (scrollHint == ScrollHint.INVALID) {
            // scroll ranges already inconsistent since latest refresh!
            // leave at INVALID
        } else if (scrollHint.matches(newHint)) {
            // same range: just accumulate distance:
            scrollHint.distance += newHint.distance;
        } else {
            // different scroll range: no scroll-optimization for next refresh
            this.scrollHint = ScrollHint.INVALID;
        }
    }

    private class TerminalScreenResizeListener implements TerminalResizeListener {
        @Override
        public void onResized(Terminal terminal, TerminalSize newSize) {
            addResizeRequest(newSize);
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

    private static class ScrollHint {
        public static final ScrollHint INVALID = new ScrollHint(-1,-1,0);
        public final int firstLine;
        public final int lastLine;
        public int distance;

        public ScrollHint(int firstLine, int lastLine, int distance) {
            this.firstLine = firstLine;
            this.lastLine = lastLine;
            this.distance = distance;
        }

        public boolean matches(ScrollHint other) {
            return this.firstLine == other.firstLine
                && this.lastLine == other.lastLine;
        }

        public void applyTo( Scrollable scr ) throws IOException {
            scr.scrollLines(firstLine, lastLine, distance);
        }
    }

}
