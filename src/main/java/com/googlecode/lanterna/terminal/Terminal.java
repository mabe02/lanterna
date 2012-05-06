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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.KeyMappingProfile;

/**
 * This is the main terminal interface, at the lowest level supported by Lanterna.
 * You can implement your own implementation of this if you want to target an
 * exotic text terminal specification or another graphical environment (like SWT).
 * @author mabe02
 */
public interface Terminal extends InputProvider
{
    public void enterPrivateMode() throws LanternaException;
    public void exitPrivateMode() throws LanternaException;
    public void clearScreen() throws LanternaException;
    public void moveCursor(int x, int y) throws LanternaException;
    public void putCharacter(char c) throws LanternaException;
    public void applySGR(SGR... options) throws LanternaException;
    public void applyForegroundColor(Color color) throws LanternaException;
    public void applyBackgroundColor(Color color) throws LanternaException;
    public void setEcho(boolean echoOn) throws LanternaException;
    public void setCBreak(boolean cbreakOn) throws LanternaException;
    public void addInputProfile(KeyMappingProfile profile);
    public void addResizeListener(ResizeListener listener);
    public void removeResizeListener(ResizeListener listener);
    public TerminalSize queryTerminalSize() throws LanternaException;
    public void flush() throws LanternaException;

    public enum SGR
    {
        RESET_ALL,
        ENTER_BOLD,
        ENTER_REVERSE,
        ENTER_UNDERLINE,
        ENTER_BLINK,
        EXIT_BOLD,
        EXIT_REVERSE,
        EXIT_UNDERLINE
    }

    public enum Color
    {
        BLACK(0),
        RED(1),
        GREEN(2),
        YELLOW(3),
        BLUE(4),
        MAGENTA(5),
        CYAN(6),
        WHITE(7),
        DEFAULT(9);

        private int index;

        private Color(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public enum Style
    {
        Bold, 
        Underline, 
        Reverse, 
        Blinking
    }

    public interface ResizeListener
    {
        public void onResized(TerminalSize newSize);
    }
}
