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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is a simple combo box implementation that allows the user to select one out of multiple items through a
 * drop-down menu. If the combo box is not in read-only mode, the user can also enter free text in the combo box, much
 * like a {@code TextBox}.
 * @param <V> Type to use for the items in the combo box
 * @author Martin
 */
public class ComboBox<V> extends AbstractInteractableComponent<ComboBox<V>> {

    /**
     * Listener interface that can be used to catch user events on the combo box
     */
    public interface Listener {
        /**
         * This method is called whenever the user changes selection from one item to another in the combo box
         * @param selectedIndex Index of the item which is now selected
         * @param previousSelection Index of the item which was previously selected
         */
        void onSelectionChanged(int selectedIndex, int previousSelection);
    }

    private final List<V> items;
    private final List<Listener> listeners;

    private PopupWindow popupWindow;
    private String text;
    private int selectedIndex;

    private boolean readOnly;
    private boolean dropDownFocused;
    private int textInputPosition;

    /**
     * Creates a new {@code ComboBox} initialized with N number of items supplied through the varargs parameter. If at
     * least one item is given, the first one in the array will be initially selected
     * @param items Items to populate the new combo box with
     */
    public ComboBox(V... items) {
        this(Arrays.asList(items));
    }

    /**
     * Creates a new {@code ComboBox} initialized with N number of items supplied through the items parameter. If at
     * least one item is given, the first one in the collection will be initially selected
     * @param items Items to populate the new combo box with
     */
    public ComboBox(Collection<V> items) {
        this(items, items.isEmpty() ? -1 : 0);
    }

    /**
     * Creates a new {@code ComboBox} initialized with N number of items supplied through the items parameter. The
     * initial text in the combo box is set to a specific value passed in through the {@code initialText} parameter, it
     * can be a text which is not contained within the items and the selection state of the combo box will be
     * "no selection" (so {@code getSelectedIndex()} will return -1) until the user interacts with the combo box and
     * manually changes it
     *
     * @param initialText Text to put in the combo box initially
     * @param items Items to populate the new combo box with
     */
    public ComboBox(String initialText, Collection<V> items) {
        this(items, -1);
        this.text = initialText;
    }

    /**
     * Creates a new {@code ComboBox} initialized with N number of items supplied through the items parameter. The
     * initially selected item is specified through the {@code selectedIndex} parameter.
     * @param items Items to populate the new combo box with
     * @param selectedIndex Index of the item which should be initially selected
     */
    public ComboBox(Collection<V> items, int selectedIndex) {
        for(V item: items) {
            if(item == null) {
                throw new IllegalArgumentException("Cannot add null elements to a ComboBox");
            }
        }
        this.items = new ArrayList<V>(items);
        this.listeners = new CopyOnWriteArrayList<Listener>();
        this.popupWindow = null;
        this.selectedIndex = selectedIndex;
        this.readOnly = true;
        this.dropDownFocused = true;
        this.textInputPosition = 0;
        if(selectedIndex != -1) {
            this.text = this.items.get(selectedIndex).toString();
        }
        else {
            this.text = "";
        }
    }

    /**
     * Adds a new item to the combo box, at the end
     * @param item Item to add to the combo box
     * @return Itself
     */
    public synchronized ComboBox<V> addItem(V item) {
        if(item == null) {
            throw new IllegalArgumentException("Cannot add null elements to a ComboBox");
        }
        items.add(item);
        if(selectedIndex == -1 && items.size() == 1) {
            setSelectedIndex(0);
        }
        invalidate();
        return this;
    }

    /**
     * Adds a new item to the combo box, at a specific index
     * @param index Index to add the item at
     * @param item Item to add
     * @return Itself
     */
    public synchronized ComboBox<V> addItem(int index, V item) {
        if(item == null) {
            throw new IllegalArgumentException("Cannot add null elements to a ComboBox");
        }
        items.add(index, item);
        if(index <= selectedIndex) {
            setSelectedIndex(selectedIndex + 1);
        }
        invalidate();
        return this;
    }

    /**
     * Removes all items from the combo box
     * @return Itself
     */
    public synchronized ComboBox<V> clearItems() {
        items.clear();
        setSelectedIndex(-1);
        invalidate();
        return this;
    }

