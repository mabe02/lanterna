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

import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;

/**
 *
 * @author Martin
 */
public class NullTextGUIGraphics implements TextGUIGraphics {

    @Override
    public void setForegroundColor(TextColor color) {
    }

    @Override
    public void setBackgroundColor(TextColor color) {
    }

    @Override
    public boolean isStyleBlink() {
        return false;
    }

    @Override
    public boolean isStyleBold() {
        return false;
    }

    @Override
    public boolean isStyleReverse() {
        return false;
    }

    @Override
    public boolean isStyleUnderline() {
        return false;
    }

    @Override
    public void resetCharacterStyles() {
    }

    @Override
    public void setStyleBlink(boolean isBlinking) {
    }

    @Override
    public void setStyleBold(boolean isBold) {
    }

    @Override
    public void setStyleReverse(boolean isReverse) {
    }

    @Override
    public void setStyleUnderline(boolean isUnderlined) {
    }

    @Override
    public void fill(char character) {
    }

    @Override
    public void drawString(int xOffset, int yOffset, String text) {
    }

    @Override
    public TextGUIGraphics clone(TerminalPosition newTopLeft, TerminalSize newSize) {
        return this;
    }   
}
