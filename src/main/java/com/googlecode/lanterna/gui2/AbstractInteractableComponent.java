package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;

/**
 * Created by martin on 15/09/14.
 */
public abstract class AbstractInteractableComponent extends AbstractComponent implements Interactable {

    private boolean inFocus;

    protected AbstractInteractableComponent() {
        inFocus = false;
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return TerminalPosition.TOP_LEFT_CORNER;
    }

    @Override
    public final void onEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
        inFocus = true;
        afterEnterFocus(direction, previouslyInFocus);
    }

    protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
    }

    @Override
    public final void onLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
        inFocus = false;
        afterLeaveFocus(direction, nextInFocus);
    }

    protected void afterLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
    }
}