    /**
     * Removes a particular item from the combo box, if it is present, otherwise does nothing
     * @param item Item to remove from the combo box
     * @return Itself
     */
    public synchronized ComboBox<V> removeItem(V item) {
        int index = items.indexOf(item);
        if(index == -1) {
            return this;
        }
        return remoteItem(index);
    }

    /**
     * Removes an item from the combo box at a particular index
     * @param index Index of the item to remove
     * @return Itself
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public synchronized ComboBox<V> remoteItem(int index) {
        items.remove(index);
        if(index < selectedIndex) {
            setSelectedIndex(selectedIndex - 1);
        }
        else if(index == selectedIndex) {
            setSelectedIndex(-1);
        }
        invalidate();
        return this;
    }

    /**
     * Updates the combo box so the item at the specified index is swapped out with the supplied value in the
     * {@code item} parameter
     * @param index Index of the item to swap out
     * @param item Item to replace with
     * @return Itself
     */
    public synchronized ComboBox<V> setItem(int index, V item) {
        if(item == null) {
            throw new IllegalArgumentException("Cannot add null elements to a ComboBox");
        }
        items.set(index, item);
        invalidate();
        return this;
    }

    /**
     * Counts and returns the number of items in this combo box
     * @return Number of items in this combo box
     */
    public synchronized int getItemCount() {
        return items.size();
    }

