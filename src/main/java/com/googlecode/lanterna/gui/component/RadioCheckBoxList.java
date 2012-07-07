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

import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class RadioCheckBoxList extends AbstractListBox {
    private int checkedIndex;

    public RadioCheckBoxList() {
        this(null);
    }

    public RadioCheckBoxList(TerminalSize preferredSize) {
        super(preferredSize);
        this.checkedIndex = -1;
    }    

    @Override
    public void clearItems() {
        checkedIndex = -1;
        super.clearItems();
    }

    @Override
    public void addItem(Object item) {
        super.addItem(item);
    }
    
    public Boolean isChecked(Object object) {
        if(indexOf(object) == -1)
            return null;
        
        return checkedIndex == indexOf(object);
    }
    
    public Boolean isChecked(int index) {
        if(index < 0 || index >= getNrOfItems())
            return null;
        
        return checkedIndex == index;
    }

    public void setCheckedItemIndex(int index) {
        if(index < -1 || index >= getNrOfItems())
            return;
        
        checkedIndex = index;
        invalidate();
    }

    public int getCheckedItemIndex() {
        return checkedIndex;
    }

    @Override
    protected Interactable.Result unhandledKeyboardEvent(Key key) {
        if(getSelectedIndex() == -1)
            return Interactable.Result.DO_NOTHING;
        
        if(key.getKind() == Key.Kind.Enter || key.getCharacter() == ' ') {
            checkedIndex = getSelectedIndex();
        }
        return Interactable.Result.DO_NOTHING;
    }

    @Override
    protected int getHotSpotPositionOnLine(int selectedIndex) {
        return 1;
    }
    
    @Override
    protected String createItemString(int index) {
        String check = " ";
        if(checkedIndex == index)
            check = "o";
        
        String text = getItemAt(index).toString();
        return "<" + check + "> " + text;
    }

    @Override
    protected Theme.Definition getListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.TextBoxFocused);
    }

    @Override
    protected Theme.Definition getSelectedListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.TextBoxFocused);
    }
}
