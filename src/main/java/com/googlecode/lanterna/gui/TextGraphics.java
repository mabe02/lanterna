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

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.Arrays;
import java.util.EnumSet;

/**
 *
 * @author Martin
 */
public class TextGraphics
{
    private final TerminalPosition topLeft;
    private final TerminalSize areaSize;
    private final Screen screen;
    private Theme theme;
    private Color foregroundColor;
    private Color backgroundColor;
    private boolean currentlyBold;

    public TextGraphics(final TerminalPosition topLeft, final TerminalSize areaSize, final Screen screen, final Theme theme)
    {
        this.topLeft = topLeft;
        this.areaSize = areaSize;
        this.screen = screen;
        this.theme = theme;
        this.currentlyBold = false;
        this.foregroundColor = Color.DEFAULT;
        this.backgroundColor = Color.DEFAULT;
    }

    private TextGraphics(final TextGraphics graphics, final TerminalPosition topLeft, final TerminalSize areaSize)
    {
        this(new TerminalPosition(topLeft.getColumn() + graphics.topLeft.getColumn(), topLeft.getRow() + graphics.topLeft.getRow()),
                new TerminalSize(areaSize.getColumns() < (graphics.getWidth() - topLeft.getColumn()) ? areaSize.getColumns() : (graphics.getWidth() - topLeft.getColumn()),
                    areaSize.getRows() < (graphics.getHeight() - topLeft.getRow()) ? areaSize.getRows() : (graphics.getHeight() - topLeft.getRow())),
                graphics.screen, graphics.theme);
    }

    public TextGraphics subAreaGraphics(final TerminalPosition terminalPosition)
    {
        terminalPosition.ensurePositivePosition();
        TerminalSize newArea = new TerminalSize(areaSize);
        newArea.setColumns(newArea.getColumns() - terminalPosition.getColumn());
        newArea.setRows(newArea.getRows() - terminalPosition.getRow());
        return subAreaGraphics(terminalPosition, newArea);
    }

    public TextGraphics subAreaGraphics(final TerminalPosition topLeft, final TerminalSize subAreaSize)
    {
        if(topLeft.getColumn() < 0)
            topLeft.setColumn(0);
        if(topLeft.getRow() < 0)
            topLeft.setRow(0);
        if(subAreaSize.getColumns() < 0)
            subAreaSize.setColumns(-subAreaSize.getColumns());
        if(subAreaSize.getRows() < 0)
            subAreaSize.setRows(-subAreaSize.getRows());

        if(topLeft.getColumn() + subAreaSize.getColumns() > areaSize.getColumns())
            subAreaSize.setColumns(areaSize.getColumns() - topLeft.getColumn());
        if(topLeft.getRow() + subAreaSize.getRows() > areaSize.getRows())
            subAreaSize.setRows(areaSize.getRows() - topLeft.getRow());

        return new TextGraphics(this, topLeft, subAreaSize);
    }

    public void drawString(int column, int row, String string, ScreenCharacterStyle... styles)
    {
        if(column >= areaSize.getColumns() || row >= areaSize.getRows() || string == null)
            return;

        string = TabBehaviour.ALIGN_TO_COLUMN_4.replaceTabs(string, column + topLeft.getColumn());
        
        if(string.length() + column > areaSize.getColumns())
            string = string.substring(0, areaSize.getColumns() - column);

        EnumSet<ScreenCharacterStyle> stylesSet = EnumSet.noneOf(ScreenCharacterStyle.class);
        if(styles != null && styles.length != 0)
            stylesSet = EnumSet.copyOf(Arrays.asList(styles));
        
        if(currentlyBold)
            stylesSet.add(ScreenCharacterStyle.Bold);

        screen.putString(column + topLeft.getColumn(), row + topLeft.getRow(), string,
                foregroundColor, backgroundColor, stylesSet);
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public Color getForegroundColor()
    {
        return foregroundColor;
    }

    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(Color foregroundColor)
    {
        this.foregroundColor = foregroundColor;
    }

    public int getWidth()
    {
        return areaSize.getColumns();
    }

    public int getHeight()
    {
        return areaSize.getRows();
    }

    public void setBoldMask(boolean enabledBoldMask)
    {
        currentlyBold = enabledBoldMask;
    }

    public Theme getTheme()
    {
        return theme;
    }

    public void applyThemeItem(Theme.Category category)
    {
        applyThemeItem(getTheme().getItem(category));
    }

    public TerminalPosition translateToGlobalCoordinates(TerminalPosition pointInArea)
    {
        return new TerminalPosition(pointInArea.getColumn() + topLeft.getColumn(), pointInArea.getRow() + topLeft.getRow());
    }

    public void applyThemeItem(Theme.Definition themeItem)
    {
        setForegroundColor(themeItem.foreground);
        setBackgroundColor(themeItem.background);
        setBoldMask(themeItem.highlighted);
    }

    public void fillArea(char character)
    {
        fillRectangle(character, new TerminalPosition(0, 0), new TerminalSize(areaSize));
    }

    public void fillRectangle(char character, TerminalPosition topLeft, TerminalSize rectangleSize)
    {
        StringBuilder emptyLineBuilder = new StringBuilder();
        for(int i = 0; i < rectangleSize.getColumns(); i++)
            emptyLineBuilder.append(character);
        String emptyLine = emptyLineBuilder.toString();
        for(int i = 0; i < rectangleSize.getRows(); i++)
            drawString(topLeft.getColumn(), topLeft.getRow() + i, emptyLine);
    }

    @Override
    public String toString()
    {
        return "TextGraphics {topLeft: " + topLeft.toString() + ", size: " + areaSize.toString() + "}";
    }
}
