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
package com.googlecode.lanterna;

/**
 * Terminal dimensions in 2-d space, measured in number of rows and columns. This class is immutable and cannot change
 * its internal state after creation.
 *
 * @author Martin
 */
public class TerminalSize {

    private final int columns;
    private final int rows;

    /**
     * Creates a new terminal size representation with a given width (columns) and height (rows)
     * @param columns Width, in number of columns
     * @param rows Height, in number of columns
     */
    public TerminalSize(int columns, int rows) {
        if (columns < 0) {
            throw new IllegalArgumentException("TerminalSize.columns cannot be less than 0!");
        }
        if (rows < 0) {
            throw new IllegalArgumentException("TerminalSize.rows cannot be less than 0!");
        }
        this.columns = columns;
        this.rows = rows;
    }

    /**
     * @return Returns the width of this size representation, in number of columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Creates a new size based on this size, but with a different width
     * @param columns Width of the new size, in columns
     * @return New size based on this one, but with a new width
     */
    public TerminalSize withColumns(int columns) {
        if(this.columns == columns) {
            return this;
        }
        return new TerminalSize(columns, this.rows);
    }


    /**
     * @return Returns the height of this size representation, in number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Creates a new size based on this size, but with a different height
     * @param rows Height of the new size, in rows
     * @return New size based on this one, but with a new height
     */
    public TerminalSize withRows(int rows) {
        if(this.rows == rows) {
            return this;
        }
        return new TerminalSize(this.columns, rows);
    }

    /**
     * Creates a new TerminalSize object representing a size with the same number of rows, but with a column size offset by a
     * supplied value. Calling this method with delta 0 will return this, calling it with a positive delta will return
     * a terminal size <i>delta</i> number of columns wider and for negative numbers shorter.
     * @param delta Column offset
     * @return New terminal size based off this one but with an applied transformation
     */
    public TerminalSize withRelativeColumns(int delta) {
        if(delta == 0) {
            return this;
        }
        return withColumns(columns + delta);
    }

    /**
     * Creates a new TerminalSize object representing a size with the same number of columns, but with a row size offset by a
     * supplied value. Calling this method with delta 0 will return this, calling it with a positive delta will return
     * a terminal size <i>delta</i> number of rows longer and for negative numbers shorter.
     * @param delta Row offset
     * @return New terminal size based off this one but with an applied transformation
     */
    public TerminalSize withRelativeRows(int delta) {
        if(delta == 0) {
            return this;
        }
        return withRows(rows + delta);
    }

    @Override
    public String toString() {
        return "{" + columns + "x" + rows + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TerminalSize)) {
            return false;
        }

        TerminalSize other = (TerminalSize) obj;
        return columns == other.columns
                && rows == other.rows;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.columns;
        hash = 53 * hash + this.rows;
        return hash;
    }
}
