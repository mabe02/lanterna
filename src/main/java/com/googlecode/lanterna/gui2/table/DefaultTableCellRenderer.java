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
package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Default implementation of {@code TableCellRenderer}
 * @param <V> Type of data stored in each table cell
 * @author Martin
 */
public class DefaultTableCellRenderer<V> implements TableCellRenderer<V> {
    @Override
    public TerminalSize getPreferredSize(Table<V> table, V cell, int columnIndex, int rowIndex) {
        String[] lines = getContent(cell);
        int maxWidth = 0;
        for(String line: lines) {
            int length = TerminalTextUtils.getColumnWidth(line);
            if(maxWidth < length) {
                maxWidth = length;
            }
        }
        return new TerminalSize(maxWidth, lines.length);
    }

    @Override
    public void drawCell(Table<V> table, V cell, int columnIndex, int rowIndex, TextGUIGraphics textGUIGraphics) {
        ThemeDefinition themeDefinition = table.getThemeDefinition();
        if((table.getSelectedColumn() == columnIndex && table.getSelectedRow() == rowIndex) ||
                (table.getSelectedRow() == rowIndex && !table.isCellSelection())) {
            if(table.isFocused()) {
                textGUIGraphics.applyThemeStyle(themeDefinition.getActive());
            }
            else {
                textGUIGraphics.applyThemeStyle(themeDefinition.getSelected());
            }
            textGUIGraphics.fill(' ');  //Make sure to fill the whole cell first
        }
        else {
            textGUIGraphics.applyThemeStyle(themeDefinition.getNormal());
        }
        String[] lines = getContent(cell);
        int rowCount = 0;
        for(String line: lines) {
            textGUIGraphics.putString(0, rowCount++, line);
        }
    }

    private String[] getContent(V cell) {
        String[] lines;
        if(cell == null) {
            lines = new String[] { "" };
        }
        else {
            lines = cell.toString().split("\r?\n");
        }
        return lines;
    }
}
