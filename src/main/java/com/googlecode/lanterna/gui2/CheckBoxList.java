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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by martin on 29/09/14.
 */
public class CheckBoxList<V> extends AbstractListBox<V, CheckBoxList<V>> {

    public interface Listener {
        void onStatusChanged(int itemIndex, boolean checked);
    }

    private final List<Listener> listeners;
    private final List<Boolean> itemStatus;

    public CheckBoxList() {
        this(null);
    }

    public CheckBoxList(TerminalSize preferredSize) {
        super(preferredSize);
        this.listeners = new CopyOnWriteArrayList<Listener>();
        this.itemStatus = new ArrayList<Boolean>();
    }

    @Override
    protected ListItemRenderer<V,CheckBoxList<V>> createDefaultListItemRenderer() {
        return new CheckBoxListItemRenderer<V>();
    }

    @Override
    public CheckBoxList<V> clearItems() {
        synchronized(this) {
            itemStatus.clear();
            return super.clearItems();
        }
    }

    @Override
    public CheckBoxList<V> addItem(V object) {
        return addItem(object, false);
    }

    /**
     * Adds an item to the checkbox list with an explicit checked status
     * @param object Object to add to the list
     * @param checkedState If <code>true</code>, the new item will be initially checked
     * @return Itself
     */
    public CheckBoxList<V> addItem(V object, boolean checkedState) {
        synchronized(this) {
            itemStatus.add(checkedState);
            return super.addItem(object);
        }
    }

    public Boolean isChecked(V object) {
        synchronized(this) {
            if(indexOf(object) == -1)
                return null;

            return itemStatus.get(indexOf(object));
        }
    }

    public Boolean isChecked(int index) {
        synchronized(this) {
            if(index < 0 || index >= itemStatus.size())
                return null;

            return itemStatus.get(index);
        }
    }

    public CheckBoxList<V> setChecked(V object, boolean checked) {
        synchronized(this) {
            int index = indexOf(object);
            if(index != -1) {
                setChecked(index, checked);
            }
            return self();
        }
    }

    private void setChecked(final int index, final boolean checked) {
        itemStatus.set(index, checked);
        runOnGUIThreadIfExistsOtherwiseRunDirect(new Runnable() {
            @Override
            public void run() {
                for(Listener listener: listeners) {
                    listener.onStatusChanged(index, checked);
                }
            }
        });
    }

    public List<V> getCheckedItems() {
        synchronized(this) {
            List<V> result = new ArrayList<V>();
            for(int i = 0; i < itemStatus.size(); i++) {
                if(itemStatus.get(i)) {
                    result.add(getItemAt(i));
                }
            }
            return result;
        }
    }

    public CheckBoxList<V> addListener(Listener listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

    public CheckBoxList<V> removeListener(Listener listener) {
        listeners.remove(listener);
        return this;
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        synchronized(this) {
            if(keyStroke.getKeyType() == KeyType.Enter ||
                    (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {
                if(itemStatus.get(getSelectedIndex()))
                    setChecked(getSelectedIndex(), Boolean.FALSE);
                else
                    setChecked(getSelectedIndex(), Boolean.TRUE);
                return Result.HANDLED;
            }
        }
        return super.handleKeyStroke(keyStroke);
    }

    public static class CheckBoxListItemRenderer<V> extends ListItemRenderer<V,CheckBoxList<V>> {
        @Override
        protected int getHotSpotPositionOnLine(int selectedIndex) {
            return 1;
        }

        @Override
        protected String getLabel(CheckBoxList<V> listBox, int index, V item) {
            String check = " ";
            List<Boolean> itemStatus = listBox.itemStatus;
            if(itemStatus.get(index))
                check = "x";

            String text = item.toString();
            return "[" + check + "] " + text;
        }
    }
}
