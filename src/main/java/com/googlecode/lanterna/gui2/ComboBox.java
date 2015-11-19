package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by martin on 15/09/15.
 */
public class ComboBox<V> extends AbstractInteractableComponent<ComboBox<V>> {

    public interface Listener {
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

    public ComboBox(V... items) {
        this(Arrays.asList(items));
    }

    public ComboBox(Collection<V> items) {
        this(items, items.isEmpty() ? -1 : 0);
    }

    public ComboBox(String initialText, Collection<V> items) {
        this(items, -1);
        this.text = initialText;
    }

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

    public ComboBox<V> addItem(V item) {
        if(item == null) {
            throw new IllegalArgumentException("Cannot add null elements to a ComboBox");
        }
        synchronized(this) {
            items.add(item);
            if(selectedIndex == -1 && items.size() == 1) {
                setSelectedIndex(0);
            }
            invalidate();
            return this;
        }
    }

    public ComboBox<V> addItem(int index, V item) {
        if(item == null) {
            throw new IllegalArgumentException("Cannot add null elements to a ComboBox");
        }
        synchronized(this) {
            items.add(index, item);
            if(index <= selectedIndex) {
                setSelectedIndex(selectedIndex + 1);
            }
            invalidate();
            return this;
        }
    }

    public ComboBox<V> clearItems() {
        synchronized(this) {
            items.clear();
            setSelectedIndex(-1);
            invalidate();
            return this;
        }
    }

    public ComboBox<V> removeItem(V item) {
        synchronized(this) {
            int index = items.indexOf(item);
            if(index == -1) {
                return this;
            }
            return remoteItem(index);
        }
    }

    public ComboBox<V> remoteItem(int index) {
        synchronized(this) {
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
    }

    public ComboBox<V> setItem(int index, V item) {
        if(item == null) {
            throw new IllegalArgumentException("Cannot add null elements to a ComboBox");
        }
        synchronized(this) {
            items.set(index, item);
            invalidate();
            return this;
        }
    }

    public int getItemCount() {
        synchronized(this) {
            return items.size();
        }
    }

    public V getItem(int index) {
        synchronized(this) {
            return items.get(index);
        }
    }

    public String getText() {
        synchronized(this) {
            return text;
        }
    }

    public ComboBox<V> setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if(readOnly) {
            dropDownFocused = true;
        }
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isDropDownFocused() {
        return dropDownFocused || isReadOnly();
    }

    public int getTextInputPosition() {
        return textInputPosition;
    }

    public void setSelectedIndex(final int selectedIndex) {
        synchronized(this) {
            if(items.size() <= selectedIndex || selectedIndex < -1) {
                throw new IllegalArgumentException("Illegal argument to ComboBox.setSelectedIndex: " + selectedIndex);
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
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public ComboBox<V> addListener(Listener listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

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
    public Result handleKeyStroke(KeyStroke keyStroke) {
        synchronized(this) {
            if(isReadOnly()) {
                return handleReadOnlyCBKeyStroke(keyStroke);
            }
            else {
                return handleEditableCBKeyStroke(keyStroke);
            }
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

    public static abstract class ComboBoxRenderer<V> implements InteractableRenderer<ComboBox<V>> {
    }

    public static class DefaultComboBoxRenderer<V> extends ComboBoxRenderer<V> {

        private int textVisibleLeftPosition;

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
                int textInputColumn = CJKUtils.getColumnWidth(comboBox.getText().substring(0, textInputPosition));
                return new TerminalPosition(textInputColumn - textVisibleLeftPosition, 0);
            }
        }

        @Override
        public TerminalSize getPreferredSize(final ComboBox<V> comboBox) {
            TerminalSize size = TerminalSize.ONE.withColumns(
                    (comboBox.getItemCount() == 0 ? CJKUtils.getColumnWidth(comboBox.getText()) : 0) + 2);
            synchronized(comboBox) {
                for(int i = 0; i < comboBox.getItemCount(); i++) {
                    V item = comboBox.getItem(i);
                    size = size.max(new TerminalSize(CJKUtils.getColumnWidth(item.toString()) + 2 + 1, 1));   // +1 to add a single column of space
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
            int columnsToInputPosition = CJKUtils.getColumnWidth(comboBox.getText().substring(0, textInputPosition));
            if(columnsToInputPosition < textVisibleLeftPosition) {
                textVisibleLeftPosition = columnsToInputPosition;
            }
            if(columnsToInputPosition - textVisibleLeftPosition >= editableArea) {
                textVisibleLeftPosition = columnsToInputPosition - editableArea + 1;
            }
            if(columnsToInputPosition - textVisibleLeftPosition + 1 == editableArea &&
                    comboBox.getText().length() > textInputPosition &&
                    CJKUtils.isCharCJK(comboBox.getText().charAt(textInputPosition))) {
                textVisibleLeftPosition++;
            }

            String textToDraw = CJKUtils.fitString(comboBox.getText(), textVisibleLeftPosition, editableArea);
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
