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

package org.lantern.terminal;

import org.lantern.LanternException;
import org.lantern.input.InputProvider;
import org.lantern.input.KeyMappingProfile;

/**
 * This is the main terminal interface, at the lowest level supported by Lantern.
 * You can implement your own implementation of this if you want to target an
 * exotic text terminal specification or another graphical environment (like SWT).
 * @author mabe02
 */
public interface Terminal extends InputProvider
{
    public void enterPrivateMode() throws LanternException;
    public void exitPrivateMode() throws LanternException;
    public void clearScreen() throws LanternException;
    public void moveCursor(int x, int y) throws LanternException;
    public void putCharacter(char c) throws LanternException;
    public void applySGR(SGR... options) throws LanternException;
    public void applyForegroundColor(Color color) throws LanternException;
    public void applyBackgroundColor(Color color) throws LanternException;
    public void setEcho(boolean echoOn) throws LanternException;
    public void setCBreak(boolean cbreakOn) throws LanternException;
    public void addInputProfile(KeyMappingProfile profile);
    public void addResizeListener(ResizeListener listener);
    public void removeResizeListener(ResizeListener listener);
    public void hackSendFakeResize() throws LanternException;
    public TerminalSize queryTerminalSize() throws LanternException;

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
