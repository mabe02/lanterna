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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.util.EnumSet;

/**
 *
 * @author Martin
 */
public class ScreenBackendTextGUIGraphics implements TextGUIGraphics {
    
    private final Screen screen;
    private final TerminalPosition topLeftPosition;
    private final TerminalSize drawableAreaSize;
    private final EnumSet<ScreenCharacterStyle> enabledCharacterStyles;
    private TextColor foregroundColor;
    private TextColor backgroundColor;

    public ScreenBackendTextGUIGraphics(Screen screen) {
        this(screen, new TerminalPosition(0, 0), screen.getTerminalSize());
    }

    public ScreenBackendTextGUIGraphics(Screen screen, TerminalPosition topLeftPosition, TerminalSize drawableAreaSize) {
        this.screen = screen;
        this.topLeftPosition = topLeftPosition;
        this.drawableAreaSize = drawableAreaSize;
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.enabledCharacterStyles = EnumSet.noneOf(ScreenCharacterStyle.class);
    }

    @Override
    public void setForegroundColor(TextColor color) {
        if(color == null) {
            throw new IllegalArgumentException("Cannot set foreground color to null");
        }
        this.foregroundColor = color;
    }

    @Override
    public void setBackgroundColor(TextColor color) {
        if(color == null) {
            throw new IllegalArgumentException("Cannot set background color to null");
        }
        this.backgroundColor = color;        
    }
    
    @Override
    public void resetCharacterStyles() {
        enabledCharacterStyles.clear();
    }
    
    @Override
    public void setStyleBold(boolean isBold) {
        if(isBold) {
            enabledCharacterStyles.add(ScreenCharacterStyle.Bold);
        }
        else {
            enabledCharacterStyles.remove(ScreenCharacterStyle.Bold);
        }
    }
    
    @Override
    public void setStyleBlink(boolean isBlinking) {
        if(isBlinking) {
            enabledCharacterStyles.add(ScreenCharacterStyle.Blinking);
        }
        else {
            enabledCharacterStyles.remove(ScreenCharacterStyle.Blinking);
        }
    }
    
    @Override
    public void setStyleReverse(boolean isReverse) {
        if(isReverse) {
            enabledCharacterStyles.add(ScreenCharacterStyle.Reverse);
        }
        else {
            enabledCharacterStyles.remove(ScreenCharacterStyle.Reverse);
        }
    }
    
    @Override
    public void setStyleUnderline(boolean isUnderlined) {
        if(isUnderlined) {
            enabledCharacterStyles.add(ScreenCharacterStyle.Underline);
        }
        else {
            enabledCharacterStyles.remove(ScreenCharacterStyle.Underline);
        }
    }
    
    @Override
    public boolean isStyleBold() {
        return enabledCharacterStyles.contains(ScreenCharacterStyle.Bold);
    }
    
    @Override
    public boolean isStyleBlink() {
        return enabledCharacterStyles.contains(ScreenCharacterStyle.Blinking);
    }
    
    @Override
    public boolean isStyleReverse() {
        return enabledCharacterStyles.contains(ScreenCharacterStyle.Reverse);
    }
    
    @Override
    public boolean isStyleUnderline() {
        return enabledCharacterStyles.contains(ScreenCharacterStyle.Underline);
    }

    @Override
    public void fill(char character) {
        StringBuilder sb = new StringBuilder(drawableAreaSize.getColumns());
        for(int i = 0; i < drawableAreaSize.getColumns(); i++) {
            sb.append(character);
        }
        String emptyLine = sb.toString();
        for(int row = 0; row < drawableAreaSize.getRows(); row++) {
            drawString(0, row, emptyLine);
        }
    }

    @Override
    public void drawString(int xOffset, int yOffset, String text) {
        
        if(yOffset < 0 || yOffset >= drawableAreaSize.getRows()) {
            return;
        }
        if(xOffset >= drawableAreaSize.getColumns()) {
            return;
        }
        
        if(xOffset < 0) {
            text = text.substring(Math.min(text.length(), Math.abs(xOffset)));
        }
        if(xOffset + text.length() > drawableAreaSize.getColumns()) {
            text = text.substring(0, drawableAreaSize.getColumns() - xOffset);
        }
        
        screen.putString(
                xOffset + topLeftPosition.getColumn(), 
                yOffset + topLeftPosition.getRow(), 
                text, 
                foregroundColor, 
                backgroundColor, 
                enabledCharacterStyles);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return clone(null, null);
    }
    
    @Override
    public TextGUIGraphics clone(TerminalPosition newTopLeft, TerminalSize newSize) {
        
        if(newSize.getColumns() == 0 || newSize.getRows() == 0) {
            return new NullTextGUIGraphics();
        }
        
        //Resize the cloned graphics area if necessary, to make sure it fits within this graphics area
        //We have to do this before converting newTopLeft to global coordinates
        if(newTopLeft.getColumn() + newSize.getColumns() >= drawableAreaSize.getColumns()) {
            newSize = newSize.withColumns(drawableAreaSize.getColumns() - newTopLeft.getColumn());
        }
        if(newTopLeft.getRow() + newSize.getRows() >= drawableAreaSize.getRows()) {
            newSize = newSize.withRows(drawableAreaSize.getRows() - newTopLeft.getRow());
        }
        
        //Recalculate to global coordinates
        newTopLeft = new TerminalPosition(
            topLeftPosition.getColumn() + newTopLeft.getColumn(),
            topLeftPosition.getRow() + newTopLeft.getRow());
        
        if(newTopLeft.getColumn() >= drawableAreaSize.getColumns() ||
                newTopLeft.getRow() >= drawableAreaSize.getRows()) {
            return new NullTextGUIGraphics();
        }
        
        return new ScreenBackendTextGUIGraphics(screen, newTopLeft, newSize);
    }
}
