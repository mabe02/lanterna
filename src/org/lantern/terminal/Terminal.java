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
    public void applySGR(SGR[] options) throws LanternException;
    public void applyForegroundColor(Color color) throws LanternException;
    public void applyBackgroundColor(Color color) throws LanternException;
    public void setEcho(boolean echoOn) throws LanternException;
    public void setCBreak(boolean cbreakOn) throws LanternException;
    public void addInputProfile(KeyMappingProfile profile);
    public void addResizeListener(ResizeListener listener);
    public void removeResizeListener(ResizeListener listener);
    public void hackSendFakeResize() throws LanternException;
    public TerminalSize queryTerminalSize() throws LanternException;

    public static class SGR
    {
        public static final int RESET_ALL_ID = 0;
        public static final int ENTER_BOLD_ID = 1;
        public static final int ENTER_REVERSE_ID = 2;
        public static final int ENTER_UNDERLINE_ID = 3;
        public static final int ENTER_BLINK_ID = 4;
        public static final int EXIT_BOLD_ID = 5;
        public static final int EXIT_REVERSE_ID = 6;
        public static final int EXIT_UNDERLINE_ID = 7;
        
        public static final SGR RESET_ALL = new SGR(RESET_ALL_ID);
        public static final SGR ENTER_BOLD = new SGR(ENTER_BOLD_ID);
        public static final SGR ENTER_REVERSE = new SGR(ENTER_REVERSE_ID);
        public static final SGR ENTER_UNDERLINE = new SGR(ENTER_UNDERLINE_ID);
        public static final SGR ENTER_BLINK = new SGR(ENTER_BLINK_ID);
        public static final SGR EXIT_BOLD = new SGR(EXIT_BOLD_ID);
        public static final SGR EXIT_REVERSE = new SGR(EXIT_REVERSE_ID);
        public static final SGR EXIT_UNDERLINE = new SGR(EXIT_UNDERLINE_ID);
        
        private final int index;

        private SGR(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Color
    {
        public static final int BLACK_ID = 0;
        public static final int RED_ID = 1;
        public static final int GREEN_ID = 2;
        public static final int YELLOW_ID = 3;
        public static final int BLUE_ID = 4;
        public static final int MAGENTA_ID = 5;
        public static final int CYAN_ID = 6;
        public static final int WHITE_ID = 7;
        public static final int DEFAULT_ID = 9;
        
        public static final Color BLACK = new Color(BLACK_ID);
        public static final Color RED = new Color(RED_ID);
        public static final Color GREEN = new Color(GREEN_ID);
        public static final Color YELLOW = new Color(YELLOW_ID);
        public static final Color BLUE = new Color(BLUE_ID);
        public static final Color MAGENTA = new Color(MAGENTA_ID);
        public static final Color CYAN = new Color(CYAN_ID);
        public static final Color WHITE = new Color(WHITE_ID);
        public static final Color DEFAULT = new Color(DEFAULT_ID);
        
        private final int index;

        private Color(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Style
    {
        public static final Style Bold = new Style(1);
        public static final Style Underline = new Style(2);
        public static final Style Reverse = new Style(3);
        public static final Style Blinking = new Style(4);
        
        private final int index;

        private Style(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public interface ResizeListener
    {
        public void onResized(TerminalSize newSize);
    }
}
