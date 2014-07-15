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
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;

/**
 *
 * @author Martin
 */
public class ScreenBackendTextGUIGraphics implements TextGUIGraphics {
    
    private final ScreenWriter screenWriter;
    private final TerminalPosition topLeftPosition;
    private final TerminalSize drawableAreaSize;

    public ScreenBackendTextGUIGraphics(Screen screen) {
        this(new  ScreenWriter(screen), new TerminalPosition(0, 0), screen.getTerminalSize());
    }

    public ScreenBackendTextGUIGraphics(ScreenWriter screenWriter, TerminalPosition topLeftPosition, TerminalSize drawableAreaSize) {
        this.screenWriter = screenWriter;
        this.topLeftPosition = topLeftPosition;
        this.drawableAreaSize = drawableAreaSize;
    }

    @Override
    public void setForegroundColor(TextColor color) {
        if(color == null) {
            throw new IllegalArgumentException("Cannot set foreground color to null");
        }
        screenWriter.setForegroundColor(color);
    }

    @Override
    public void setBackgroundColor(TextColor color) {
        if(color == null) {
            throw new IllegalArgumentException("Cannot set background color to null");
        }
        screenWriter.setBackgroundColor(color);
    }
    
    @Override
    public void resetCharacterStyles() {
        screenWriter.clearModifiers();
    }
    
    @Override
    public void setStyleBold(boolean isBold) {
        if(isBold) {
            screenWriter.enableModifiers(Terminal.SGR.BOLD);
        }
        else {
            screenWriter.disableModifiers(Terminal.SGR.BOLD);
        }
    }
    
    @Override
    public void setStyleBlink(boolean isBlinking) {
        if(isBlinking) {
            screenWriter.enableModifiers(Terminal.SGR.BLINK);
        }
        else {
            screenWriter.disableModifiers(Terminal.SGR.BLINK);
        }
    }
    
    @Override
    public void setStyleReverse(boolean isReverse) {
        if(isReverse) {
            screenWriter.enableModifiers(Terminal.SGR.REVERSE);
        }
        else {
            screenWriter.disableModifiers(Terminal.SGR.REVERSE);
        }
    }
    
    @Override
    public void setStyleUnderline(boolean isUnderlined) {
        if(isUnderlined) {
            screenWriter.enableModifiers(Terminal.SGR.UNDERLINE);
        }
        else {
            screenWriter.disableModifiers(Terminal.SGR.UNDERLINE);
        }
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
        
        screenWriter.setPosition(new TerminalPosition(xOffset + topLeftPosition.getColumn(), yOffset + topLeftPosition.getRow()));
    }

    //We don't care about this inspection since we know that the clone overload we are delegating to will always create
    //a new instance of this object
    @SuppressWarnings("CloneDoesntCallSuperClone")
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
        
        return new ScreenBackendTextGUIGraphics(screenWriter, newTopLeft, newSize);
    }
}
