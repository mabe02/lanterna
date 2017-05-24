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
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.googlecode.lanterna.gui2.menu;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import java.util.Arrays;

/**
 * Represents a menu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Menu extends Button {

    /** for generating the menu. */
    protected MenuListDialogBuilder builder;

    /** the context. */
    protected WindowBasedTextGUI context;

    /**
     * Initializes the menu.
     *
     * @param label the name of the menu
     */
    public Menu(String label, final WindowBasedTextGUI context) {
        super(label);
        this.context = context;
        this.builder = new MenuListDialogBuilder();
        this.builder.setTitle("");
        addListener(newListener());
    }

    /**
     * Returns the button listener, which pops up the action list dialog
     * displaying the menu.
     *
     * @return the listener
     */
    protected Listener newListener() {
        return new Listener() {
            @Override
            public void onTriggered(Button button) {
                MenuListDialog dialog = builder.build();
                dialog.setHints(Arrays.asList(Hint.FIXED_POSITION));
                dialog.setPosition(
                        new TerminalPosition(
                                Menu.this.getPosition().getColumn() + 2,
                                Menu.this.getPosition().getRow() + 3));
                dialog.showDialog(context);
            }
        };
    }

    /**
     * Adds the menu item (name and action).
     *
     * @param label  Label of the menu item
     * @param action Action to perform if the user selects this item
     */
    public void addMenuItem(String label, Runnable action) {
        builder.addAction(label, action);
    }

    /**
     * Adds the menu item.
     *
     * @param item the menu item
     */
    public void addMenuItem(MenuItem item) {
        builder.addAction(item.getTitle(), item.getRunnable(context));
    }
}
