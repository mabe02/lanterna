/*
 * This file is part of lanterna.
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
 */
package com.googlecode.lanterna.gui2.menu;

import java.util.List;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.LocalizedString;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;

/**
 * Dialog containing a multiple item action list box.
 *
 * @author Martin
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MenuListDialog extends DialogWindow {

	private final Runnable closeRunnable = new Runnable() {
		@Override
		public void run() {
			close();
		}
	};

	MenuListDialog(List<Runnable> items) {
		super("");

		if (items.isEmpty()) {
			throw new IllegalStateException("MenuListDialog needs at least one item");
		}

		MenuItemListBox listBox = new MenuItemListBox(closeRunnable);
		for (final Runnable item : items) {
			listBox.addItem(item);
		}

		Panel mainPanel = new Panel();
		mainPanel.setLayoutManager(new GridLayout(1).setLeftMarginSize(1).setRightMarginSize(1));
		listBox.setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.CENTER, true, false))
				.addTo(mainPanel);

		Panel buttonPanel = new Panel();
		buttonPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(1));
		buttonPanel.addComponent(new Button(LocalizedString.Close.toString(), closeRunnable).setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));
		buttonPanel.setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, false, false))
				.addTo(mainPanel);
		setComponent(mainPanel);
	}

}
