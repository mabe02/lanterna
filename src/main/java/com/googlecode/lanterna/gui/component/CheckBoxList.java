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

import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Theme.Definition;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class CheckBoxList extends AbstractListBox
{
    private final List<Boolean> itemStatus;

    public CheckBoxList() {
        this(null);
    }

    public CheckBoxList(TerminalSize preferredSize) {
        super(preferredSize);
        this.itemStatus = new ArrayList<Boolean>();
    }    

    @Override
    public void clearItems() {
        itemStatus.clear();
        super.clearItems();
    }

    @Override
    public void addItem(Object object) {
        itemStatus.add(Boolean.FALSE);
        super.addItem(object);
    }

    public Boolean isChecked(Object object) {
        if(indexOf(object) == -1)
            return null;
        
        return itemStatus.get(indexOf(object));
    }
    
    public Boolean isChecked(int index) {
        if(index < 0 || index >= itemStatus.size())
            return null;
        
        return itemStatus.get(index);
    }

    public void setChecked(Object object, boolean checked) {
        if(indexOf(object) == -1)
            return;
        
        itemStatus.set(indexOf(object), checked);
    }

    @Override
    protected Result unhandledKeyboardEvent(Key key) {
        if(getSelectedIndex() == -1)
            return Result.EVENT_NOT_HANDLED;
        
        if(key.getKind() == Key.Kind.Enter || key.getCharacter() == ' ') {
            if(itemStatus.get(getSelectedIndex()) == true)
                itemStatus.set(getSelectedIndex(), Boolean.FALSE);
            else
                itemStatus.set(getSelectedIndex(), Boolean.TRUE);
            return Result.EVENT_HANDLED;
        }
        return Result.EVENT_NOT_HANDLED;
    }

    @Override
    protected int getHotSpotPositionOnLine(int selectedIndex) {
        return 1;
    }
    
    @Override
    protected String createItemString(int index) {
        String check = " ";
        if(itemStatus.get(index))
            check = "x";
        
        String text = getItemAt(index).toString();
        return "[" + check + "] " + text;
    }

    @Override
    protected Definition getListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.TEXTBOX_FOCUSED);
    }

    @Override
    protected Definition getSelectedListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.TEXTBOX_FOCUSED);
    }
}
