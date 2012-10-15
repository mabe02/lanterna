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
 * The list box will display a number of items, of which one and only one can be marked as selected.
 * The user can select an item in the list box by pressing the return key or space bar key. If you
 * select one item when another item is already selected, the previously selected item will be
 * deselected and the highlighted item will be the selected one instead.
 * @author Martin
 */
public class RadioCheckBoxList extends AbstractListBox {
    private int checkedIndex;

    /**
     * Creates a new RadioCheckBoxList with no items
     */
    public RadioCheckBoxList() {
        this(null);
    }

    /**
     * Creates a new RadioCheckBoxList with a specified size override
     * @param preferredSize Size of the RadioCheckBoxList or {@code null} to use the default 
     * calculation algorithm
     */
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
    
    /**
     * This method will see if an object is the currently selected item in this RadioCheckBoxList
     * @param object Object to test if it's the selected one
     * @return {@code true} if the supplied object is what's currently selected in the list box,
     * {@code false} otherwise. Returns null if the supplied object is not an item in the list box.
     */    
    public Boolean isChecked(Object object) {
        if(object == null)
            return null;
        
        if(indexOf(object) == -1)
            return null;
        
        return checkedIndex == indexOf(object);
    }
    
    /**
     * This method will see if an item, adressed by index, is the currently selected item in this 
     * RadioCheckBoxList
     * @param index Index of the item to check if it's currently selected
     * @return {@code true} if the currently selected object is at the supplied index,
     * {@code false} otherwise. Returns null if the index is out of range.
     */
    public Boolean isChecked(int index) {
        if(index < 0 || index >= getNrOfItems())
            return null;
        
        return checkedIndex == index;
    }

    /**
     * Sets the currently selected item by index
     * @param index Index of the item to be selected
     */
    public void setCheckedItemIndex(int index) {
        if(index < -1 || index >= getNrOfItems())
            return;
        
        checkedIndex = index;
        invalidate();
    }

    /**
     * @return The index of the item which is currently selected, or -1 if there is no selection
     */
    public int getCheckedItemIndex() {
        return checkedIndex;
    }
    
    /**
     * @return The object currently selected, or null if there is no selection
     */
    public Object getCheckedItem() {
        if(checkedIndex == -1 || checkedIndex >= getNrOfItems())
            return null;
        
        return getItemAt(checkedIndex);
    }

    @Override
    protected Interactable.Result unhandledKeyboardEvent(Key key) {
        if(getSelectedIndex() == -1)
            return Interactable.Result.EVENT_NOT_HANDLED;
        
        if(key.getKind() == Key.Kind.Enter || key.getCharacter() == ' ') {
            checkedIndex = getSelectedIndex();
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
        if(checkedIndex == index)
            check = "o";
        
        String text = getItemAt(index).toString();
        return "<" + check + "> " + text;
    }

    @Override
    protected Theme.Definition getListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.TEXTBOX_FOCUSED);
    }

    @Override
    protected Theme.Definition getSelectedListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.TEXTBOX_FOCUSED);
    }
}
