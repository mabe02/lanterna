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

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class ActionListBox extends AbstractListBox {
    
    public ActionListBox() {
        this(null);
    }

    public ActionListBox(TerminalSize preferredSize) {
        super(preferredSize);
    }

    /**
     * Adds an action to the list, using toString() of the action as a label
     * @param action Action to be performed when the user presses enter key
     */
    public void addAction(final Action action) {
        addAction(action.toString(), action);
    }

    /**
     * Adds an action to the list, with a specified label
     * @param label Label to be displayed, representing the action
     * @param action Action to be performed when the user presses enter key
     */
    public void addAction(final String label, final Action action) {
        super.addItem(new Item() {
            @Override
            public String getTitle() {
                return label;
            }

            @Override
            public void doAction() {
                action.doAction();
            }
        });
    }

    @Override
    protected Result unhandledKeyboardEvent(Key key) {
        if(key.getKind() == Key.Kind.Enter) {
            ((Item)getSelectedItem()).doAction();
            return Result.EVENT_HANDLED;
        }
        return Result.EVENT_NOT_HANDLED;
    }
    
    @Override
    protected String createItemString(int index) {
        return ((Item)getItemAt(index)).getTitle();
    }

    @Override
    public TerminalPosition getHotspot() {
        return null;    //No hotspot for ActionListBox:es
    }

    @Override
    protected Theme.Definition getListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.DIALOG_AREA);
    }

    @Override
    protected Theme.Definition getSelectedListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.TEXTBOX_FOCUSED);
    }
    
    private static interface Item {
        public String getTitle();
        public void doAction();
    }
}
