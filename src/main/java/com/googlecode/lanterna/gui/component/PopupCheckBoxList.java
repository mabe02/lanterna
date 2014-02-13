package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

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
			return ACS.ARROW_DOWN + this.getItemAt(index).toString();
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
	public Result keyboardInteraction(Key key) {
		if (poppedUp) {
			Result parentRet = super.keyboardInteraction(key);
			if (key.equalsString("<cr>") || key.equalsString("<space>")) {
				poppedUp = false;
				invalidate();
				valueChanged();
				return parentRet;
			}
		} 
		if (key.equalsString("<cr>")) {
			poppedUp = true;
			invalidate();
			valueChanged();
		} else if (key.equalsString("<Tab>")) {
			return Result.NEXT_INTERACTABLE_DOWN;
		} else if (key.equalsString("<S-Tab>")) {
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
