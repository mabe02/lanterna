package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.KeyStroke;

/**
 * This interface can be used to programmatically intercept input from the user and decide if the input should be passed
 * on to the interactable. It's also possible to fire custom actions for certain keystrokes.
 */
public interface InputFilter {
    /**
     * Called when the component is about to receive input from the user and decides if the input should be passed on to
     * the component or not
     * @param interactable Interactable that the input is directed to
     * @param keyStroke User input
     * @return {@code true} if the input should be passed on to the interactable, {@code false} otherwise
     */
    boolean onInput(Interactable interactable, KeyStroke keyStroke);
}
