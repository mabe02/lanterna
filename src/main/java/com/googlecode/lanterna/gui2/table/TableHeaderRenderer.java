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
 * This interface can be implemented if you want to customize how table headers are drawn.
 * @param <V> Type of data stored in each table cell
 * @author Martin
 */
public interface TableHeaderRenderer<V> {
    /**
     * Called by the table when it wants to know how big a particular table header should be
     * @param table Table containing the header
     * @param label Label for this header
     * @param columnIndex Column index of the header
     * @return Size this renderer would like the header to have
     */
    TerminalSize getPreferredSize(Table<V> table, String label, int columnIndex);

    /**
     * Called by the table when it's time to draw a header, you can see how much size is available by checking the size
     * of the {@code textGUIGraphics}. The top-left position of the graphics object is the top-left position of this
     * header.
     * @param table Table containing the header
     * @param label Label for this header
     * @param index Column index of the header
     * @param textGUIGraphics Graphics object to header with
     */
    void drawHeader(Table<V> table, String label, int index, TextGUIGraphics textGUIGraphics);
}
