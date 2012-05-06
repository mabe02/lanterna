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

package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Helper class to write to a Screen, a bit like a pen in graphical environments.
 * @author mabe02
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

    public int getWidth()
    {
        return targetScreen.getWidth();
    }

    public int getHeight()
    {
        return targetScreen.getHeight();
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
        int screenWidth = targetScreen.getWidth();
        int screenHeight = targetScreen.getHeight();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < screenWidth; i++)
            sb.append(c);

        String line = sb.toString();
        for(int i = 0; i < screenHeight; i++) {
            drawString(0, i, line);
        }
    }

    public void drawString(final int x, final int y, final String string, final ScreenCharacterStyle... styles)
    {
        currentPosition.setColumn(x);
        currentPosition.setRow(y);

        Set<ScreenCharacterStyle> drawStyle = EnumSet.noneOf(ScreenCharacterStyle.class);
        drawStyle.addAll(Arrays.asList(styles));
        
        targetScreen.putString(x, y, string, foregroundColor, backgroundColor,
                drawStyle.contains(ScreenCharacterStyle.Bold), drawStyle.contains(ScreenCharacterStyle.Underline),
                drawStyle.contains(ScreenCharacterStyle.Reverse));
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
