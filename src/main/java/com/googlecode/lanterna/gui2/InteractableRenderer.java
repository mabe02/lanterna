package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;

/**
 * @author Martin
 */
public interface InteractableRenderer<T extends Interactable> extends ComponentRenderer<T> {
    TerminalPosition getCursorLocation(T component);
}
