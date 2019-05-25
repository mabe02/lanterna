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
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Formalized interactable renderer for tables
 * @author Martin
 */
public interface TableRenderer<V> extends InteractableRenderer<Table<V>> {
    @Override
    void drawComponent(TextGUIGraphics graphics, Table<V> component);

    @Override
    TerminalSize getPreferredSize(Table<V> component);

    /**
     * Returns the number of rows visible in the table cell area on the last draw operation
     * @return The number of rows visible in the table cell area on the last draw operation
     */
    int getVisibleRowsOnLastDraw();

    /**
     * Returns the index of the first visible row with the renderers current state
     * @return Index of the first visible row of the table
     */
    int getViewTopRow();

    /**
     * Modifies which row is the first visible, this may be overwritten depending on the circumstances when drawing the
     * table.
     * @param viewTopRow First row to be displayed when drawing the table
     */
    void setViewTopRow(int viewTopRow);

    /**
     * Returns the index of the first visible column with the renderers current state
     * @return Index of the first visible column of the table
     */
    int getViewLeftColumn();

    /**
     * Modifies which column is the first visible, this may be overwritten depending on the circumstances when drawing the
     * table.
     * @param viewLeftColumn First column to be displayed when drawing the table
     */
    void setViewLeftColumn(int viewLeftColumn);
}
