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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.gui.dialog;

import java.util.ArrayList;
import java.util.List;
import com.googlecode.lanterna.LanternException;
import com.googlecode.lanterna.gui.ActionListBox;
import com.googlecode.lanterna.gui.GUIScreen;

/**
 *
 * @author mabe02
 */
public class ListSelectDialog
{
    public static Object showDialog(final GUIScreen owner, final String title,
            final String description, final Object... items) throws LanternException
    {
        return showDialog(owner, title, description, -1, items);
    }

    public static Object showDialog(final GUIScreen owner, final String title,
            final String description, final int listWidth, final Object... items) throws LanternException
    {
        final List<Object> result = new ArrayList<Object>();
        ActionListBox.Item []actionItems = new ActionListBox.Item[items.length];
        for(int i = 0; i < items.length; i++) {
            final Object item = items[i];
            actionItems[i] = new ActionListBox.Item() {
                public String getTitle()
                {
                    return item.toString();
                }

                public void doAction()
                {
                    result.add(item);
                }
            };
        }

        ActionListDialog.showActionListDialog(owner, title, description, listWidth, actionItems);
        if(result.size() == 0)
            return null;
        else
            return result.get(0);
    }
}
