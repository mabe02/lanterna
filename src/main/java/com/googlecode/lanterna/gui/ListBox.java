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

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.gui.theme.Theme.Category;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class ListBox extends AbstractInteractableComponent
{
    private final List<Object> items;
    private final TerminalSize preferredSize;
    private int selectedIndex;
    private int scrollTopIndex;

    public ListBox(final int columnWidth, final int rowHeight)
    {
        this(new TerminalSize(columnWidth, rowHeight));
    }

    public ListBox(final TerminalSize preferredSize)
    {
        this.items = new ArrayList<Object>();
        this.preferredSize = new TerminalSize(preferredSize);
        this.selectedIndex = -1;
        this.scrollTopIndex = 0;
    }

    public TerminalSize getPreferredSize()
    {
        return preferredSize;
    }

    public void addItem(Object item)
    {
        if(item == null)
             return;

        items.add(item);
        if(selectedIndex == -1)
            selectedIndex = 0;
        invalidate();
    }

    public Object getItemAt(int index)
    {
        return items.get(index);
    }

    public int getNrOfItems()
    {
        return items.size();
    }

    public void setSelectedItem(int index)
    {
        selectedIndex = index;
        invalidate();
    }

    public Object getSelectedItem()
    {
        if(selectedIndex == -1)
            return null;
        else
            return items.get(selectedIndex);
    }

    public void repaint(TextGraphics graphics)
    {
        if(selectedIndex != -1) {
            if(selectedIndex < scrollTopIndex)
                scrollTopIndex = selectedIndex;
            else if(selectedIndex >= graphics.getHeight() + scrollTopIndex)
                scrollTopIndex = selectedIndex - graphics.getHeight() + 1;
        }

        graphics.applyThemeItem(Category.ListItem);
        graphics.fillArea(' ');

        for(int i = scrollTopIndex; i < items.size(); i++) {
            if(i - scrollTopIndex >= graphics.getHeight())
                break;

            if(i == selectedIndex)
                graphics.applyThemeItem(Category.ListItemSelected);
            else
                graphics.applyThemeItem(Category.ListItem);
            printItem(graphics, 0, 0 + i - scrollTopIndex, items.get(i).toString());
        }

        if(items.size() > graphics.getHeight()) {
            graphics.applyThemeItem(Category.DialogArea);
            graphics.drawString(graphics.getWidth() - 1, 0, ACS.ARROW_UP + "");

            graphics.applyThemeItem(Category.DialogArea);
            for(int i = 1; i < graphics.getHeight() - 1; i++)
                graphics.drawString(graphics.getWidth() - 1, i, ACS.BLOCK_MIDDLE + "");

            graphics.applyThemeItem(Category.DialogArea);
            graphics.drawString(graphics.getWidth() - 1, graphics.getHeight() - 1, ACS.ARROW_DOWN + "");
            
            //Finally print the 'tick'
            int scrollableSize = items.size() - graphics.getHeight();
            double position = (double)scrollTopIndex / ((double)scrollableSize - 1.0);
            int tickPosition = (int)(((double)graphics.getHeight() - 3.0) * position);

            graphics.applyThemeItem(Category.Shadow);
            graphics.drawString(graphics.getWidth() - 1, 1 + tickPosition, " ");
        }
        if(selectedIndex == -1 || items.isEmpty())
            setHotspot(new TerminalPosition(0, 0));
        else
            setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(0, selectedIndex - scrollTopIndex)));
    }

    public void keyboardInteraction(Key key, InteractableResult result) throws LanternaException
    {
        switch(key.getKind()) {
            case Tab:
            case ArrowRight:
            case Enter:
                result.type = Result.NEXT_INTERACTABLE;
                break;

            case ReverseTab:
            case ArrowLeft:
                result.type = Result.PREVIOUS_INTERACTABLE;
                break;

            case ArrowDown:
                if(items.isEmpty() || selectedIndex == items.size() - 1)
                    return;

                selectedIndex++;
                break;

            case ArrowUp:
                if(items.isEmpty() || selectedIndex == 0)
                    return;

                selectedIndex--;
                if(selectedIndex - scrollTopIndex < 0)
                    scrollTopIndex--;
                break;
        }
        invalidate();
    }

    private void printItem(TextGraphics graphics, int x, int y, String text)
    {
        if(text.length() > graphics.getWidth())
            text = text.substring(0, graphics.getWidth());
        graphics.drawString(x, y, text);
    }

}
