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
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import java.util.LinkedList;

/**
 *
 * @author Martin
 */
public class StackedModalWindowManager implements WindowManager {
    
    public static final Hint LOCATION_CENTERED = new Hint();
    public static final Hint LOCATION_CASCADE = new Hint();
    private static final int CASCADE_SHIFT_RIGHT = 2;
    private static final int CASCADE_SHIFT_DOWN = 1;
    
    private final LinkedList<Window> windowStack;
    private final LinkedList<TerminalPosition> topLeftPositions;
    private TerminalPosition nextTopLeftPosition;

    public StackedModalWindowManager() {
        this.windowStack = new LinkedList<Window>();
        this.topLeftPositions = new LinkedList<TerminalPosition>();
        nextTopLeftPosition = new TerminalPosition(CASCADE_SHIFT_RIGHT, CASCADE_SHIFT_DOWN);
    }    

    @Override
    public synchronized void addWindow(Window window, Hint... windowManagerHints) {
        if(window == null) {
            throw new IllegalArgumentException("Cannot call addWindow(...) with null window");
        }
        windowStack.add(window);
        if(isCentered(windowManagerHints)) {
            topLeftPositions.add(null);
        }
        else {
            topLeftPositions.add(nextTopLeftPosition);
            nextTopLeftPosition = nextTopLeftPosition
                                    .withColumn(nextTopLeftPosition.getColumn() + CASCADE_SHIFT_RIGHT)
                                    .withRow(nextTopLeftPosition.getRow() + CASCADE_SHIFT_DOWN);
        }
    }

    @Override
    public synchronized void removeWindow(Window window) {
        if(window == null) {
            throw new IllegalArgumentException("Cannot call removeWindow(...) with null window");
        }
        int index = windowStack.indexOf(window);
        if(index == -1) {
            throw new IllegalArgumentException("Unknown window passed to removeWindow(...), this window manager doesn't"
                    + " contain " + window);
        }
        topLeftPositions.remove(index);
        windowStack.remove(index);
    }

    @Override
    public synchronized Window getActiveWindow() {
        if(windowStack.isEmpty()) {
            return null;
        }
        else {
            return windowStack.getLast();
        }
    }

    @Override
    public synchronized boolean handleInput(Key key) {
        if(windowStack.isEmpty()) {
            return false;
        }
        
        return windowStack.getLast().handleInput(key);
    }

    private boolean isCentered(Hint... windowManagerHints) {
        for(Hint hint: windowManagerHints) {
            if(hint == LOCATION_CENTERED) {
                return true;
            }
        }
        return false;
    }
    
}
