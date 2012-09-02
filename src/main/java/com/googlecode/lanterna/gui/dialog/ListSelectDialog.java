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

package com.googlecode.lanterna.gui.dialog;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import java.util.ArrayList;
import java.util.List;

/**
 * This dialog helper class can be used for easily giving the user a list of
 * items to pick one from.
 * @author Martin
 */
public class ListSelectDialog
{
    private ListSelectDialog() {}
    
    /**
     * Presents the user with a dialog where a list of items is displayed and
     * the user can select one of the items.
     * 
     * @param owner GUIScreen to draw the dialog on
     * @param title Title of the dialog
     * @param description Text describing the dialog and the list
     * @param items Items to show in the list
     * @return The item the user chose
     */
    public static <T> T showDialog(final GUIScreen owner, final String title,
            final String description, final T... items)
    {
        return showDialog(owner, title, description, 0, items);
    }

    /**
     * Presents the user with a dialog where a list of items is displayed and
     * the user can select one of the items.
     * 
     * @param owner GUIScreen to draw the dialog on
     * @param title Title of the dialog
     * @param description Text describing the dialog and the list
     * @param listWidth Width of the list, in columns
     * @param items Items to show in the list
     * @return The item the user chose
     */
    public static <T> T showDialog(final GUIScreen owner, final String title,
            final String description, final int listWidth, final T... items)
    {
        final List<T> result = new ArrayList<T>();
        Action []actionItems = new Action[items.length];
        for(int i = 0; i < items.length; i++) {
            final T item = items[i];
            actionItems[i] = new Action() {
                @Override
                public void doAction()
                {
                    result.add(item);
                }

                @Override
                public String toString() {
                    return item.toString();
                }
            };
        }

        ActionListDialog.showActionListDialog(owner, title, description, listWidth, actionItems);
        if(result.isEmpty())
            return null;
        else
            return result.get(0);
    }
}
