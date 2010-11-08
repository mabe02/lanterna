/*
 *  Copyright (C) 2010 mabe02
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lantern.gui.layout;

import org.lantern.gui.Component;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public class VerticalLayout extends AxisLayout
{
    @Override
    protected int getMajorAxis(TerminalSize terminalSize)
    {
        return terminalSize.getRows();
    }

    @Override
    protected int getMinorAxis(TerminalSize terminalSize)
    {
        return terminalSize.getColumns();
    }

    @Override
    protected void setMajorAxis(TerminalSize terminalSize, int majorAxisValue)
    {
        terminalSize.setRows(majorAxisValue);
    }

    @Override
    protected void setMajorAxis(TerminalPosition terminalPosition, int majorAxisValue)
    {
        terminalPosition.setRow(majorAxisValue);
    }

    @Override
    protected void setMinorAxis(TerminalSize terminalSize, int minorAxisValue)
    {
        terminalSize.setColumns(minorAxisValue);
    }
}
