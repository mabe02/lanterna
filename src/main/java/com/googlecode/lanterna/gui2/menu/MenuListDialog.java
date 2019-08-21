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
 * Copyright (C) 2010-2017 Martin Berglund
 * Copyright (C) 2017 Bruno Eberhard
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */
package com.googlecode.lanterna.gui2.menu;

import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;

import java.util.List;

/**
 * Dialog containing a multiple item action list box.
 *
 * @author Martin
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
class MenuListDialog extends DialogWindow {

	private final Runnable closeRunnable = new Runnable() {
		@Override
		public void run() {
			close();
		}
	};

	MenuListDialog(List<Runnable> items) {
		super("");
		setCloseWindowWithEscape(true);

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
		buttonPanel.setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, false, false))
				.addTo(mainPanel);
		setComponent(mainPanel);
	}
}
