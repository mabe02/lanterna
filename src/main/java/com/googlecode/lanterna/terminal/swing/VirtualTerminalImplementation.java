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
import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.AbstractTerminal;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author martin
 */
class VirtualTerminalImplementation extends AbstractTerminal implements IOSafeTerminal {

    interface DeviceEmulator {

        public KeyStroke readInput();

        public void enterPrivateMode();

        public void exitPrivateMode();

        public TextBuffer getBuffer();

        public void setCursorVisible(boolean visible);

        public void flush();

        public byte[] equireTerminal();

    }

    private final DeviceEmulator deviceEmulator;
    private TerminalSize terminalSize;
    private TerminalPosition currentPosition;
    private TextColor foregroundColor;
    private TextColor backgroundColor;
    private final EnumSet<SGR> activeSGRs;

    public VirtualTerminalImplementation(DeviceEmulator deviceEmulator, TerminalSize initialSize) {
        this.deviceEmulator = deviceEmulator;
        this.terminalSize = initialSize;
        this.currentPosition = TerminalPosition.TOP_LEFT_CORNER;
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.activeSGRs = EnumSet.noneOf(SGR.class);

        //Initialize lastKnownSize in AbstractTerminal
        onResized(terminalSize.getColumns(), terminalSize.getRows());
    }

    ///////////
    // Now implement all Terminal-related methods
    ///////////
    @Override
    public KeyStroke readInput() {
        return deviceEmulator.readInput();
    }

    @Override
    public void addKeyDecodingProfile(KeyDecodingProfile profile) {
    }

    @Override
    public void enterPrivateMode() {
        deviceEmulator.enterPrivateMode();
    }

    @Override
    public void exitPrivateMode() {
        deviceEmulator.exitPrivateMode();
    }

    @Override
    public void clearScreen() {
        int linesToAdd = terminalSize.getRows();
        for(int i = 0; i < linesToAdd; i++) {
            deviceEmulator.getBuffer().newLine(terminalSize);
        }
    }

    private void advanceCursor(char c) {
        currentPosition = currentPosition.withRelativeColumn(CJKUtils.isCharCJK(c) ? 2 : 1);
        if(currentPosition.getColumn() >= getTerminalSize().getColumns()) {
            currentPosition = currentPosition.withColumn(0).withRelativeRow(1);
        }
        correctCursorPosition();
    }

    @Override
    public void moveCursor(int x, int y) {
        currentPosition = currentPosition.withColumn(x).withRow(y);
        correctCursorPosition();
    }

    private void correctCursorPosition() {
        int x = currentPosition.getColumn();
        int y = currentPosition.getRow();
        TerminalSize size = getTerminalSize();
        if(x < 0) {
            currentPosition = currentPosition.withColumn(0);
        }
        if(x >= size.getColumns()) {
            currentPosition = currentPosition.withColumn(size.getColumns() - 1);
        }
        if(y < 0) {
            currentPosition = currentPosition.withRow(0);
        }
        else if(y >= size.getRows()) {
            currentPosition = currentPosition.withRow(size.getRows() - 1);
        }
    }

    @Override
    public void setCursorVisible(boolean visible) {
        deviceEmulator.setCursorVisible(visible);
    }

    public TerminalPosition getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void putCharacter(char c) {
        if(c == '\n') {
            if(currentPosition.getRow() == terminalSize.getRows() - 1) {
                deviceEmulator.getBuffer().newLine(getTerminalSize());
            }
            moveCursor(0, currentPosition.getRow() + 1);
        }
        else if (c == '\t') {
            for(int i = 0; i < 4 - currentPosition.getColumn() % 4; i++) {
                if(currentPosition.getColumn() < getTerminalSize().getColumns() - 1) {
                    putCharacter(' ');
                }
            }
        }
        else {
            deviceEmulator.getBuffer().setCharacter(getTerminalSize(), currentPosition, new TerminalCharacter(c, foregroundColor, backgroundColor, activeSGRs));
            advanceCursor(c);
        }
    }

    @Override
    public void enableSGR(SGR sgr) {
        activeSGRs.add(sgr);
    }

    @Override
    public void disableSGR(SGR sgr) {
        activeSGRs.remove(sgr);
    }

    @Override
    public void resetAllSGR() {
        activeSGRs.clear();
        foregroundColor = TextColor.ANSI.DEFAULT;
        backgroundColor = TextColor.ANSI.DEFAULT;
    }

    @Override
    public void setForegroundColor(TextColor color) {
        this.foregroundColor = color;
    }

    //We don't need to implement these, they will never be called since we override applyForegroundColor(TextColor)
    @Override
    protected void applyForegroundColor(TextColor.ANSI color) { }
    @Override
    protected void applyForegroundColor(int index) { }
    @Override
    protected void applyForegroundColor(int r, int g, int b) { }

    //We don't need to implement these, they will never be called since we override applyBackgroundColor(TextColor)
    @Override
    protected void applyBackgroundColor(TextColor.ANSI color) { }
    @Override
    protected void applyBackgroundColor(int index) { }
    @Override
    protected void applyBackgroundColor(int r, int g, int b) { }


    @Override
    public void setBackgroundColor(TextColor color) {
        this.backgroundColor = color;
    }

    void setTerminalSize(TerminalSize terminalSize) {
        onResized(terminalSize.getColumns(), terminalSize.getRows());
        if(!this.terminalSize.equals(terminalSize)) {
            if(this.terminalSize.getRows() < terminalSize.getRows()) {
                currentPosition = currentPosition.withRelativeRow(terminalSize.getRows() - this.terminalSize.getRows());
            }
            else if(this.terminalSize.getRows() > terminalSize.getRows()) {
                currentPosition = currentPosition.withRelativeRow(terminalSize.getRows() - this.terminalSize.getRows());
            }
        }
        this.terminalSize = terminalSize;
        correctCursorPosition();
    }

    @Override
    public TerminalSize getTerminalSize() {
        return terminalSize;
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return deviceEmulator.equireTerminal();
    }

    @Override
    public void flush() {
        deviceEmulator.flush();
    }
}
