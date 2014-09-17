package com.googlecode.lanterna.gui2;

/**
 * Text GUI system that is based around classes implementing Component and Interactable.
 * @author Martin
 */
public interface ComponentBasedTextGUI extends TextGUI {
    /**
     * Returns the interactable component currently in focus
     * @return Component that is currently in input focus
     */
    Interactable getFocusedInteractable();
}
