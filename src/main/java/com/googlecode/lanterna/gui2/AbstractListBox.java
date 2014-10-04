package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.ACS;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for several list box implementations, this will handle the list of items and the scrollbar for you
 * @author Martin
 */
public abstract class AbstractListBox extends AbstractInteractableComponent<AbstractListBox.ListBoxRenderer> {
    private final List<Object> items;
    private int selectedIndex;
    private ListItemRenderer listItemRenderer;

    protected AbstractListBox() {
        this(null);
    }

    protected AbstractListBox(TerminalSize size) {
        this.items = new ArrayList<Object>();
        this.selectedIndex = -1;
        setPreferredSize(size);
        setListItemRenderer(createDefaultListItemRenderer());
    }

    @Override
    protected ListBoxRenderer createDefaultRenderer() {
        return new DefaultListBoxRenderer();
    }

    protected ListItemRenderer createDefaultListItemRenderer() {
        return new ListItemRenderer();
    }

    public ListItemRenderer getListItemRenderer() {
        return listItemRenderer;
    }

    public void setListItemRenderer(ListItemRenderer listItemRenderer) {
        if(listItemRenderer == null) {
            listItemRenderer = createDefaultListItemRenderer();
            if(listItemRenderer == null) {
                throw new IllegalStateException("createDefaultListItemRenderer returned null");
            }
        }
        this.listItemRenderer = listItemRenderer;
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
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
            }
            return Result.UNHANDLED;
        }
        finally {
            invalidate();
        }
    }

    @Override
    protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
        if(items.isEmpty())
            return;

        if(direction == FocusChangeDirection.DOWN)
            selectedIndex = 0;
        else if(direction == FocusChangeDirection.UP)
            selectedIndex = items.size() - 1;
    }

    protected void addItem(Object item) {
        if (item == null) {
            return;
        }

        items.add(item);
        if (selectedIndex == -1) {
            selectedIndex = 0;
        }
        invalidate();
    }

    public void clearItems() {
        items.clear();
        selectedIndex = -1;
        invalidate();
    }

    public int indexOf(Object item) {
        return items.indexOf(item);
    }

    public Object getItemAt(int index) {
        return items.get(index);
    }

    public int getItemCount() {
        return items.size();
    }

    List<Object> getItems() {
        return items;
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
        if(selectedIndex < 0) {
            selectedIndex = 0;
        }
        if(selectedIndex > items.size() - 1) {
            selectedIndex = items.size() - 1;
        }
        invalidate();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Object getSelectedItem() {
        if (selectedIndex == -1) {
            return null;
        } else {
            return items.get(selectedIndex);
        }
    }

    public static abstract class ListBoxRenderer implements InteractableRenderer<AbstractListBox> {
        public abstract Result handleKeyStroke(AbstractListBox listBox, KeyStroke keyStroke);
    }

    public static class DefaultListBoxRenderer extends ListBoxRenderer {
        private int scrollTopIndex;
        private int pageSize;

        public DefaultListBoxRenderer() {
            this.scrollTopIndex = 0;
            this.pageSize = 1;
        }

        @Override
        public Result handleKeyStroke(AbstractListBox listBox, KeyStroke keyStroke) {
            switch (keyStroke.getKeyType()) {
                case PageUp:
                    listBox.setSelectedIndex(listBox.getSelectedIndex() - pageSize);
                    return Result.HANDLED;

                case PageDown:
                    listBox.setSelectedIndex(listBox.getSelectedIndex() + pageSize);
                    return Result.HANDLED;
            }
            return Result.UNHANDLED;
        }

        @Override
        public TerminalPosition getCursorLocation(AbstractListBox listBox) {
            int selectedIndex = listBox.getSelectedIndex();
            int columnAccordingToRenderer = listBox.getListItemRenderer().getHotSpotPositionOnLine(selectedIndex);
            return new TerminalPosition(columnAccordingToRenderer, selectedIndex - scrollTopIndex);
        }

        @Override
        public TerminalSize getPreferredSize(AbstractListBox listBox) {
            int maxWidth = 5;   //Set it to something...
            int index = 0;
            for (Object item : listBox.getItems()) {
                String itemString = listBox.getListItemRenderer().getLabel(index++, item);
                if (itemString.length() > maxWidth) {
                    maxWidth = itemString.length();
                }
            }
            return new TerminalSize(maxWidth + 1, listBox.getItemCount());
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, AbstractListBox listBox) {
            //update the page size, used for page up and page down keys
            int componentHeight = graphics.getSize().getRows();
            int componentWidth = graphics.getSize().getColumns();
            int selectedIndex = listBox.getSelectedIndex();
            List<Object> items = listBox.getItems();
            ListItemRenderer listItemRenderer = listBox.getListItemRenderer();
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
                graphics.putString(componentWidth - 1, 0, ACS.ARROW_UP + "");

                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getInsensitive());
                for(int i = 1; i < componentHeight - 1; i++)
                    graphics.putString(componentWidth - 1, i, ACS.BLOCK_MIDDLE + "");

                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getNormal());
                graphics.putString(componentWidth - 1, componentHeight - 1, ACS.ARROW_DOWN + "");

                //Finally print the 'tick'
                int scrollableSize = items.size() - componentHeight;
                double position = (double)scrollTopIndex / ((double)scrollableSize);
                int tickPosition = (int)(((double) componentHeight - 3.0) * position);
                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getInsensitive());
                graphics.putString(componentWidth - 1, 1 + tickPosition, " ");
            }
        }
    }

    public static class ListItemRenderer {
        protected int getHotSpotPositionOnLine(int selectedIndex) {
            return 0;
        }

        protected String getLabel(int index, Object item) {
            return item != null ? item.toString() : "<null>";
        }

        protected void drawItem(TextGUIGraphics graphics, AbstractListBox listBox, int index, Object item, boolean selected, boolean focused) {
            if(selected && focused) {
                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getSelected());
            }
            else {
                graphics.applyThemeStyle(graphics.getThemeDefinition(AbstractListBox.class).getNormal());
            }
            graphics.putString(0, 0, getLabel(index, item));
        }
    }
}
