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
import org.lantern.gui.theme.Theme.Category;
import org.lantern.input.Key;
import org.lantern.terminal.Terminal;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public class CheckBoxList extends AbstractInteractableComponent
{
    private final List items;
    private final List itemStatus;
    private final int forceWidth;
    private int selectedIndex;

    public CheckBoxList()
    {
        this(-1);
    }

    public CheckBoxList(int forceWidth)
    {
        this.forceWidth = forceWidth;
        this.items = new ArrayList();
        this.itemStatus = new ArrayList();
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
        return (Boolean)itemStatus.get(items.indexOf(object));
    }
    
    public Boolean isChecked(int index)
    {
        if(index < 0 || index >= itemStatus.size())
            return null;
        return (Boolean)itemStatus.get(index);
    }

    public void setChecked(Object object, boolean checked)
    {
        if(items.indexOf(object) == -1)
            return;
        
        itemStatus.set(items.indexOf(object), new Boolean(checked));
    }
    
    public int getItemCount()
    {
        return items.size();
    }
    
    public Object getItemAt(int index)
    {
        return items.get(index);
    }

    public void keyboardInteraction(Key key, InteractableResult result)
    {
        switch(key.getKind().getIndex())
        {
            case Key.Kind.Enter_ID:
            case Key.Kind.Tab_ID:
            case Key.Kind.ArrowRight_ID:
                result.type = Result.NEXT_INTERACTABLE;
                break;

            case Key.Kind.ReverseTab_ID:
            case Key.Kind.ArrowLeft_ID:
                result.type = Result.PREVIOUS_INTERACTABLE;
                break;

            case Key.Kind.ArrowDown_ID:
                if(selectedIndex == items.size() - 1)
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

            default:
                if(key.getCharacter() == ' ') {
                    if(((Boolean)itemStatus.get(selectedIndex)).booleanValue() == true)
                        itemStatus.set(selectedIndex, Boolean.FALSE);
                    else
                        itemStatus.set(selectedIndex, Boolean.TRUE);
                }
        }
        invalidate();
    }

    public void repaint(TextGraphics graphics)
    {
        graphics.applyThemeItem(Category.CheckBox);
        graphics.fillRectangle(' ', new TerminalPosition(0, 0), new TerminalSize(graphics.getWidth(), graphics.getHeight()));

        for(int i = 0; i < items.size(); i++)
        {
            if(selectedIndex == i)
                graphics.applyThemeItem(Category.CheckBoxSelected);
            else
                graphics.applyThemeItem(Category.CheckBox);

            
            String check = " ";
            if(((Boolean)itemStatus.get(i)).booleanValue())
                check = "x";
            String text = items.get(i).toString();
            if(text.length() + 4 > graphics.getWidth())
                text = text.substring(0, graphics.getWidth() - 4);

            graphics.drawString(0, i, "[" + check + "] " + text, new Terminal.Style[]{});
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
                for(int i = 0; i < items.size(); i++) {
                    Object object = items.get(i);
                    if(maxWidth < object.toString().length() + 4)
                        maxWidth = object.toString().length() + 4;
                }
                return new TerminalSize(maxWidth, items.size());
            }
        }
    }
}
