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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 29/09/14.
 */
public class CheckBoxList extends AbstractListBox<CheckBoxList> {
    private final List<Boolean> itemStatus;

    public CheckBoxList() {
        this(null);
    }

    public CheckBoxList(TerminalSize preferredSize) {
        super(preferredSize);
        this.itemStatus = new ArrayList<Boolean>();
    }

    @Override
    protected ListItemRenderer createDefaultListItemRenderer() {
        return new CheckBoxListItemRenderer();
    }

    @Override
    public synchronized void clearItems() {
        itemStatus.clear();
        super.clearItems();
    }

    @Override
    public synchronized void addItem(Object object) {
        itemStatus.add(Boolean.FALSE);
        super.addItem(object);
    }

    public synchronized Boolean isChecked(Object object) {
        if(indexOf(object) == -1)
            return null;

        return itemStatus.get(indexOf(object));
    }

    public synchronized Boolean isChecked(int index) {
        if(index < 0 || index >= itemStatus.size())
            return null;

        return itemStatus.get(index);
    }

    public synchronized void setChecked(Object object, boolean checked) {
        if(indexOf(object) == -1)
            return;

        itemStatus.set(indexOf(object), checked);
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        if(keyStroke.getKeyType() == KeyType.Enter ||
                (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {
            if(itemStatus.get(getSelectedIndex()))
                itemStatus.set(getSelectedIndex(), Boolean.FALSE);
            else
                itemStatus.set(getSelectedIndex(), Boolean.TRUE);
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }

    public static class CheckBoxListItemRenderer extends ListItemRenderer {
        @Override
        protected int getHotSpotPositionOnLine(int selectedIndex) {
            return 1;
        }

        @Override
        protected String getLabel(AbstractListBox<?> listBox, int index, Object item) {
            String check = " ";
            if(((CheckBoxList)listBox).itemStatus.get(index))
                check = "x";

            String text = item.toString();
            return "[" + check + "] " + text;
        }
    }
}
