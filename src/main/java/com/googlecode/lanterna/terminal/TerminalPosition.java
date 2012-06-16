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
 * A 2-d position in 'terminal space'. Please note that the coordinates are 
 * 0-indexed, meaning 0x0 is the top left corner of the terminal.
 * @author Martin
 */
public class TerminalPosition {
    private int row;
    private int column;

    public TerminalPosition(TerminalPosition position)
    {
        this(position.getColumn(), position.getRow());
    }

    public TerminalPosition(int column, int row)
    {
        this.row = row;
        this.column = column;
    }

    public int getColumn()
    {
        return column;
    }

    public int getRow()
    {
        return row;
    }

    public void setColumn(int column)
    {
        this.column = column;
    }

    public void setRow(int row)
    {
        this.row = row;
    }

    public void ensurePositivePosition()
    {
        if(row < 0)
            row = 0;
        if(column < 0)
            column = 0;
    }

    @Override
    public String toString()
    {
        return "[" + column + ":" + row + "]";
    }
}