    /**
     * Returns the item at the specific index
     * @param index Index of the item to return
     * @return Item at the specific index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public synchronized V getItem(int index) {
        return items.get(index);
    }

    /**
     * Returns the text currently displayed in the combo box, this will likely be the label of the selected item but for
     * writable combo boxes it's also what the user has typed in
     * @return String currently displayed in the combo box
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the combo box to either read-only or writable. In read-only mode, the user cannot type in any text in the
     * combo box but is forced to pick one of the items, displayed by the drop-down. In writable mode, the user can
     * enter any string in the combo box
     * @param readOnly If the combo box should be in read-only mode, pass in {@code true}, otherwise {@code false} for
     *                 writable mode
     * @return Itself
     */
    public synchronized ComboBox<V> setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if(readOnly) {
            dropDownFocused = true;
        }
        return this;
    }

    /**
     * Returns {@code true} if this combo box is in read-only mode
     * @return {@code true} if this combo box is in read-only mode, {@code false} otherwise
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Returns {@code true} if the users input focus is currently on the drop-down button of the combo box, so that
     * pressing enter would trigger the popup window. This is generally used by renderers only and is always true for
     * read-only combo boxes as the component won't allow you to focus on the text in that mode.
     * @return {@code true} if the input focus is on the drop-down "button" of the combo box
     */
    public boolean isDropDownFocused() {
        return dropDownFocused || isReadOnly();
    }

    /**
     * For writable combo boxes, this method returns the position where the text input cursor is right now. Meaning, if
     * the user types some character, where are those are going to be inserted in the string that is currently
     * displayed. If the text input position equals the size of the currently displayed text, new characters will be
     * appended at the end. The user can usually move the text input position by using left and right arrow keys on the
     * keyboard.
     * @return Current text input position
     */
    public int getTextInputPosition() {
        return textInputPosition;
    }

    /**
     * Programmatically selects one item in the combo box, which causes the displayed text to change to match the label
     * of the selected index
     * @param selectedIndex Index of the item to select
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public synchronized void setSelectedIndex(final int selectedIndex) {
        if(items.size() <= selectedIndex || selectedIndex < -1) {
            throw new IndexOutOfBoundsException("Illegal argument to ComboBox.setSelectedIndex: " + selectedIndex);
        }
        final int oldSelection = this.selectedIndex;
        this.selectedIndex = selectedIndex;
        if(selectedIndex == -1) {
            text = "";
        }
        else {
            text = items.get(selectedIndex).toString();
        }
        if(textInputPosition > text.length()) {
            textInputPosition = text.length();
        }
        runOnGUIThreadIfExistsOtherwiseRunDirect(new Runnable() {
            @Override
            public void run() {
                for(Listener listener: listeners) {
                    listener.onSelectionChanged(selectedIndex, oldSelection);
                }
            }
        });
        invalidate();
    }

    /**
     * Returns the index of the currently selected item
     * @return Index of the currently selected item
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Adds a new listener to the {@code ComboBox} that will be called on certain user actions
     * @param listener Listener to attach to this {@code ComboBox}
     * @return Itself
     */
    public ComboBox<V> addListener(Listener listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

    /**
     * Removes a listener from this {@code ComboBox} so that if it had been added earlier, it will no longer be
     * called on user actions
     * @param listener Listener to remove from this {@code ComboBox}
     * @return Itself
     */
    public ComboBox<V> removeListener(Listener listener) {
        listeners.remove(listener);
        return this;
    }

    @Override
    protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
        if(direction == FocusChangeDirection.RIGHT && !isReadOnly()) {
            dropDownFocused = false;
            selectedIndex = 0;
        }
    }

    @Override
    protected void afterLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
        if(popupWindow != null) {
            popupWindow.close();
            popupWindow = null;
        }
    }

    @Override
    protected InteractableRenderer<ComboBox<V>> createDefaultRenderer() {
        return new DefaultComboBoxRenderer<V>();
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        if(isReadOnly()) {
            return handleReadOnlyCBKeyStroke(keyStroke);
        }
        else {
            return handleEditableCBKeyStroke(keyStroke);
        }
    }

    private Result handleReadOnlyCBKeyStroke(KeyStroke keyStroke) {
        switch(keyStroke.getKeyType()) {
            case ArrowDown:
                if(popupWindow != null) {
                    popupWindow.listBox.handleKeyStroke(keyStroke);
                    return Result.HANDLED;
                }
                return Result.MOVE_FOCUS_DOWN;

            case ArrowUp:
                if(popupWindow != null) {
                    popupWindow.listBox.handleKeyStroke(keyStroke);
                    return Result.HANDLED;
                }
                return Result.MOVE_FOCUS_UP;

            case Enter:
                if(popupWindow != null) {
                    popupWindow.listBox.handleKeyStroke(keyStroke);
                    popupWindow.close();
                    popupWindow = null;
                }
                else {
                    popupWindow = new PopupWindow();
                    popupWindow.setPosition(toGlobal(getPosition().withRelativeRow(1)));
                    ((WindowBasedTextGUI) getTextGUI()).addWindow(popupWindow);
                }
                break;

            case Escape:
                if(popupWindow != null) {
                    popupWindow.close();
                    popupWindow = null;
                    return Result.HANDLED;
                }
                break;

            default:
        }
        return super.handleKeyStroke(keyStroke);
    }

    private Result handleEditableCBKeyStroke(KeyStroke keyStroke) {
        //First check if we are in drop-down focused mode, treat keystrokes a bit differently then
        if(isDropDownFocused()) {
            switch(keyStroke.getKeyType()) {
                case ReverseTab:
                case ArrowLeft:
                    dropDownFocused = false;
                    textInputPosition = text.length();
                    return Result.HANDLED;

                //The rest we can process in the same way as with read-only combo boxes when we are in drop-down focused mode
                default:
                    return handleReadOnlyCBKeyStroke(keyStroke);
            }
        }

        switch(keyStroke.getKeyType()) {
            case Character:
                text = text.substring(0, textInputPosition) + keyStroke.getCharacter() + text.substring(textInputPosition);
                textInputPosition++;
                return Result.HANDLED;

            case Tab:
                dropDownFocused = true;
                return Result.HANDLED;

            case Backspace:
                if(textInputPosition > 0) {
                    text = text.substring(0, textInputPosition - 1) + text.substring(textInputPosition);
                    textInputPosition--;
                }
                return Result.HANDLED;

            case Delete:
                if(textInputPosition < text.length()) {
                    text = text.substring(0, textInputPosition) + text.substring(textInputPosition + 1);
                }
                return Result.HANDLED;

            case ArrowLeft:
                if(textInputPosition > 0) {
                    textInputPosition--;
                }
                else {
                    return Result.MOVE_FOCUS_LEFT;
                }
                return Result.HANDLED;

            case ArrowRight:
                if(textInputPosition < text.length()) {
                    textInputPosition++;
                }
                else {
                    dropDownFocused = true;
                    return Result.HANDLED;
                }
                return Result.HANDLED;

            case ArrowDown:
                if(selectedIndex < items.size() - 1) {
                    setSelectedIndex(selectedIndex + 1);
                }
                return Result.HANDLED;

            case ArrowUp:
                if(selectedIndex > 0) {
                    setSelectedIndex(selectedIndex - 1);
                }
                return Result.HANDLED;

            default:
        }
        return super.handleKeyStroke(keyStroke);
    }

    private class PopupWindow extends BasicWindow {
        private final ActionListBox listBox;

        public PopupWindow() {
            setHints(Arrays.asList(
                    Hint.NO_FOCUS,
                    Hint.FIXED_POSITION));
            listBox = new ActionListBox(ComboBox.this.getSize().withRows(getItemCount()));
            for(int i = 0; i < getItemCount(); i++) {
                V item = items.get(i);
                final int index = i;
                listBox.addItem(item.toString(), new Runnable() {
                    @Override
                    public void run() {
                        setSelectedIndex(index);
                        close();
                    }
                });
            }
            listBox.setSelectedIndex(getSelectedIndex());
            setComponent(listBox);
        }
    }

    /**
     * Helper interface that doesn't add any new methods but makes coding new combo box renderers a little bit more clear
     */
    public static abstract class ComboBoxRenderer<V> implements InteractableRenderer<ComboBox<V>> {
    }

    /**
     * This class is the default renderer implementation which will be used unless overridden. The combo box is rendered
     * like a text box with an arrow point down to the right of it, which can receive focus and triggers the popup.
     * @param <V> Type of items in the combo box
     */
    public static class DefaultComboBoxRenderer<V> extends ComboBoxRenderer<V> {

        private int textVisibleLeftPosition;

        /**
         * Default constructor
         */
        public DefaultComboBoxRenderer() {
            this.textVisibleLeftPosition = 0;
        }

        @Override
        public TerminalPosition getCursorLocation(ComboBox<V> comboBox) {
            if(comboBox.isDropDownFocused()) {
                return new TerminalPosition(comboBox.getSize().getColumns() - 1, 0);
            }
            else {
                int textInputPosition = comboBox.getTextInputPosition();
                int textInputColumn = TerminalTextUtils.getColumnWidth(comboBox.getText().substring(0, textInputPosition));
                return new TerminalPosition(textInputColumn - textVisibleLeftPosition, 0);
            }
        }

        @Override
        public TerminalSize getPreferredSize(final ComboBox<V> comboBox) {
            TerminalSize size = TerminalSize.ONE.withColumns(
                    (comboBox.getItemCount() == 0 ? TerminalTextUtils.getColumnWidth(comboBox.getText()) : 0) + 2);
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized(comboBox) {
                for(int i = 0; i < comboBox.getItemCount(); i++) {
                    V item = comboBox.getItem(i);
                    size = size.max(new TerminalSize(TerminalTextUtils.getColumnWidth(item.toString()) + 2 + 1, 1));   // +1 to add a single column of space
                }
            }
            return size;
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, ComboBox<V> comboBox) {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.setBackgroundColor(TextColor.ANSI.BLUE);
            if(comboBox.isFocused()) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                graphics.enableModifiers(SGR.BOLD);
            }
            graphics.fill(' ');
            int editableArea = graphics.getSize().getColumns() - 2; //This is exclusing the 'drop-down arrow'
            int textInputPosition = comboBox.getTextInputPosition();
            int columnsToInputPosition = TerminalTextUtils.getColumnWidth(comboBox.getText().substring(0, textInputPosition));
            if(columnsToInputPosition < textVisibleLeftPosition) {
                textVisibleLeftPosition = columnsToInputPosition;
            }
            if(columnsToInputPosition - textVisibleLeftPosition >= editableArea) {
                textVisibleLeftPosition = columnsToInputPosition - editableArea + 1;
            }
            if(columnsToInputPosition - textVisibleLeftPosition + 1 == editableArea &&
                    comboBox.getText().length() > textInputPosition &&
                    TerminalTextUtils.isCharCJK(comboBox.getText().charAt(textInputPosition))) {
                textVisibleLeftPosition++;
            }

            String textToDraw = TerminalTextUtils.fitString(comboBox.getText(), textVisibleLeftPosition, editableArea);
            graphics.putString(0, 0, textToDraw);
            if(comboBox.isFocused()) {
                graphics.disableModifiers(SGR.BOLD);
            }
            graphics.setForegroundColor(TextColor.ANSI.BLACK);
            graphics.setBackgroundColor(TextColor.ANSI.WHITE);
            graphics.putString(editableArea, 0, "|" + Symbols.ARROW_DOWN);
        }
    }
}
