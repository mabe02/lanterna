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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.terminal;

import org.lantern.LanternException;

/**
 * Terminal dimensions in 2-d space
 * @author mabe02
 */
public class TerminalSize {
    private int columns;
    private int rows;

    public TerminalSize(int columns, int rows)
    {
        setColumns(columns);
        setRows(rows);
    }

    public TerminalSize(TerminalSize terminalSize)
    {
        this(terminalSize.getColumns(), terminalSize.getRows());
    }

    public int getColumns()
    {
        return columns;
    }

    public void setColumns(int columns)
    {
        //TODO: Vad ska vi göra här?
        if(columns < -1)
            columns = 0;

        this.columns = columns;
    }

    public int getRows()
    {
        return rows;
    }

    public void setRows(int rows)
    {
        //TODO: Vad ska vi göra här?
        if(rows < -1)
            rows = 0;
        
        this.rows = rows;
    }

    @Override
    public String toString()
    {
        return "{" + columns + "x" + rows + "}";
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof TerminalSize == false)
            return false;

        TerminalSize other = (TerminalSize)obj;
        return columns == other.columns &&
                rows == other.rows;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 53 * hash + this.columns;
        hash = 53 * hash + this.rows;
        return hash;
    }

    public static void overrideSizeQuerying(TerminalSize fixedSize)
    {
        TerminalStatus.USE_THIS_SIZE_INSTEAD_OF_QUERY_OS = fixedSize;
    }

    public static boolean canQuerySize() throws LanternException
    {
        return TerminalStatus.verifySizeQuery();
    }
}
