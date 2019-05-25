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
package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * The main interface to implement when you need to customize the way table cells are drawn
 *
 * @param <V> Type of data in the table cells
 * @author Martin
 */
public interface TableCellRenderer<V> {
    /**
     * Called by the table when it wants to know how big a particular table cell should be
     * @param table Table containing the cell
     * @param cell Data stored in the cell
     * @param columnIndex Column index of the cell
     * @param rowIndex Row index of the cell
     * @return Size this renderer would like the cell to have
     */
    TerminalSize getPreferredSize(Table<V> table, V cell, int columnIndex, int rowIndex);

    /**
     * Called by the table when it's time to draw a cell, you can see how much size is available by checking the size of
     * the {@code textGUIGraphics}. The top-left position of the graphics object is the top-left position of this cell.
     * @param table Table containing the cell
     * @param cell Data stored in the cell
     * @param columnIndex Column index of the cell
     * @param rowIndex Row index of the cell
     * @param textGUIGraphics Graphics object to draw with
     */
    void drawCell(Table<V> table, V cell, int columnIndex, int rowIndex, TextGUIGraphics textGUIGraphics);
}
