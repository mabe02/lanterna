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
package com.googlecode.lanterna.gui.layout;

import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * Layout which will put components along a horizontal line. The height of the container will be the
 * same as the tallest child.
 *
 * @author Martin
 */
public class HorisontalLayout extends LinearLayout {

    public HorisontalLayout() {
        super();
        setPadding(1);  //By default set the padding to 1 for Horizontal layouts
    }
    
    @Override
    protected int getMajorAxis(TerminalSize terminalSize) {
        return terminalSize.getColumns();
    }

    @Override
    protected int getMinorAxis(TerminalSize terminalSize) {
        return terminalSize.getRows();
    }

    @Override
    protected void setMajorAxis(TerminalSize terminalSize, int majorAxisValue) {
        terminalSize.setColumns(majorAxisValue);
    }

    @Override
    protected void setMajorAxis(TerminalPosition terminalPosition, int majorAxisValue) {
        terminalPosition.setColumn(majorAxisValue);
    }

    @Override
    protected void setMinorAxis(TerminalSize terminalSize, int minorAxisValue) {
        terminalSize.setRows(minorAxisValue);
    }

    @Override
    protected LayoutParameter getMajorMaximizesParameter() {
        return LinearLayout.MAXIMIZES_HORIZONTALLY;
    }

    @Override
    protected LayoutParameter getMinorMaximizesParameter() {
        return LinearLayout.MAXIMIZES_VERTICALLY;
    }

    @Override
    protected LayoutParameter getMajorGrowingParameter() {
        return LinearLayout.GROWS_HORIZONTALLY;
    }

    @Override
    protected LayoutParameter getMinorGrowingParameter() {
        return LinearLayout.GROWS_VERTICALLY;
    }

    @Override
    protected boolean maximisesOnMajorAxis(Panel panel) {
        return panel.maximisesHorisontally();
    }

    @Override
    protected boolean maximisesOnMinorAxis(Panel panel) {
        return panel.maximisesVertically();
    }
}
