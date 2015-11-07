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

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for several list box implementations, this will handle the list of items and the scrollbar for you
 * @param <T>
 * @author Martin
 */
public abstract class AbstractListBox<V, T extends AbstractListBox<V, T>> extends AbstractInteractableComponent<T> {
    private final List<V> items;
    private int selectedIndex;
    private ListItemRenderer<V,T> listItemRenderer;

    protected AbstractListBox() {
        this(null);
    }

    protected AbstractListBox(TerminalSize size) {
        this.items = new ArrayList<V>();
        this.selectedIndex = -1;
        setPreferredSize(size);
        setListItemRenderer(createDefaultListItemRenderer());
    }

    @Override
    protected ListBoxRenderer<V, T> createDefaultRenderer() {
        return new DefaultListBoxRenderer<V, T>();
    }
    
    protected ListItemRenderer<V,T> createDefaultListItemRenderer() {
        return new ListItemRenderer<V,T>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListBoxRenderer<V, T> getRenderer() {
        return (ListBoxRenderer<V, T>)super.getRenderer();
    }
    
    public ListItemRenderer<V,T> getListItemRenderer() {
        return listItemRenderer;
    }

    public synchronized T setListItemRenderer(ListItemRenderer<V,T> listItemRenderer) {
        if(listItemRenderer == null) {
            listItemRenderer = createDefaultListItemRenderer();
            if(listItemRenderer == null) {
                throw new IllegalStateException("createDefaultListItemRenderer returned null");
            }
        }
        this.listItemRenderer = listItemRenderer;
        return self();
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        try {
            Result rendererResult = getRenderer().handleKeyStroke(this, keyStroke);
            if(rendererResult != Result.UNHANDLED) {
                return rendererResult;
            }
            switch(keyStroke.getKeyType()) {
                case Tab:
                    return Result.MOVE_FOCUS_NEXT;

                case ReverseTab:
                    return Result.MOVE_FOCUS_PREVIOUS;

                case ArrowRight:
                    return Result.MOVE_FOCUS_RIGHT;

                case ArrowLeft:
                    return Result.MOVE_FOCUS_LEFT;

                case ArrowDown:
                    if(items.isEmpty() || selectedIndex == items.size() - 1) {
                        return Result.MOVE_FOCUS_DOWN;
                    }
                    selectedIndex++;
                    return Result.HANDLED;

                case ArrowUp:
                    if(items.isEmpty() || selectedIndex == 0) {
                        return Result.MOVE_FOCUS_UP;
                    }
                    selectedIndex--;
                    return Result.HANDLED;

                case Home:
                    selectedIndex = 0;
                    return Result.HANDLED;

                case End:
                    selectedIndex = items.size() - 1;
                    return Result.HANDLED;

                default:
            }
            return Result.UNHANDLED;
        }
        finally {
            invalidate();
        }
    }

    @Override
    protected synchronized void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
        if(items.isEmpty())
            return;

        if(direction == FocusChangeDirection.DOWN)
            selectedIndex = 0;
        else if(direction == FocusChangeDirection.UP)
            selectedIndex = items.size() - 1;
    }

    public synchronized T addItem(V item) {
        if (item == null) {
            return self();
        }

        items.add(item);
        if (selectedIndex == -1) {
            selectedIndex = 0;
        }
        invalidate();
        return self();
    }

    public synchronized T clearItems() {
        items.clear();
        selectedIndex = -1;
        invalidate();
        return self();
    }

    public synchronized int indexOf(V item) {
        return items.indexOf(item);
    }

