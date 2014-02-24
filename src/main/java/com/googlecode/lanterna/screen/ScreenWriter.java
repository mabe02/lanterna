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

package com.googlecode.lanterna.screen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TextColor;

/**
 * Helper class to write to a Screen, a bit like a pen in graphical environments.
 * @author Martin
 */
public class ScreenWriter
{
    private final Screen targetScreen;
    private TerminalPosition currentPosition;
    private TextColor foregroundColor;
    private TextColor backgroundColor;

    public ScreenWriter(final Screen targetScreen)
    {
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.targetScreen = targetScreen;
        this.currentPosition = new TerminalPosition(0, 0);
    }

    public TextColor getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(final TextColor backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public TextColor getForegroundColor()
    {
        return foregroundColor;
    }

    public Screen getTargetScreen() {
		return targetScreen;
	}

    public void setForegroundColor(final TextColor foregroundColor)
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
    	if (! string.contains("\n") && ! string.contains("\r")) { 
    		currentPosition = currentPosition.withColumn(x).withRow(y);
    		targetScreen.putString(x, y, string, foregroundColor, backgroundColor, styles);
    		currentPosition = currentPosition.withColumn(currentPosition.getColumn() + string.length());
    	} else {
    		currentPosition = currentPosition.withColumn(x).withRow(y);
    		int lines=1;
    		for (String line : string.split("[\n\r]")) {
    			lines++;
    			targetScreen.putString(x, y+lines, line, foregroundColor, backgroundColor, styles);
    		}
    		currentPosition = currentPosition
    				.withRow(currentPosition.getRow() + lines)
    				.withColumn(currentPosition.getColumn() + string.length());
    	}
    }

    public void drawCharacter(final int x, final int y, final char character, final ScreenCharacterStyle... styles) {
    	Set<ScreenCharacterStyle> styleSet = new HashSet<ScreenCharacterStyle>();
    	styleSet.addAll(Arrays.asList(styles));
    	ScreenCharacter screenCharacter = new ScreenCharacter(character, getForegroundColor(), getBackgroundColor(), styleSet);
    	targetScreen.putCharacter(x, y, screenCharacter);
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
