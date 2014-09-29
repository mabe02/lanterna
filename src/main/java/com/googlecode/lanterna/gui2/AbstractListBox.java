package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.ACS;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for several list box implementations, this will handle the list of items and the scrollbar for you
 * @author Martin
 */
public abstract class AbstractListBox extends AbstractInteractableComponent {
    private final List<Object> items;
    private int selectedIndex;
    private int scrollTopIndex;
    private int pageSize;

    protected AbstractListBox() {
        this(null);
    }

    protected AbstractListBox(TerminalSize size) {
        this.items = new ArrayList<Object>();
        this.selectedIndex = -1;
        this.scrollTopIndex = 0;
        this.pageSize = 1;
        setPreferredSize(size);
    }

    @Override
    public TerminalSize calculatePreferredSize() {
        int maxWidth = 5;   //Set it to something...
        int index = 0;
        for (Object item : items) {
            String itemString = getLabel(index++, item);
            if (itemString.length() > maxWidth) {
                maxWidth = itemString.length();
            }
        }
        return new TerminalSize(maxWidth + 1, items.size());
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {
        //update the page size, used for page up and page down keys
        int componentHeight = graphics.getSize().getRows();
        int componentWidth = graphics.getSize().getColumns();
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

        graphics.fill(' ');

        for(int i = scrollTopIndex; i < items.size(); i++) {
            if(i - scrollTopIndex >= componentHeight)
                break;

//            if(i == selectedIndex && isFocused())
//                graphics.applyTheme(getSelectedListItemThemeDefinition(graphics.getTheme()));
//            else
//                graphics.applyTheme(getListItemThemeDefinition(graphics.getTheme()));
            graphics.putString(0, i - scrollTopIndex, getLabel(i, items.get(i)));
        }

        if(items.size() > componentHeight) {
            //graphics.applyTheme(Theme.Category.DIALOG_AREA);
            graphics.putString(componentWidth - 1, 0, ACS.ARROW_UP + "");

            //graphics.applyTheme(Theme.Category.DIALOG_AREA);
            for(int i = 1; i < componentHeight - 1; i++)
                graphics.putString(componentWidth - 1, i, ACS.BLOCK_MIDDLE + "");

            //graphics.applyTheme(Theme.Category.DIALOG_AREA);
            graphics.putString(componentWidth - 1, componentHeight - 1, ACS.ARROW_DOWN + "");

            //Finally print the 'tick'
            int scrollableSize = items.size() - componentHeight;
            double position = (double)scrollTopIndex / ((double)scrollableSize);
            int tickPosition = (int)(((double) componentHeight - 3.0) * position);

            //graphics.applyTheme(Theme.Category.SHADOW);
            graphics.putString(componentWidth - 1, 1 + tickPosition, " ");
        }
//        if(selectedIndex == -1 || items.isEmpty())
//            setHotspot(new TerminalPosition(0, 0));
//        else
//            setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(getHotSpotPositionOnLine(selectedIndex), selectedIndex - scrollTopIndex)));
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
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
                    break;

                case ArrowUp:
                    if(items.isEmpty() || selectedIndex == 0) {
                        return Result.MOVE_FOCUS_UP;
                    }
                    selectedIndex--;
                    if(selectedIndex - scrollTopIndex < 0)
                        scrollTopIndex--;
                    break;

                case Home:
                    selectedIndex = 0;
                    break;

                case End:
                    selectedIndex = items.size() - 1;
                    break;

                case PageUp:
                    selectedIndex -= pageSize;
                    if(selectedIndex < 0) {
                        selectedIndex = 0;
                    }
                    break;

                case PageDown:
                    selectedIndex += pageSize;
                    if(selectedIndex > items.size() - 1) {
                        selectedIndex = items.size() - 1;
                    }
                    break;
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

    public void setSelectedItem(int index) {
        selectedIndex = index;
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

    protected int getHotSpotPositionOnLine(int selectedIndex) {
        return 0;
    }

    protected abstract String getLabel(int index, Object item);
}