    public synchronized V getItemAt(int index) {
        return items.get(index);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public synchronized int getItemCount() {
        return items.size();
    }

    List<V> getItems() {
        return items;
    }

    public synchronized Iterable<V> getItemsIterable() {
        return new ArrayList<V>(items);
    }

    public synchronized T setSelectedIndex(int index) {
        selectedIndex = index;
        if(selectedIndex < 0) {
            selectedIndex = 0;
        }
        if(selectedIndex > items.size() - 1) {
            selectedIndex = items.size() - 1;
        }
        invalidate();
        return self();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public synchronized V getSelectedItem() {
        if (selectedIndex == -1) {
            return null;
        } else {
            return items.get(selectedIndex);
        }
    }

    public static abstract class ListBoxRenderer<V, T extends AbstractListBox<V, T>> implements InteractableRenderer<T> {
        public abstract Result handleKeyStroke(AbstractListBox<V, T> listBox, KeyStroke keyStroke);
    }

    public static class DefaultListBoxRenderer<V, T extends AbstractListBox<V, T>> extends ListBoxRenderer<V, T> {
        private int scrollTopIndex;
        private int pageSize;

        public DefaultListBoxRenderer() {
            this.scrollTopIndex = 0;
            this.pageSize = 1;
        }

        @Override
        public Result handleKeyStroke(AbstractListBox<V, T> listBox, KeyStroke keyStroke) {
            switch (keyStroke.getKeyType()) {
                case PageUp:
                    listBox.setSelectedIndex(listBox.getSelectedIndex() - pageSize);
                    return Result.HANDLED;

                case PageDown:
                    listBox.setSelectedIndex(listBox.getSelectedIndex() + pageSize);
                    return Result.HANDLED;

                default:
            }
            return Result.UNHANDLED;
        }

        @Override
        public TerminalPosition getCursorLocation(T listBox) {
            int selectedIndex = listBox.getSelectedIndex();
            int columnAccordingToRenderer = listBox.getListItemRenderer().getHotSpotPositionOnLine(selectedIndex);
            return new TerminalPosition(columnAccordingToRenderer, selectedIndex - scrollTopIndex);
        }

        @Override
        public TerminalSize getPreferredSize(T listBox) {
            int maxWidth = 5;   //Set it to something...
            int index = 0;
            for (V item : listBox.getItems()) {
                String itemString = listBox.getListItemRenderer().getLabel(listBox, index++, item);
                int stringLengthInColumns = CJKUtils.getColumnWidth(itemString);
                if (stringLengthInColumns > maxWidth) {
                    maxWidth = stringLengthInColumns;
                }
            }
            return new TerminalSize(maxWidth + 1, listBox.getItemCount());
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, T listBox) {
            //update the page size, used for page up and page down keys
            int componentHeight = graphics.getSize().getRows();
            int componentWidth = graphics.getSize().getColumns();
            int selectedIndex = listBox.getSelectedIndex();
            List<V> items = listBox.getItems();
            ListItemRenderer<V,T> listItemRenderer = listBox.getListItemRenderer();
            pageSize = componentHeight;

            if(selectedIndex != -1) {
                if(selectedIndex < scrollTopIndex)
                    scrollTopIndex = selectedIndex;
                else if(selectedIndex >= componentHeight + scrollTopIndex)
                    scrollTopIndex = selectedIndex - componentHeight + 1;
            }

            //Do we need to recalculate the scroll position?
            //This code would be triggered by resizing the window when the scroll
            //position is at the bottom
            if(items.size() > componentHeight &&
                    items.size() - scrollTopIndex < componentHeight) {
                scrollTopIndex = items.size() - componentHeight;
            }

            graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getNormal());
            graphics.fill(' ');

            TerminalSize itemSize = graphics.getSize().withRows(1);
            for(int i = scrollTopIndex; i < items.size(); i++) {
                if(i - scrollTopIndex >= componentHeight) {
                    break;
                }
                listItemRenderer.drawItem(
                        graphics.newTextGraphics(new TerminalPosition(0, i - scrollTopIndex), itemSize),
                        listBox,
                        i,
                        items.get(i),
                        selectedIndex == i,
                        listBox.isFocused());
            }

            graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getNormal());
            if(items.size() > componentHeight) {
                graphics.putString(componentWidth - 1, 0, Symbols.ARROW_UP + "");

                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getInsensitive());
                for(int i = 1; i < componentHeight - 1; i++)
                    graphics.putString(componentWidth - 1, i, Symbols.BLOCK_MIDDLE + "");

                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getNormal());
                graphics.putString(componentWidth - 1, componentHeight - 1, Symbols.ARROW_DOWN + "");

                //Finally print the 'tick'
                int scrollableSize = items.size() - componentHeight;
                double position = (double)scrollTopIndex / ((double)scrollableSize);
                int tickPosition = (int)(((double) componentHeight - 3.0) * position);
                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getInsensitive());
                graphics.putString(componentWidth - 1, 1 + tickPosition, " ");
            }
        }
    }

    public static class ListItemRenderer<V, T extends AbstractListBox<V, T>> {
        protected int getHotSpotPositionOnLine(int selectedIndex) {
            return 0;
        }

        protected String getLabel(T listBox, int index, V item) {
            return item != null ? item.toString() : "<null>";
        }

        protected void drawItem(TextGUIGraphics graphics, T listBox, int index, V item, boolean selected, boolean focused) {
            if(selected && focused) {
                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getSelected());
            }
            else {
                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getNormal());
            }
            String label = getLabel(listBox, index, item);
            label = CJKUtils.fitString(label, graphics.getSize().getColumns());
            graphics.putString(0, 0, label);
        }
    }
}
