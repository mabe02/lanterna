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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.graphics.ThemeStyle;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is a list box implementation where each item has its own checked state that can be toggled on and off
 * @author Martin
 */
public class CheckBoxList<V> extends AbstractListBox<V, CheckBoxList<V>> {
    /**
     * Listener interface that can be attached to the {@code CheckBoxList} in order to be notified on user actions
     */
    public interface Listener {
        /**
         * Called by the {@code CheckBoxList} when the user changes the toggle state of one item
         * @param itemIndex Index of the item that was toggled
         * @param checked If the state of the item is now checked, this will be {@code true}, otherwise {@code false}
         */
        void onStatusChanged(int itemIndex, boolean checked);
    }

    private final List<Listener> listeners;
    private final List<Boolean> itemStatus;

    /**
     * Creates a new {@code CheckBoxList} that is initially empty and has no hardcoded preferred size, so it will
     * attempt to be as big as necessary to draw all items.
     */
    public CheckBoxList() {
        this(null);
    }

    /**
     * Creates a new {@code CheckBoxList} that is initially empty and has a pre-defined size that it will request. If
     * there are more items that can fit in this size, the list box will use scrollbars.
     * @param preferredSize Size the list box should request, no matter how many items it contains
     */
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
    public synchronized CheckBoxList<V> clearItems() {
        itemStatus.clear();
        return super.clearItems();
    }

    @Override
    public CheckBoxList<V> addItem(V object) {
        return addItem(object, false);
    }

    @Override
    public synchronized V removeItem(int index) {
        V item = super.removeItem(index);
        itemStatus.remove(index);
        return item;
    }

    /**
     * Adds an item to the checkbox list with an explicit checked status
     * @param object Object to add to the list
     * @param checkedState If <code>true</code>, the new item will be initially checked
     * @return Itself
     */
    public synchronized CheckBoxList<V> addItem(V object, boolean checkedState) {
        itemStatus.add(checkedState);
        return super.addItem(object);
    }

    /**
     * Checks if a particular item is part of the check box list and returns a boolean value depending on the toggle
     * state of the item.
     * @param object Object to check the status of
     * @return If the item wasn't found in the list box, {@code null} is returned, otherwise {@code true} or
     * {@code false} depending on checked state of the item
     */
    public synchronized Boolean isChecked(V object) {
        if(indexOf(object) == -1)
            return null;

        return itemStatus.get(indexOf(object));
    }

    /**
     * Checks if a particular item is part of the check box list and returns a boolean value depending on the toggle
     * state of the item.
     * @param index Index of the item to check the status of
     * @return If the index was not valid in the list box, {@code null} is returned, otherwise {@code true} or
     * {@code false} depending on checked state of the item at that index
     */
    public synchronized Boolean isChecked(int index) {
        if(index < 0 || index >= itemStatus.size())
            return null;

        return itemStatus.get(index);
    }

    /**
     * Programmatically sets the checked state of an item in the list box
     * @param object Object to set the checked state of
     * @param checked If {@code true}, then the item is set to checked, otherwise not
     * @return Itself
     */
    public synchronized CheckBoxList<V> setChecked(V object, boolean checked) {
        int index = indexOf(object);
        if(index != -1) {
            setChecked(index, checked);
        }
        return self();
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

    /**
     * Returns all the items in the list box that have checked state, as a list
     * @return List of all items in the list box that has checked state on
     */
    public synchronized List<V> getCheckedItems() {
        List<V> result = new ArrayList<V>();
        for(int i = 0; i < itemStatus.size(); i++) {
            if(itemStatus.get(i)) {
                result.add(getItemAt(i));
            }
        }
        return result;
    }

    /**
     * Adds a new listener to the {@code CheckBoxList} that will be called on certain user actions
     * @param listener Listener to attach to this {@code CheckBoxList}
     * @return Itself
     */
    public synchronized CheckBoxList<V> addListener(Listener listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

    /**
     * Removes a listener from this {@code CheckBoxList} so that if it had been added earlier, it will no longer be
     * called on user actions
     * @param listener Listener to remove from this {@code CheckBoxList}
     * @return Itself
     */
    public CheckBoxList<V> removeListener(Listener listener) {
        listeners.remove(listener);
        return this;
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        if(keyStroke.getKeyType() == KeyType.Enter ||
                (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {
            if(itemStatus.get(getSelectedIndex()))
                setChecked(getSelectedIndex(), Boolean.FALSE);
            else
                setChecked(getSelectedIndex(), Boolean.TRUE);
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }

    /**
     * Default renderer for this component which is used unless overridden. The checked state is drawn on the left side
     * of the item label using a "[ ]" block filled with an X if the item has checked state on
     * @param <V> Type of items in the {@link CheckBoxList}
     */
    public static class CheckBoxListItemRenderer<V> extends ListItemRenderer<V,CheckBoxList<V>> {
        @Override
        public int getHotSpotPositionOnLine(int selectedIndex) {
            return 1;
        }

        @Override
        public String getLabel(CheckBoxList<V> listBox, int index, V item) {
            String check = " ";
            List<Boolean> itemStatus = listBox.itemStatus;
            if(itemStatus.get(index))
                check = "x";

            String text = item.toString();
            return "[" + check + "] " + text;
        }

        @Override
        public void drawItem(TextGUIGraphics graphics, CheckBoxList<V> listBox, int index, V item, boolean selected, boolean focused) {
            ThemeDefinition themeDefinition = listBox.getTheme().getDefinition(CheckBoxList.class);
            ThemeStyle itemStyle;
            if(selected && !focused) {
                itemStyle = themeDefinition.getSelected();
            }
            else if(selected) {
                itemStyle = themeDefinition.getActive();
            }
            else if(focused) {
                itemStyle = themeDefinition.getInsensitive();
            }
            else {
                itemStyle = themeDefinition.getNormal();
            }

            if(themeDefinition.getBooleanProperty("CLEAR_WITH_NORMAL", false)) {
                graphics.applyThemeStyle(themeDefinition.getNormal());
                graphics.fill(' ');
                graphics.applyThemeStyle(itemStyle);
            }
            else {
                graphics.applyThemeStyle(itemStyle);
                graphics.fill(' ');
            }

            String brackets = themeDefinition.getCharacter("LEFT_BRACKET", '[') +
                    " " +
                    themeDefinition.getCharacter("RIGHT_BRACKET", ']');
            if(themeDefinition.getBooleanProperty("FIXED_BRACKET_COLOR", false)) {
                graphics.applyThemeStyle(themeDefinition.getPreLight());
                graphics.putString(0, 0, brackets);
                graphics.applyThemeStyle(itemStyle);
            }
            else {
                graphics.putString(0, 0, brackets);
            }

            String text = (item != null ? item : "<null>").toString();
            graphics.putString(4, 0, text);

            boolean itemChecked = listBox.isChecked(index);
            char marker = themeDefinition.getCharacter("MARKER", 'x');
            if(themeDefinition.getBooleanProperty("MARKER_WITH_NORMAL", false)) {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            if(selected && focused && themeDefinition.getBooleanProperty("HOTSPOT_PRELIGHT", false)) {
                graphics.applyThemeStyle(themeDefinition.getPreLight());
            }
            graphics.setCharacter(1, 0, (itemChecked ? marker : ' '));
        }
    }
}
