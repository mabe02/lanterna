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

package com.googlecode.lanterna.gui.component;

/**
 *
 * @author Martin
 */
public class CheckBox extends CommonCheckBox
{
    private boolean selected;

    public CheckBox(final String label, final boolean initiallyChecked)
    {
        super(label);
        this.selected = initiallyChecked;
    }

    @Override
    protected char getSelectionCharacter()
    {
        return 'x';
    }

    @Override
    public boolean isSelected()
    {
        return selected;
    }

    @Override
    protected void onActivated()
    {
        selected = !selected;
    }

    @Override
    protected String surroundCharacter(char character)
    {
        return "[" + character + "]";
    }


}
