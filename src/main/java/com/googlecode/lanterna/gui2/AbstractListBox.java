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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for several list box implementations, this will handle things like list of items and the scrollbar.
 * @param <T> Should always be itself, see {@code AbstractComponent}
 * @param <V> Type of items this list box contains
 * @author Martin
 */
public abstract class AbstractListBox<V, T extends AbstractListBox<V, T>> extends AbstractInteractableComponent<T> {
    private final List<V> items;
    private int selectedIndex;
    private ListItemRenderer<V,T> listItemRenderer;

    /**
     * This constructor sets up the component so it has no preferred size but will ask to be as big as the list is. If
     * the GUI cannot accommodate this size, scrolling and a vertical scrollbar will be used.
     */
    protected AbstractListBox() {
        this(null);
    }

    /**
     * This constructor sets up the component with a preferred size that is will always request, no matter what items
     * are in the list box. If there are more items than the size can contain, scrolling and a vertical scrollbar will
     * be used. Calling this constructor with a {@code null} value has the same effect as calling the default
     * constructor.
     *
     * @param size Preferred size that the list should be asking for instead of invoking the preferred size calculation,
     *             or if set to {@code null} will ask to be big enough to display all items.
     */
    protected AbstractListBox(TerminalSize size) {
        this.items = new ArrayList<V>();
        this.selectedIndex = -1;
        setPreferredSize(size);
        setListItemRenderer(createDefaultListItemRenderer());
    }

    @Override
    protected InteractableRenderer<T> createDefaultRenderer() {
        return new DefaultListBoxRenderer<V, T>();
    }

    /**
     * Method that constructs the {@code ListItemRenderer} that this list box should use to draw the elements of the
     * list box. This can be overridden to supply a custom renderer. Note that this is not the renderer used for the
     * entire list box but for each item, called one by one.
     * @return {@code ListItemRenderer} to use when drawing the items in the list
     */
    protected ListItemRenderer<V,T> createDefaultListItemRenderer() {
        return new ListItemRenderer<V,T>();
    }
    
    ListItemRenderer<V,T> getListItemRenderer() {
        return listItemRenderer;
    }

    /**
     * This method overrides the {@code ListItemRenderer} that is used to draw each element in the list box. Note that
     * this is not the renderer used for the entire list box but for each item, called one by one.
     * @param listItemRenderer New renderer to use when drawing the items in the list box
     * @return Itself
     */
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

                case PageUp:
                    if(getSize() != null) {
                        setSelectedIndex(getSelectedIndex() - getSize().getRows());
                    }
                    return Result.HANDLED;

                case PageDown:
                    if(getSize() != null) {
                        setSelectedIndex(getSelectedIndex() + getSize().getRows());
                    }
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
        if(items.isEmpty()) {
            return;
        }

