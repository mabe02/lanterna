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

package org.lantern.gui.dialog;

/**
 *
 * @author mabe02
 */
public class DialogResult
{
    public static final int OK_ID = 1;
    public static final int CANCEL_ID = 2;
    public static final int YES_ID = 3;
    public static final int NO_ID = 4;
    
    public static final DialogResult OK = new DialogResult(OK_ID);
    public static final DialogResult CANCEL = new DialogResult(CANCEL_ID);
    public static final DialogResult YES = new DialogResult(YES_ID);
    public static final DialogResult NO = new DialogResult(NO_ID);
    
    private final int index;

    private DialogResult(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
