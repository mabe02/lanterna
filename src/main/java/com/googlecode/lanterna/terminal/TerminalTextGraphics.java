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
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.common.AbstractTextGraphics;
import com.googlecode.lanterna.common.TextCharacter;
import com.googlecode.lanterna.common.TextGraphics;

import java.io.IOException;

/**
 * This is the Terminal's implementation of TextGraphics. Upon creation it takes a snapshot for the Terminal's size, so
 * that it won't require to do an expensive lookup on every call to {@code getSize()}, but this also means that it can
 * go stale quickly if the terminal is resized. You should try to use the object quickly and then let it be GC:ed. It
 * will not pick up on terminal resizes! Also, the state of the Terminal after an operation performed by this
 * TextGraphics implementation is undefined and you should probably re-initialize colors and modifiers.
 * <p/>
 * Any write operation that results in an IOException will be wrapped by a RuntimeException since the TextGraphics
 * interface doesn't allow throwing IOException
 */
class TerminalTextGraphics extends AbstractTextGraphics {

    private final Terminal terminal;
    private final TerminalSize terminalSize;

    private boolean inManagedCall;
    private TextCharacter lastCharacter;
    private TerminalPosition lastPosition;

    TerminalTextGraphics(Terminal terminal) throws IOException {
        this.terminal = terminal;
        this.terminalSize = terminal.getTerminalSize();
        this.inManagedCall = false;
        this.lastCharacter = null;
        this.lastPosition = null;
    }

    @Override
    protected synchronized void setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter) {
        try {
            if(inManagedCall) {
                if(lastCharacter != null && !lastCharacter.equals(textCharacter)) {
                    applyGraphicState(textCharacter);
                    lastCharacter = textCharacter;
                }
                if(lastPosition != null && !lastPosition.equals(columnIndex, rowIndex)) {
                    terminal.setCursorPosition(columnIndex, rowIndex);
                    lastPosition = new TerminalPosition(columnIndex, rowIndex);
                }
            }
            else {
                terminal.setCursorPosition(columnIndex, rowIndex);
                applyGraphicState(textCharacter);
            }
            terminal.putCharacter(textCharacter.getCharacter());
            if(inManagedCall) {
                lastPosition = new TerminalPosition(columnIndex + 1, rowIndex);
            }
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyGraphicState(TextCharacter textCharacter) throws IOException {
        terminal.resetAllSGR();
        terminal.setForegroundColor(textCharacter.getForegroundColor());
        terminal.setBackgroundColor(textCharacter.getBackgroundColor());
        for(Terminal.SGR sgr: textCharacter.getModifiers()) {
            terminal.enableSGR(sgr);
        }
    }

    @Override
    public TerminalSize getSize() {
        return terminalSize;
    }

    @Override
    public synchronized void drawLine(TerminalPosition toPoint, char character) {
        try {
            inManagedCall = true;
            super.drawLine(toPoint, character);
        }
        finally {
            inManagedCall = false;
            lastPosition = null;
            lastCharacter = null;
        }
    }

    @Override
    public synchronized void drawTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        try {
            inManagedCall = true;
            super.drawTriangle(p1, p2, character);
        }
        finally {
            inManagedCall = false;
            lastPosition = null;
            lastCharacter = null;
        }
    }

    @Override
    public synchronized void fillTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        try {
            inManagedCall = true;
            super.fillTriangle(p1, p2, character);
        }
        finally {
            inManagedCall = false;
            lastPosition = null;
            lastCharacter = null;
        }
    }

    @Override
    public synchronized void fillRectangle(TerminalSize size, char character) {
        try {
            inManagedCall = true;
            super.fillRectangle(size, character);
        }
        finally {
            inManagedCall = false;
            lastPosition = null;
            lastCharacter = null;
        }
    }

    @Override
    public synchronized void drawRectangle(TerminalSize size, char character) {
        try {
            inManagedCall = true;
            super.drawRectangle(size, character);
        }
        finally {
            inManagedCall = false;
            lastPosition = null;
            lastCharacter = null;
        }
    }

    @Override
    public synchronized TextGraphics putString(String string) {
        try {
            inManagedCall = true;
            return super.putString(string);
        }
        finally {
            inManagedCall = false;
            lastPosition = null;
            lastCharacter = null;
        }
    }
}
