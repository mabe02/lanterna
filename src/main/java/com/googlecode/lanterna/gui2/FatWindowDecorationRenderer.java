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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 *
 */
public class FatWindowDecorationRenderer implements WindowDecorationRenderer {
    @Override
    public TextGUIGraphics draw(WindowBasedTextGUI textGUI, TextGUIGraphics graphics, Window window) {
        String title = window.getTitle();
        if(title == null) {
            title = "";
        }
        boolean hasTitle = !title.trim().isEmpty();
        if(hasTitle) {
            title = " " + title.trim() + " ";
        }

        ThemeDefinition themeDefinition = window.getTheme().getDefinition(FatWindowDecorationRenderer.class);
        char horizontalLine = themeDefinition.getCharacter("HORIZONTAL_LINE", Symbols.SINGLE_LINE_HORIZONTAL);
        char verticalLine = themeDefinition.getCharacter("VERTICAL_LINE", Symbols.SINGLE_LINE_VERTICAL);
        char bottomLeftCorner = themeDefinition.getCharacter("BOTTOM_LEFT_CORNER", Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        char topLeftCorner = themeDefinition.getCharacter("TOP_LEFT_CORNER", Symbols.SINGLE_LINE_TOP_LEFT_CORNER);
        char bottomRightCorner = themeDefinition.getCharacter("BOTTOM_RIGHT_CORNER", Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
        char topRightCorner = themeDefinition.getCharacter("TOP_RIGHT_CORNER", Symbols.SINGLE_LINE_TOP_RIGHT_CORNER);
        char leftJunction = themeDefinition.getCharacter("LEFT_JUNCTION", Symbols.SINGLE_LINE_T_RIGHT);
        char rightJunction = themeDefinition.getCharacter("RIGHT_JUNCTION", Symbols.SINGLE_LINE_T_LEFT);
        TerminalSize drawableArea = graphics.getSize();

        if(hasTitle) {
            graphics.applyThemeStyle(themeDefinition.getPreLight());
            graphics.drawLine(0, drawableArea.getRows() - 2, 0, 1, verticalLine);
            graphics.drawLine(1, 0, drawableArea.getColumns() - 2, 0, horizontalLine);
            graphics.drawLine(1, 2, drawableArea.getColumns() - 2, 2, horizontalLine);
            graphics.setCharacter(0, 0, topLeftCorner);
            graphics.setCharacter(0, 2, leftJunction);
            graphics.setCharacter(0, drawableArea.getRows() - 1, bottomLeftCorner);

            graphics.applyThemeStyle(themeDefinition.getNormal());
            graphics.drawLine(
                    drawableArea.getColumns() - 1, 1,
                    drawableArea.getColumns() - 1, drawableArea.getRows() - 2,
                    verticalLine);
            graphics.drawLine(
                    1, drawableArea.getRows() - 1,
                    drawableArea.getColumns() - 2, drawableArea.getRows() - 1,
                    horizontalLine);

            graphics.setCharacter(drawableArea.getColumns() - 1, 0, topRightCorner);
            graphics.setCharacter(drawableArea.getColumns() - 1, 2, rightJunction);
            graphics.setCharacter(drawableArea.getColumns() - 1, drawableArea.getRows() - 1, bottomRightCorner);

            graphics.applyThemeStyle(themeDefinition.getActive());
            graphics.drawLine(1, 1, drawableArea.getColumns() - 2, 1, ' ');
            graphics.putString(1, 1, TerminalTextUtils.fitString(title, drawableArea.getColumns() - 3));

            return graphics.newTextGraphics(OFFSET_WITH_TITLE, graphics.getSize().withRelativeColumns(-2).withRelativeRows(-4));
        }
        else {
            graphics.applyThemeStyle(themeDefinition.getPreLight());
            graphics.drawLine(0, drawableArea.getRows() - 2, 0, 1, verticalLine);
            graphics.drawLine(1, 0, drawableArea.getColumns() - 2, 0, horizontalLine);
            graphics.setCharacter(0, 0, topLeftCorner);
            graphics.setCharacter(0, drawableArea.getRows() - 1, bottomLeftCorner);

            graphics.applyThemeStyle(themeDefinition.getNormal());
            graphics.drawLine(
                    drawableArea.getColumns() - 1, 1,
                    drawableArea.getColumns() - 1, drawableArea.getRows() - 2,
                    verticalLine);
            graphics.drawLine(
                    1, drawableArea.getRows() - 1,
                    drawableArea.getColumns() - 2, drawableArea.getRows() - 1,
                    horizontalLine);

            graphics.setCharacter(drawableArea.getColumns() - 1, 0, topRightCorner);
            graphics.setCharacter(drawableArea.getColumns() - 1, drawableArea.getRows() - 1, bottomRightCorner);

            return graphics.newTextGraphics(OFFSET_WITHOUT_TITLE, graphics.getSize().withRelativeColumns(-2).withRelativeRows(-2));
        }
    }

    @Override
    public TerminalSize getDecoratedSize(Window window, TerminalSize contentAreaSize) {
        if(hasTitle(window)) {
            return contentAreaSize
                    .withRelativeColumns(2)
                    .withRelativeRows(4)
                    .max(new TerminalSize(TerminalTextUtils.getColumnWidth(window.getTitle()) + 4, 1));  //Make sure the title fits!
        }
        else {
            return contentAreaSize
                    .withRelativeColumns(2)
                    .withRelativeRows(2)
                    .max(new TerminalSize(3, 1));
        }
    }

    private static final TerminalPosition OFFSET_WITH_TITLE = new TerminalPosition(1, 3);
    private static final TerminalPosition OFFSET_WITHOUT_TITLE = new TerminalPosition(1, 1);

    @Override
    public TerminalPosition getOffset(Window window) {
        if(hasTitle(window)) {
            return OFFSET_WITH_TITLE;
        }
        else {
            return OFFSET_WITHOUT_TITLE;
        }
    }

    private boolean hasTitle(Window window) {
        return !(window.getTitle() == null || window.getTitle().trim().isEmpty());
    }
}
