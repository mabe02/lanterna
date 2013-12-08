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
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.terminal;

/**
 * A 2-d position in 'terminal space'. Please note that the coordinates are 0-indexed, meaning 0x0 is the top left
 * corner of the terminal.
 *
 * @author Martin
 */
public class TerminalPosition {

    private final int row;
    private final int column;

    public TerminalPosition(int column, int row) {
        if (column < 0) {
            throw new IllegalArgumentException("TerminalPosition.columns cannot be less than 0!");
        }
        if (row < 0) {
            throw new IllegalArgumentException("TerminalPosition.rows cannot be less than 0!");
        }
        this.row = row;
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
    
    public TerminalPosition withRow(int row) {
        return new TerminalPosition(this.column, row);
    }
    
    public TerminalPosition withColumn(int column) {
        return new TerminalPosition(column, this.row);
    }

    @Override
    public String toString() {
        return "[" + column + ":" + row + "]";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.row;
        hash = 23 * hash + this.column;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TerminalPosition other = (TerminalPosition) obj;
        if (this.row != other.row) {
            return false;
        }
        if (this.column != other.column) {
            return false;
        }
        return true;
    }
}
