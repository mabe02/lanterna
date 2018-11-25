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
 * Copyright (C) 2010-2018 Martin Berglund
 */
package com.googlecode.lanterna.gui2.table;

/**
 * Describing how table cells are separated when drawn
 */
public enum TableCellBorderStyle {
    /**
     * There is no separation between table cells, they are drawn immediately next to each other
     */
    None(0),
    /**
     * There is a single space of separation between the cells, drawn as a single line
     */
    SingleLine(1),
    /**
     * There is a single space of separation between the cells, drawn as a double line
     */
    DoubleLine(1),
    /**
     * There is a single space of separation between the cells, kept empty
     */
    EmptySpace(1),
    ;

    private final int size;

    TableCellBorderStyle(int size) {
        this.size = size;
    }

    /**
     * Returns the number of rows (for vertical borders) or columns (for horizontal borders) this table cell border will
     * take up when used.
     * @return Size of the border, in rows or columns depending on the context
     */
    int getSize() {
        return size;
    }
}
