package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

/**
 * ActionListBox component gives you a multiple-choice selector that runs custom code then on of the items is selected
 * with either enter key or space key.
 * @author Martin
 */
public class ActionListBox extends AbstractComponent {

    @Override
    protected TerminalSize getPreferredSizeWithoutBorder() {
        return null;
    }

    @Override
    public ActionListBox withBorder(Border border) {
        super.withBorder(border);
        return this;
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {

    }
}
