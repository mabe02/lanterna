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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui;

import java.util.ArrayList;
import java.util.List;
import org.lantern.LanternException;
import org.lantern.gui.theme.Theme.Category;
import org.lantern.input.Key;
import org.lantern.terminal.Terminal;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public class ActionListBox extends AbstractInteractableComponent
{
    private final List itemList;
    private final int forceWidth;
    private int selectedIndex;

    public ActionListBox()
    {
        this(-1);
    }

    public ActionListBox(int forceWidth)
    {
        this.itemList = new ArrayList();
        this.forceWidth = forceWidth;
        this.selectedIndex = -1;
    }

    public void addItem(final Item item)
    {
        itemList.add(item);
        if(selectedIndex == -1)
            selectedIndex = 0;
    }

    public void addAction(final Action action)
    {
        addItem(new Item() {
            public String getTitle() {
                return action.toString();
            }

            public void doAction() throws LanternException {
                action.doAction();
            }
        });
    }

    public void clearItems()
    {
        itemList.clear();
        selectedIndex = -1;
    }

    public int getSelectedItemIndex()
    {
        return selectedIndex;
    }

    public Item getItem(int index)
    {
        return (Item)itemList.get(index);
    }

    public int getNrOfItems()
    {
        return itemList.size();
    }

    public void setSelectedIndex(int index)
    {
        if(index < -1)
            index = -1;

        if(index == -1 && getNrOfItems() > 0)
            selectedIndex = 0;
        else if(index != -1 && index >= getNrOfItems())
            selectedIndex = getNrOfItems() - 1;
        else
            selectedIndex = index;
        invalidate();
    }

    public void repaint(TextGraphics graphics)
    {
        for(int i = 0; i < itemList.size(); i++) {
            if(selectedIndex == i && hasFocus())
                graphics.applyThemeItem(Category.ItemSelected);
            else
                graphics.applyThemeItem(Category.Item);

            String title = ((Item)itemList.get(i)).getTitle();
            if(title.length() > graphics.getWidth() && graphics.getWidth() > 3)
                title = title.substring(0, graphics.getWidth() - 3) + "...";

            graphics.drawString(0, i, title, new Terminal.Style[]{});
        }
        if(selectedIndex == -1)
            setHotspot(new TerminalPosition(0, 0));
        else
            setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(0, selectedIndex)));
    }

    public TerminalSize getPreferredSize()
    {
        if(itemList.isEmpty())
            return new TerminalSize(1,1);
        
        if(forceWidth != -1)
            return new TerminalSize(forceWidth, itemList.size());

        int maxLength = 0;
        for(int i = 0; i < itemList.size(); i++) {
            Item item = (Item)itemList.get(i);
            if(item.getTitle().length() > maxLength)
                maxLength = item.getTitle().length();
        }
        return new TerminalSize(maxLength, itemList.size());
    }
    
    protected void afterEnteredFocus(FocusChangeDirection direction)
    {
        if(direction == FocusChangeDirection.DOWN_OR_RIGHT)
            selectedIndex = 0;
        else if(direction == FocusChangeDirection.UP_OR_LEFT)
            selectedIndex = itemList.size() - 1;
    }

    public void keyboardInteraction(Key key, InteractableResult result) throws LanternException
    {
        switch(key.getKind().getIndex()) {
            case Key.Kind.Tab_ID:
            case Key.Kind.ArrowRight_ID:
                result.type = Result.NEXT_INTERACTABLE;
                break;

            case Key.Kind.ReverseTab_ID:
            case Key.Kind.ArrowLeft_ID:
                result.type = Result.PREVIOUS_INTERACTABLE;
                break;

            case Key.Kind.ArrowDown_ID:
                if(selectedIndex == itemList.size() - 1)
                    result.type = Result.NEXT_INTERACTABLE;
                else
                    selectedIndex++;
                break;

            case Key.Kind.ArrowUp_ID:
                if(selectedIndex == 0)
                    result.type = Result.PREVIOUS_INTERACTABLE;
                else
                    selectedIndex--;
                break;

            case Key.Kind.Enter_ID:
                if(selectedIndex != -1)
                    ((Item)itemList.get(selectedIndex)).doAction();
                break;

            case Key.Kind.PageDown_ID:
                selectedIndex = itemList.size() - 1;
                break;

            case Key.Kind.PageUp_ID:
                selectedIndex = 0;
                break;
        }
        invalidate();
    }

    public static interface Item
    {
        public String getTitle();
        public void doAction() throws LanternException;
    }
}
