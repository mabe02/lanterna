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
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class CheckBoxList extends AbstractInteractableComponent
{
    private final List<Object> items;
    private final List<Boolean> itemStatus;
    private final int forceWidth;
    private int selectedIndex;

    public CheckBoxList()
    {
        this(-1);
    }

    public CheckBoxList(int forceWidth)
    {
        this.forceWidth = forceWidth;
        this.items = new ArrayList<Object>();
        this.itemStatus = new ArrayList<Boolean>();
        this.selectedIndex = -1;
    }

    public void clearItems()
    {
        items.clear();
        itemStatus.clear();
        selectedIndex = -1;
    }

    public void addItem(Object object)
    {
        items.add(object);
        itemStatus.add(Boolean.FALSE);

        if(selectedIndex == -1)
            selectedIndex = 0;
    }

    public Boolean isChecked(Object object)
    {
        if(items.indexOf(object) == -1)
            return null;
        return itemStatus.get(items.indexOf(object));
    }
    
    public Boolean isChecked(int index)
    {
        if(index < 0 || index >= itemStatus.size())
            return null;
        return itemStatus.get(index);
    }

    public void setChecked(Object object, boolean checked)
    {
        if(items.indexOf(object) == -1)
            return;
        
        itemStatus.set(items.indexOf(object), checked);
    }
    
    public int getItemCount()
    {
        return items.size();
    }
    
    public Object getItemAt(int index)
    {
        return items.get(index);
    }

    public Interactable.Result keyboardInteraction(Key key)
    {
        try {
            switch(key.getKind())
            {
                case Enter:
                case Tab:
                case ArrowRight:
                    return Result.NEXT_INTERACTABLE;

                case ReverseTab:
                case ArrowLeft:
                    return Result.PREVIOUS_INTERACTABLE;

                case ArrowDown:
                    if(selectedIndex == items.size() - 1)
                        return Result.NEXT_INTERACTABLE;
                    else
                        selectedIndex++;
                    break;

                case ArrowUp:
                    if(selectedIndex == 0)
                        return Result.PREVIOUS_INTERACTABLE;
                    else
                        selectedIndex--;
                    break;

                default:
                    if(key.getCharacter() == ' ') {
                        if(itemStatus.get(selectedIndex) == true)
                            itemStatus.set(selectedIndex, Boolean.FALSE);
                        else
                            itemStatus.set(selectedIndex, Boolean.TRUE);
                    }
            }
            return Result.DO_NOTHING;
        }
        finally {
            invalidate();
        }
    }

    public void repaint(TextGraphics graphics)
    {
        graphics.applyTheme(Category.CheckBox);
        graphics.fillRectangle(' ', new TerminalPosition(0, 0), new TerminalSize(graphics.getWidth(), graphics.getHeight()));

        for(int i = 0; i < items.size(); i++)
        {
            if(selectedIndex == i)
                graphics.applyTheme(Category.CheckBoxSelected);
            else
                graphics.applyTheme(Category.CheckBox);

            
            String check = " ";
            if(itemStatus.get(i))
                check = "x";
            String text = items.get(i).toString();
            if(text.length() + 4 > graphics.getWidth())
                text = text.substring(0, graphics.getWidth() - 4);

            graphics.drawString(0, i, "[" + check + "] " + text);
        }
        if(selectedIndex == -1)
            setHotspot(new TerminalPosition(0, 0));
        else
            setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(1, selectedIndex)));
    }

    public TerminalSize getPreferredSize()
    {
        if(forceWidth != -1) {
            if(items.isEmpty())
                return new TerminalSize(forceWidth, 1);
            else
                return new TerminalSize(forceWidth, items.size());
        }
        else {
            if(items.isEmpty())
                return new TerminalSize(1, 1);
            else {
                int maxWidth = 1;
                for(Object object: items) {
                    if(maxWidth < object.toString().length() + 4)
                        maxWidth = object.toString().length() + 4;
                }
                return new TerminalSize(maxWidth, items.size());
            }
        }
    }
}
