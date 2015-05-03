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
 * Copyright (C) 2010-2015
 */
package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import java.io.IOException;

@Deprecated
public class PopupCheckBoxList extends RadioCheckBoxList {

	private static final String EMPTY_SELECT = "-------";
	
	private boolean poppedUp = false;
	
	public boolean isPoppedUp() {
		return poppedUp;
	}

	@Override
	protected String createItemString(int index) {
		if (poppedUp) {
			return super.createItemString(index);
		}
		if (getCheckedItemIndex() > -1) {
			return Symbols.ARROW_DOWN + this.getItemAt(index).toString();
		} else {
			return EMPTY_SELECT;
		}
	}
	
	@Override
	public void repaint(TextGraphics graphics) {
		if (poppedUp) {
			super.repaint(graphics);
		} else {
			if (hasFocus()) graphics.applyTheme(getListItemThemeDefinition(graphics.getTheme()));
			graphics.drawString(0, 0, createItemString(getCheckedItemIndex()));
			setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(0, 0)));
		}
	}
	
	@Override
	public Result keyboardInteraction(KeyStroke key) throws IOException {
		if (poppedUp) {
			Result parentRet = super.keyboardInteraction(key);
			if (key.getKeyType() == KeyType.Enter || key.getCharacter() == ' ') {
				poppedUp = false;
				invalidate();
				valueChanged();
				return parentRet;
			}
		} 
		if (key.getKeyType() == KeyType.Enter) {
			poppedUp = true;
			invalidate();
			valueChanged();
		} else if (key.getKeyType() == KeyType.Tab) {
			return Result.NEXT_INTERACTABLE_DOWN;
		} else if (key.getKeyType() == KeyType.ReverseTab) {
			return Result.PREVIOUS_INTERACTABLE_UP;
		}
		return Result.EVENT_HANDLED;
	}

	@Override
	public TerminalSize getPreferredSize() {
		if (poppedUp) {
			return super.getPreferredSize();
		}
		// TODO size is not correct because of getCheckedItemIndex returning -1
//		System.out.println("" + getCheckedItemIndex());
		int width = getCheckedItemIndex() > -1 ? createItemString(getSelectedIndex()).length() : EMPTY_SELECT.length();
		return new TerminalSize(width+1, 1);
	}
	
}
