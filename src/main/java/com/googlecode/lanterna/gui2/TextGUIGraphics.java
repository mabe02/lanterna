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

import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;

/**
 *
 * @author Martin
 */
public interface TextGUIGraphics {

    void setForegroundColor(TextColor color);

    void setBackgroundColor(TextColor color);

    boolean isStyleBlink();

    boolean isStyleBold();

    boolean isStyleReverse();

    boolean isStyleUnderline();

    void resetCharacterStyles();

    void setStyleBlink(boolean isBlinking);

    void setStyleBold(boolean isBold);

    void setStyleReverse(boolean isReverse);

    void setStyleUnderline(boolean isUnderlined);

    void fill(char character);
    
    void drawString(int xOffset, int yOffset, String text);
    
    TextGUIGraphics clone(TerminalPosition newTopLeft, TerminalSize newSize);
}
