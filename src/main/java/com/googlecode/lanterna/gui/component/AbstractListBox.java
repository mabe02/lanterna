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
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.List;

/**
 * Common base class for list-type components (check box list, action list, etc)
 * @author Martin
 */
public abstract class AbstractListBox extends AbstractInteractableComponent {
    private final List<Object> items;
    private TerminalSize preferredSizeOverride;
    private int selectedIndex;
    private int scrollTopIndex;

    public AbstractListBox() {
        this(null);
    }
    
    public AbstractListBox(TerminalSize preferredSize) {
        this.items = new ArrayList<Object>();
        this.preferredSizeOverride = preferredSize;
        this.selectedIndex = -1;
        this.scrollTopIndex = 0;
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        if(preferredSizeOverride != null)
            return preferredSizeOverride;
        
        int maxWidth = 5;   //Set it to something...
        for(int i = 0; i < items.size(); i++) {
            String itemString = createItemString(i);
            if(itemString.length() > maxWidth)
                maxWidth = itemString.length();
        }
        return new TerminalSize(maxWidth + 1, items.size());
    }

    protected void addItem(Object item) {
        if(item == null)
             return;

        items.add(item);
        if(selectedIndex == -1)
            selectedIndex = 0;
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
    
    public int getSize() {
        return items.size();
    }

    public Object getItemAt(int index) {
        return items.get(index);
    }

    public int getNrOfItems() {
        return items.size();
    }

    public void setSelectedItem(int index) {
        selectedIndex = index;
        invalidate();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Object getSelectedItem()
    {
        if(selectedIndex == -1)
            return null;
        else
            return items.get(selectedIndex);
    }

    @Override
    public boolean isScrollable() {
        return true;
    }
    
    public void repaint(TextGraphics graphics) {
        if(selectedIndex != -1) {
            if(selectedIndex < scrollTopIndex)
                scrollTopIndex = selectedIndex;
            else if(selectedIndex >= graphics.getHeight() + scrollTopIndex)
                scrollTopIndex = selectedIndex - graphics.getHeight() + 1;
        }
        
        //Do we need to recalculate the scroll position? 
        //This code would be triggered by resizing the window when the scroll
        //position is at the bottom
        if(items.size() > graphics.getHeight() &&
                items.size() - scrollTopIndex < graphics.getHeight()) {
            scrollTopIndex = items.size() - graphics.getHeight();
        }

        graphics.applyTheme(getListItemThemeDefinition(graphics.getTheme()));
        graphics.fillArea(' ');

        for(int i = scrollTopIndex; i < items.size(); i++) {
            if(i - scrollTopIndex >= graphics.getHeight())
                break;

            if(i == selectedIndex && hasFocus())
                graphics.applyTheme(getSelectedListItemThemeDefinition(graphics.getTheme()));
            else
                graphics.applyTheme(getListItemThemeDefinition(graphics.getTheme()));
            printItem(graphics, 0, 0 + i - scrollTopIndex, i);
        }

        if(items.size() > graphics.getHeight()) {
            graphics.applyTheme(Theme.Category.DIALOG_AREA);
            graphics.drawString(graphics.getWidth() - 1, 0, ACS.ARROW_UP + "");

            graphics.applyTheme(Theme.Category.DIALOG_AREA);
            for(int i = 1; i < graphics.getHeight() - 1; i++)
                graphics.drawString(graphics.getWidth() - 1, i, ACS.BLOCK_MIDDLE + "");

            graphics.applyTheme(Theme.Category.DIALOG_AREA);
            graphics.drawString(graphics.getWidth() - 1, graphics.getHeight() - 1, ACS.ARROW_DOWN + "");
            
            //Finally print the 'tick'
            int scrollableSize = items.size() - graphics.getHeight();
            double position = (double)scrollTopIndex / ((double)scrollableSize);
            int tickPosition = (int)(((double)graphics.getHeight() - 3.0) * position);

            graphics.applyTheme(Theme.Category.SHADOW);
            graphics.drawString(graphics.getWidth() - 1, 1 + tickPosition, " ");
        }
        if(selectedIndex == -1 || items.isEmpty())
            setHotspot(new TerminalPosition(0, 0));
        else
            setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(getHotSpotPositionOnLine(selectedIndex), selectedIndex - scrollTopIndex)));
    }

    @Override
    protected void afterEnteredFocus(FocusChangeDirection direction) {
        if(items.isEmpty())
            return;
        
        if(direction == FocusChangeDirection.DOWN)
            selectedIndex = 0;
        else if(direction == FocusChangeDirection.UP)
            selectedIndex = items.size() - 1;
    }

    protected Theme.Definition getSelectedListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.LIST_ITEM_SELECTED);
    }

    protected Theme.Definition getListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Category.LIST_ITEM);
    }

    public Result keyboardInteraction(Key key) {
        try {
            switch(key.getKind()) {
                case Tab:
                case ArrowRight:
                    return Result.NEXT_INTERACTABLE_RIGHT;

                case ReverseTab:
                case ArrowLeft:
                    return Result.PREVIOUS_INTERACTABLE_LEFT;

                case ArrowDown:
                    if(items.isEmpty() || selectedIndex == items.size() - 1)
                        return Result.NEXT_INTERACTABLE_DOWN;

                    selectedIndex++;
                    break;

                case ArrowUp:
                    if(items.isEmpty() || selectedIndex == 0)
                        return Result.PREVIOUS_INTERACTABLE_UP;

                    selectedIndex--;
                    if(selectedIndex - scrollTopIndex < 0)
                        scrollTopIndex--;
                    break;
                    
                default:
                    return unhandledKeyboardEvent(key);
            }
            return Result.EVENT_HANDLED;
        }
        finally {
            invalidate();
        }
    }

    /**
     * Draws an item in the ListBox at specific coordinates. If you override this method,
     * please note that the x and y positions have been pre-calculated for you and you should use
     * the values supplied instead of trying to figure out the position on your own based on the
     * index of the item.
     * @param graphics TextGraphics object to use when drawing the item
     * @param x X-coordinate on the terminal of the item, pre-adjusted for scrolling
     * @param y Y-coordinate on the terminal of the item, pre-adjusted for scrolling
     * @param index Index of the item that is to be drawn
     */
    protected void printItem(TextGraphics graphics, int x, int y, int index) {
        String asText = createItemString(index);
        if(asText.length() > graphics.getWidth())
            asText = asText.substring(0, graphics.getWidth());
        graphics.drawString(x, y, asText);
    }
    
    protected Interactable.Result unhandledKeyboardEvent(Key key) {
        return Result.EVENT_NOT_HANDLED;
    }

    protected int getHotSpotPositionOnLine(int selectedIndex) {
        return 0;
    }

    protected abstract String createItemString(int index);
}
