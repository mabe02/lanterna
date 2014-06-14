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

import com.googlecode.lanterna.input.KeyStroke;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Interface extending Terminal that removes the IOException throw clause. You can for example use this instead of 
 * Terminal if you use an implementation that doesn't throw any IOExceptions or if you wrap your terminal in an 
 * IOSafeTerminalAdapter.
 * @author Martin
 */
public interface IOSafeTerminal extends Terminal {
    @Override
    public void enterPrivateMode();
    @Override
    public void exitPrivateMode();
    @Override
    public void clearScreen();
    @Override
    public void moveCursor(int x, int y);
    @Override
    public void setCursorVisible(boolean visible);
    @Override
    public void putCharacter(char c);
    @Override
    public void enableSGR(SGR sgr);
    @Override
    public void disableSGR(SGR sgr);
    @Override
    public void resetAllSGR();
    @Override
    public void applyForegroundColor(TextColor color);
    @Override
    public void applyBackgroundColor(TextColor color);
    @Override
    public TerminalSize getTerminalSize();
    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit);
    @Override
    public void flush();
    @Override
    KeyStroke readInput();
}
