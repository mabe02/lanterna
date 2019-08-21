/*
 * This file is part of lanterna.
 *
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
 * Copyright (C) 2010-2017 Martin Berglund
 * Copyright (C) 2017 Bruno Eberhard
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */
package com.googlecode.lanterna.gui2.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

/**
 * Represents a menu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Bruno Eberhard
 */
public class Menu extends Button {
	private final List<Runnable> items = new ArrayList<Runnable>();
	private Menu parent;

	private final Listener listener = new Listener() {
		@Override
		public void onTriggered(Button button) {
            popup();
		}
	};

	/**
     * Initializes the menu.
     *
     * @param label the name of the menu
     */
    public Menu(String label) {
        super(label);
        addListener(listener);
    }

    /**
     * Adds the menu item. A (sub) menu is also a menu item
     *
     * @param item the menu item
     */
    public void addMenuItem(Runnable item) {
        items.add(item);
    }

    public void addMenuItem(final String label, final Runnable action) {
        addMenuItem(new Runnable() {
            @Override
            public void run() {
                action.run();
            }

            @Override
            public String toString() {
                return label;
            }
        });
    }

    public void addSubMenuItem(final Menu subMenu) {
       subMenu.parent = this;
       addMenuItem(new Runnable() {
           @Override
           public void run() {
               subMenu.popup();
           }

           @Override
           public String toString() {
               return subMenu.toString();
           }
       });
    }

    @Override
    public String toString() {
    	return getLabel();
    }

    private WindowBasedTextGUI getWindowBasedTextGUI() {
    	if (parent != null) {
    		return parent.getWindowBasedTextGUI();
    	} else {
    		return (WindowBasedTextGUI) getTextGUI();
    	}
    }

    private int calcDepth() {
    	int depth = 0;
    	Menu m = this;
    	while (m.parent != null) {
    		m = m.parent;
    		depth++;
    	}
    	return depth;
    }

    private void popup() {
		MenuListDialog dialog = new MenuListDialog(items);
		dialog.setHints(Arrays.asList(Hint.FIXED_POSITION));
		int depth = calcDepth();
		dialog.setPosition(
				new TerminalPosition(
						Menu.this.getPosition().getColumn() + depth * 2,
						Menu.this.getPosition().getRow() + depth * 2 + 1));

		dialog.showDialog(getWindowBasedTextGUI());
    }
}