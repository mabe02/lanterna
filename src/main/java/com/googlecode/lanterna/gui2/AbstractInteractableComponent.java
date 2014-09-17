package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;

/**
 * Created by martin on 15/09/14.
 */
public abstract class AbstractInteractableComponent extends AbstractComponent implements Interactable {
    @Override
    public TerminalPosition getCursorLocation() {
        return TerminalPosition.TOP_LEFT_CORNER;
    }
}
