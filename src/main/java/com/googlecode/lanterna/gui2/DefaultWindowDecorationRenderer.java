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

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * Default window decoration renderer that is used unless overridden with another decoration renderer. The windows are
 * drawn using a bevel colored line and the window title in the top-left corner, very similar to ordinary titled
 * borders.
 *
 * @author Martin
 */
public class DefaultWindowDecorationRenderer implements WindowDecorationRenderer {
    @Override
    public TextGUIGraphics draw(TextGUI textGUI, TextGUIGraphics graphics, Window window) {
        String title = window.getTitle();
        if(title == null) {
            title = "";
        }

        ThemeDefinition themeDefinition = graphics.getThemeDefinition(DefaultWindowDecorationRenderer.class);
        char horizontalLine = themeDefinition.getCharacter("HORIZONTAL_LINE", Symbols.SINGLE_LINE_HORIZONTAL);
        char verticalLine = themeDefinition.getCharacter("VERTICAL_LINE", Symbols.SINGLE_LINE_VERTICAL);
        char bottomLeftCorner = themeDefinition.getCharacter("BOTTOM_LEFT_CORNER", Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        char topLeftCorner = themeDefinition.getCharacter("TOP_LEFT_CORNER", Symbols.SINGLE_LINE_TOP_LEFT_CORNER);
        char bottomRightCorner = themeDefinition.getCharacter("BOTTOM_RIGHT_CORNER", Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
        char topRightCorner = themeDefinition.getCharacter("TOP_RIGHT_CORNER", Symbols.SINGLE_LINE_TOP_RIGHT_CORNER);

        TerminalSize drawableArea = graphics.getSize();
        graphics.applyThemeStyle(themeDefinition.getPreLight());
        graphics.drawLine(new TerminalPosition(0, drawableArea.getRows() - 2), new TerminalPosition(0, 1), verticalLine);
        graphics.drawLine(new TerminalPosition(1, 0), new TerminalPosition(drawableArea.getColumns() - 2, 0), horizontalLine);
        graphics.setCharacter(0, 0, topLeftCorner);
        graphics.setCharacter(0, drawableArea.getRows() - 1, bottomLeftCorner);

        graphics.applyThemeStyle(themeDefinition.getNormal());

        graphics.drawLine(
                new TerminalPosition(drawableArea.getColumns() - 1, 1),
                new TerminalPosition(drawableArea.getColumns() - 1, drawableArea.getRows() - 2),
                verticalLine);
        graphics.drawLine(
                new TerminalPosition(1, drawableArea.getRows() - 1),
                new TerminalPosition(drawableArea.getColumns() - 2, drawableArea.getRows() - 1),
                horizontalLine);

        graphics.setCharacter(drawableArea.getColumns() - 1, 0, topRightCorner);
        graphics.setCharacter(drawableArea.getColumns() - 1, drawableArea.getRows() - 1, bottomRightCorner);

        if(!title.isEmpty()) {
            graphics.putString(2, 0, TerminalTextUtils.fitString(title, drawableArea.getColumns() - 3));
        }

        return graphics.newTextGraphics(new TerminalPosition(1, 1), graphics.getSize().withRelativeColumns(-2).withRelativeRows(-2));
    }

    @Override
    public TerminalSize getDecoratedSize(Window window, TerminalSize contentAreaSize) {
        return contentAreaSize
                .withRelativeColumns(2)
                .withRelativeRows(2)
                .max(new TerminalSize(TerminalTextUtils.getColumnWidth(window.getTitle()) + 4, 1));  //Make sure the title fits!
    }

    private static final TerminalPosition OFFSET = new TerminalPosition(1, 1);

    @Override
    public TerminalPosition getOffset(Window window) {
        return OFFSET;
    }
}
