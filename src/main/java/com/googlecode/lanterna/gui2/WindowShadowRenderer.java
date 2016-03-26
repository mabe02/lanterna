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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * This WindowPostRenderer implementation draws a shadow under the window
 *
 * @author Martin
 */
public class WindowShadowRenderer implements WindowPostRenderer {
    @Override
    public void postRender(
            TextGraphics textGraphics,
            TextGUI textGUI,
            Window window) {

        TerminalPosition windowPosition = window.getPosition();
        TerminalSize decoratedWindowSize = window.getDecoratedSize();
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        textGraphics.enableModifiers(SGR.BOLD);
        TerminalPosition lowerLeft = windowPosition.withRelativeColumn(2).withRelativeRow(decoratedWindowSize.getRows());
        TerminalPosition lowerRight = lowerLeft.withRelativeColumn(decoratedWindowSize.getColumns() - 1);
        textGraphics.drawLine(lowerLeft, lowerRight, ' ');
        TerminalPosition upperRight = lowerRight.withRelativeRow(-decoratedWindowSize.getRows() + 1);
        textGraphics.drawLine(lowerRight, upperRight, ' ');

        //Fill the remaining hole
        upperRight = upperRight.withRelativeColumn(-1);
        lowerRight = lowerRight.withRelativeColumn(-1);
        textGraphics.drawLine(upperRight, lowerRight, ' ');

    }
}
