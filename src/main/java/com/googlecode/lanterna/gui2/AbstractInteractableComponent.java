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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Default implementation of Interactable that extends from AbstractComponent. You will want to extend from this class
 * if you want to make your own interactable component.
 * @param <T> Type of renderer you which to use for this class
 * @author Martin
 */
public abstract class AbstractInteractableComponent<T extends AbstractInteractableComponent<T>> extends AbstractComponent<T> implements Interactable {

    private boolean inFocus;

    protected AbstractInteractableComponent() {
        inFocus = false;
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

    @Override
    protected abstract InteractableRenderer<T> createDefaultRenderer();

    @Override
    protected InteractableRenderer<T> getRenderer() {
        return (InteractableRenderer<T>)super.getRenderer();
    }

    @Override
    public boolean isFocused() {
        return inFocus;
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case ArrowDown:
                return Result.MOVE_FOCUS_DOWN;
            case ArrowLeft:
                return Result.MOVE_FOCUS_LEFT;
            case ArrowRight:
                return Result.MOVE_FOCUS_RIGHT;
            case ArrowUp:
                return Result.MOVE_FOCUS_UP;
            case Tab:
                return Result.MOVE_FOCUS_NEXT;
            case ReverseTab:
                return Result.MOVE_FOCUS_PREVIOUS;
            default:
        }
        return Result.UNHANDLED;
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return getRenderer().getCursorLocation(self());
    }
}