        if(direction == FocusChangeDirection.DOWN) {
            selectedIndex = 0;
        }
        else if(direction == FocusChangeDirection.UP) {
            selectedIndex = items.size() - 1;
        }
    }

    /**
     * Adds one more item to the list box, at the end.
     * @param item Item to add to the list box
     * @return Itself
     */
    public synchronized T addItem(V item) {
        if(item == null) {
            return self();
        }

        items.add(item);
        if(selectedIndex == -1) {
            selectedIndex = 0;
        }
        invalidate();
        return self();
    }

    /**
     * Removes an item from the list box by its index. The current selection in the list box will be adjusted
     * accordingly.
     * @param index Index of the item to remove
     * @return The item that was removed
     * @throws IndexOutOfBoundsException if the index is out of bounds in regards to the list of items
     */
    public synchronized V removeItem(int index) {
        V existing = items.remove(index);
        if(index < selectedIndex) {
            selectedIndex--;
        }
        while(selectedIndex >= items.size()) {
            selectedIndex--;
        }
        invalidate();
        return existing;
    }

    /**
     * Removes all items from the list box
     * @return Itself
     */
    public synchronized T clearItems() {
        items.clear();
        selectedIndex = -1;
        invalidate();
        return self();
    }

    @Override
    public boolean isFocusable() {
        if(isEmpty()) {
            // These dialog boxes are quite weird when they are empty and receive input focus, so try to avoid that
            return false;
        }
        return super.isFocusable();
    }

    /**
     * Looks for the particular item in the list and returns the index within the list (starting from zero) of that item
     * if it is found, or -1 otherwise
     * @param item What item to search for in the list box
     * @return Index of the item in the list box or -1 if the list box does not contain the item
     */
    public synchronized int indexOf(V item) {
        return items.indexOf(item);
    }

    /**
     * Retrieves the item at the specified index in the list box
     * @param index Index of the item to fetch
     * @return The item at the specified index
     * @throws IndexOutOfBoundsException If the index is less than zero or equals/greater than the number of items in
     * the list box
     */
    public synchronized V getItemAt(int index) {
        return items.get(index);
    }

    /**
     * Checks if the list box has no items
     * @return {@code true} if the list box has no items, {@code false} otherwise
     */
    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns the number of items currently in the list box
     * @return Number of items in the list box
     */
    public synchronized int getItemCount() {
        return items.size();
    }

    /**
     * Returns a copy of the items in the list box as a {@code List}
     * @return Copy of all the items in this list box
     */
    public synchronized List<V> getItems() {
        return new ArrayList<V>(items);
    }

    /**
     * Sets which item in the list box that is currently selected. Please note that in this context, selected simply
     * means it is the item that currently has input focus. This is not to be confused with list box implementations
     * such as {@code CheckBoxList} where individual items have a certain checked/unchecked state.
     * @param index Index of the item that should be currently selected
     * @return Itself
     */
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

    /**
     * Returns the index of the currently selected item in the list box. Please note that in this context, selected
     * simply means it is the item that currently has input focus. This is not to be confused with list box
     * implementations such as {@code CheckBoxList} where individual items have a certain checked/unchecked state.
     * @return The index of the currently selected row in the list box, or -1 if there are no items
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Returns the currently selected item in the list box. Please note that in this context, selected
     * simply means it is the item that currently has input focus. This is not to be confused with list box
     * implementations such as {@code CheckBoxList} where individual items have a certain checked/unchecked state.
     * @return The currently selected item in the list box, or {@code null} if there are no items
     */
    public synchronized V getSelectedItem() {
        if (selectedIndex == -1) {
            return null;
        } else {
            return items.get(selectedIndex);
        }
    }

    /**
     * The default renderer for {@code AbstractListBox} and all its subclasses.
     * @param <V> Type of the items the list box this renderer is for
     * @param <T> Type of list box
     */
    public static class DefaultListBoxRenderer<V, T extends AbstractListBox<V, T>> implements InteractableRenderer<T> {
        private final ScrollBar verticalScrollBar;
        private int scrollTopIndex;

        /**
         * Default constructor
         */
        public DefaultListBoxRenderer() {
            this.verticalScrollBar = new ScrollBar(Direction.VERTICAL);
            this.scrollTopIndex = 0;
        }

        @Override
        public TerminalPosition getCursorLocation(T listBox) {
            if(!listBox.getThemeDefinition().isCursorVisible()) {
                return null;
            }
            int selectedIndex = listBox.getSelectedIndex();
            int columnAccordingToRenderer = listBox.getListItemRenderer().getHotSpotPositionOnLine(selectedIndex);
            if(columnAccordingToRenderer == -1) {
                return null;
            }
            return new TerminalPosition(columnAccordingToRenderer, selectedIndex - scrollTopIndex);
        }

        @Override
        public TerminalSize getPreferredSize(T listBox) {
            int maxWidth = 5;   //Set it to something...
            int index = 0;
            for (V item : listBox.getItems()) {
                String itemString = listBox.getListItemRenderer().getLabel(listBox, index++, item);
                int stringLengthInColumns = TerminalTextUtils.getColumnWidth(itemString);
                if (stringLengthInColumns > maxWidth) {
                    maxWidth = stringLengthInColumns;
                }
            }
            return new TerminalSize(maxWidth + 1, listBox.getItemCount());
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, T listBox) {
            //update the page size, used for page up and page down keys
            ThemeDefinition themeDefinition = listBox.getTheme().getDefinition(AbstractListBox.class);
            int componentHeight = graphics.getSize().getRows();
            //int componentWidth = graphics.getSize().getColumns();
            int selectedIndex = listBox.getSelectedIndex();
            List<V> items = listBox.getItems();
            ListItemRenderer<V,T> listItemRenderer = listBox.getListItemRenderer();

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

            graphics.applyThemeStyle(themeDefinition.getNormal());
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

            graphics.applyThemeStyle(themeDefinition.getNormal());
            if(items.size() > componentHeight) {
                verticalScrollBar.onAdded(listBox.getParent());
                verticalScrollBar.setViewSize(componentHeight);
                verticalScrollBar.setScrollMaximum(items.size());
                verticalScrollBar.setScrollPosition(scrollTopIndex);
                verticalScrollBar.draw(graphics.newTextGraphics(
                        new TerminalPosition(graphics.getSize().getColumns() - 1, 0),
                        new TerminalSize(1, graphics.getSize().getRows())));
                /*
                graphics.putString(componentWidth - 1, 0, Symbols.ARROW_UP + "");

                graphics.applyThemeStyle(themeDefinition.getInsensitive());
                for(int i = 1; i < componentHeight - 1; i++)
                    graphics.putString(componentWidth - 1, i, Symbols.BLOCK_MIDDLE + "");

                graphics.applyThemeStyle(themeDefinition.getNormal());
                graphics.putString(componentWidth - 1, componentHeight - 1, Symbols.ARROW_DOWN + "");

                //Finally print the 'tick'
                int scrollableSize = items.size() - componentHeight;
                double position = (double)scrollTopIndex / ((double)scrollableSize);
                int tickPosition = (int)(((double) componentHeight - 3.0) * position);
                graphics.applyThemeStyle(themeDefinition.getInsensitive());
                graphics.putString(componentWidth - 1, 1 + tickPosition, " ");
                */
            }
        }
    }

    /**
     * The default list item renderer class, this can be extended and customized it needed. The instance which is
     * assigned to the list box will be called once per item in the list when the list box is drawn.
     * @param <V> Type of the items in the list box
     * @param <T> Type of the list box class itself
     */
    public static class ListItemRenderer<V, T extends AbstractListBox<V, T>> {
        /**
         * Returns where on the line to place the text terminal cursor for a currently selected item. By default this
         * will return 0, meaning the first character of the selected line. If you extend {@code ListItemRenderer} you
         * can change this by returning a different number. Returning -1 will cause lanterna to hide the cursor.
         * @param selectedIndex Which item is currently selected
         * @return Index of the character in the string we want to place the terminal cursor on, or -1 to hide it
         */
        public int getHotSpotPositionOnLine(int selectedIndex) {
            return 0;
        }

        /**
         * Given a list box, an index of an item within that list box and what the item is, this method should return
         * what to draw for that item. The default implementation is to return whatever {@code toString()} returns when
         * called on the item.
         * @param listBox List box the item belongs to
         * @param index Index of the item
         * @param item The item itself
         * @return String to draw for this item
         */
        public String getLabel(T listBox, int index, V item) {
            return item != null ? item.toString() : "<null>";
        }

        /**
         * This is the main drawing method for a single list box item, it applies the current theme to setup the colors
         * and then calls {@code getLabel(..)} and draws the result using the supplied {@code TextGUIGraphics}. The
         * graphics object is created just for this item and is restricted so that it can only draw on the area this
         * item is occupying. The top-left corner (0x0) should be the starting point when drawing the item.
         * @param graphics Graphics object to draw with
         * @param listBox List box we are drawing an item from
         * @param index Index of the item we are drawing
         * @param item The item we are drawing
         * @param selected Will be set to {@code true} if the item is currently selected, otherwise {@code false}, but
         *                 please notice what context 'selected' refers to here (see {@code setSelectedIndex})
         * @param focused Will be set to {@code true} if the list box currently has input focus, otherwise {@code false}
         */
        public void drawItem(TextGUIGraphics graphics, T listBox, int index, V item, boolean selected, boolean focused) {
            ThemeDefinition themeDefinition = listBox.getTheme().getDefinition(AbstractListBox.class);
            if(selected && focused) {
                graphics.applyThemeStyle(themeDefinition.getSelected());
            }
            else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            String label = getLabel(listBox, index, item);
            label = TerminalTextUtils.fitString(label, graphics.getSize().getColumns());
            while(TerminalTextUtils.getColumnWidth(label) < graphics.getSize().getColumns()) {
                label += " ";
            }
            graphics.putString(0, 0, label);
        }
    }
}
