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
 * Copyright (C) 2010-2017 Martin Berglund
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

    private static final int TITLE_POSITION_WITH_PADDING = 4;
    private static final int TITLE_POSITION_WITHOUT_PADDING = 3;

    @Override
    public TextGUIGraphics draw(WindowBasedTextGUI textGUI, TextGUIGraphics graphics, Window window) {
        String title = window.getTitle();
        if(title == null) {
            title = "";
        }

        TerminalSize drawableArea = graphics.getSize();
        ThemeDefinition themeDefinition = window.getTheme().getDefinition(DefaultWindowDecorationRenderer.class);
        char horizontalLine = themeDefinition.getCharacter("HORIZONTAL_LINE", Symbols.SINGLE_LINE_HORIZONTAL);
        char verticalLine = themeDefinition.getCharacter("VERTICAL_LINE", Symbols.SINGLE_LINE_VERTICAL);
        char bottomLeftCorner = themeDefinition.getCharacter("BOTTOM_LEFT_CORNER", Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        char topLeftCorner = themeDefinition.getCharacter("TOP_LEFT_CORNER", Symbols.SINGLE_LINE_TOP_LEFT_CORNER);
        char bottomRightCorner = themeDefinition.getCharacter("BOTTOM_RIGHT_CORNER", Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
        char topRightCorner = themeDefinition.getCharacter("TOP_RIGHT_CORNER", Symbols.SINGLE_LINE_TOP_RIGHT_CORNER);
        char titleSeparatorLeft = themeDefinition.getCharacter("TITLE_SEPARATOR_LEFT", Symbols.SINGLE_LINE_HORIZONTAL);
        char titleSeparatorRight = themeDefinition.getCharacter("TITLE_SEPARATOR_RIGHT", Symbols.SINGLE_LINE_HORIZONTAL);
        boolean useTitlePadding = themeDefinition.getBooleanProperty("TITLE_PADDING", false);
        boolean centerTitle = themeDefinition.getBooleanProperty("CENTER_TITLE", false);

        int titleHorizontalPosition = useTitlePadding ? TITLE_POSITION_WITH_PADDING : TITLE_POSITION_WITHOUT_PADDING;
        int titleMaxColumns = drawableArea.getColumns() - titleHorizontalPosition * 2;
        if(centerTitle) {
            titleHorizontalPosition = (drawableArea.getColumns() / 2) - (TerminalTextUtils.getColumnWidth(title) / 2);
            titleHorizontalPosition = Math.max(titleHorizontalPosition, useTitlePadding ? TITLE_POSITION_WITH_PADDING : TITLE_POSITION_WITHOUT_PADDING);
        }
        String actualTitle = TerminalTextUtils.fitString(title, titleMaxColumns);
        int titleActualColumns = TerminalTextUtils.getColumnWidth(actualTitle);

        graphics.applyThemeStyle(themeDefinition.getPreLight());
        graphics.drawLine(new TerminalPosition(0, drawableArea.getRows() - 2), new TerminalPosition(0, 1), verticalLine);
        graphics.drawLine(new TerminalPosition(1, 0), new TerminalPosition(drawableArea.getColumns() - 2, 0), horizontalLine);
        graphics.setCharacter(0, 0, topLeftCorner);
        graphics.setCharacter(0, drawableArea.getRows() - 1, bottomLeftCorner);

        if(!actualTitle.isEmpty() && drawableArea.getColumns() > 8) {
            int separatorOffset = 1;
            if(useTitlePadding) {
                graphics.setCharacter(titleHorizontalPosition - 1, 0, ' ');
                graphics.setCharacter(titleHorizontalPosition + titleActualColumns, 0, ' ');
                separatorOffset = 2;
            }
            graphics.setCharacter(titleHorizontalPosition - separatorOffset, 0, titleSeparatorLeft);
            graphics.setCharacter(titleHorizontalPosition + titleActualColumns + separatorOffset - 1, 0, titleSeparatorRight);
        }

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

        if(!actualTitle.isEmpty()) {
            if(textGUI.getActiveWindow() == window) {
                graphics.applyThemeStyle(themeDefinition.getActive());
            }
            else {
                graphics.applyThemeStyle(themeDefinition.getInsensitive());
            }
            graphics.putString(titleHorizontalPosition, 0, actualTitle);
        }

        return graphics.newTextGraphics(
                new TerminalPosition(1, 1),
                drawableArea
                        // Make sure we don't make the new graphic's area smaller than 0
                        .withRelativeColumns(-(Math.min(2, drawableArea.getColumns())))
                        .withRelativeRows(-(Math.min(2, drawableArea.getRows()))));
    }

    @Override
    public TerminalSize getDecoratedSize(Window window, TerminalSize contentAreaSize) {
        ThemeDefinition themeDefinition = window.getTheme().getDefinition(DefaultWindowDecorationRenderer.class);
        boolean useTitlePadding = themeDefinition.getBooleanProperty("TITLE_PADDING", false);

        int titleWidth = TerminalTextUtils.getColumnWidth(window.getTitle());
        int minPadding = TITLE_POSITION_WITHOUT_PADDING * 2;
        if(useTitlePadding) {
            minPadding = TITLE_POSITION_WITH_PADDING * 2;
        }

        return contentAreaSize
                .withRelativeColumns(2)
                .withRelativeRows(2)
                .max(new TerminalSize(titleWidth + minPadding, 1));  //Make sure the title fits!
    }

    private static final TerminalPosition OFFSET = new TerminalPosition(1, 1);

    @Override
    public TerminalPosition getOffset(Window window) {
        return OFFSET;
    }
}
