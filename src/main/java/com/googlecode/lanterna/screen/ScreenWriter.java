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

package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;

/**
 * Helper class to write to a Screen, a bit like a pen in graphical environments.
 * @author Martin
 */
public class ScreenWriter
{
    private final Screen targetScreen;
    private final TerminalPosition currentPosition;
    private Terminal.Color foregroundColor;
    private Terminal.Color backgroundColor;

    public ScreenWriter(final Screen targetScreen)
    {
        this.foregroundColor = Color.DEFAULT;
        this.backgroundColor = Color.DEFAULT;
        this.targetScreen = targetScreen;
        this.currentPosition = new TerminalPosition(0, 0);
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(final Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public Color getForegroundColor()
    {
        return foregroundColor;
    }

    public void setForegroundColor(final Color foregroundColor)
    {
        this.foregroundColor = foregroundColor;
    }

    public void fillScreen(char c)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < targetScreen.getTerminalSize().getColumns(); i++)
            sb.append(c);

        String line = sb.toString();
        for(int i = 0; i < targetScreen.getTerminalSize().getRows(); i++) {
            drawString(0, i, line);
        }
    }

    /**
     * Draws a string on the screen at a particular position
     * @param x 0-indexed column number of where to put the first character in the string
     * @param y 0-indexed row number of where to put the first character in the string
     * @param string Text to put on the screen
     * @param styles Additional styles to apply to the text
     */
    public void drawString(final int x, final int y, final String string, final ScreenCharacterStyle... styles)
    {
        currentPosition.setColumn(x);
        currentPosition.setRow(y);        
        targetScreen.putString(x, y, string, foregroundColor, backgroundColor, styles);
        currentPosition.setColumn(currentPosition.getColumn() + string.length());
    }

    @Override
    public int hashCode()
    {
        return targetScreen.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof ScreenWriter == false)
            return false;

        return targetScreen.equals(((ScreenWriter)(obj)).targetScreen);
    }
}
