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
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Default implementation of Interactable that extends from AbstractComponent. If you want to write your own component
 * that is interactable, i.e. can receive keyboard (and mouse) input, you probably want to extend from this class as
 * it contains some common implementations of the methods from {@code Interactable} interface
 * @param <T> Should always be itself, see {@code AbstractComponent}
 * @author Martin
 */
public abstract class AbstractInteractableComponent<T extends AbstractInteractableComponent<T>> extends AbstractComponent<T> implements Interactable {

    private InputFilter inputFilter;
    private boolean inFocus;
    private boolean enabled;

    /**
     * Default constructor
     */
    protected AbstractInteractableComponent() {
        inputFilter = null;
        inFocus = false;
        enabled = true;
    }

    @Override
    public T takeFocus() {
        if(!isEnabled()) {
            return self();
        }
        BasePane basePane = getBasePane();
        if(basePane != null) {
            basePane.setFocusedInteractable(this);
        }
        return self();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is final in {@code AbstractInteractableComponent}, please override {@code afterEnterFocus} instead
     */
    @Override
    public final void onEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
        inFocus = true;
        afterEnterFocus(direction, previouslyInFocus);
    }

    /**
     * Called by {@code AbstractInteractableComponent} automatically after this component has received input focus. You
     * can override this method if you need to trigger some action based on this.
     * @param direction How focus was transferred, keep in mind this is from the previous component's point of view so
     *                  if this parameter has value DOWN, focus came in from above
     * @param previouslyInFocus Which interactable component had focus previously
     */
    @SuppressWarnings("EmptyMethod")
    protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
        //By default no action
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is final in {@code AbstractInteractableComponent}, please override {@code afterLeaveFocus} instead
     */
    @Override
    public final void onLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
        inFocus = false;
        afterLeaveFocus(direction, nextInFocus);
    }

    /**
     * Called by {@code AbstractInteractableComponent} automatically after this component has lost input focus. You
     * can override this method if you need to trigger some action based on this.
     * @param direction How focus was transferred, keep in mind this is from the this component's point of view so
     *                  if this parameter has value DOWN, focus is moving down to a component below
     * @param nextInFocus Which interactable component is going to receive focus
     */
    @SuppressWarnings("EmptyMethod")
    protected void afterLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
        //By default no action
    }

    @Override
    protected abstract InteractableRenderer<T> createDefaultRenderer();

    @Override
    public InteractableRenderer<T> getRenderer() {
        return (InteractableRenderer<T>)super.getRenderer();
    }

    @Override
    public boolean isFocused() {
        return inFocus;
    }

    @Override
    public synchronized T setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(!enabled && isFocused()) {
            BasePane basePane = getBasePane();
            if(basePane != null) {
                basePane.setFocusedInteractable(null);
            }
        }
        return self();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public final synchronized Result handleInput(KeyStroke keyStroke) {
        if(inputFilter == null || inputFilter.onInput(this, keyStroke)) {
            return handleKeyStroke(keyStroke);
        }
        else {
            return Result.UNHANDLED;
        }
    }

    /**
     * This method can be overridden to handle various user input (mostly from the keyboard) when this component is in
     * focus. The input method from the interface, {@code handleInput(..)} is final in
     * {@code AbstractInteractableComponent} to ensure the input filter is properly handled. If the filter decides that
     * this event should be processed, it will call this method.
     * @param keyStroke What input was entered by the user
     * @return Result of processing the key-stroke
     */
    protected Result handleKeyStroke(KeyStroke keyStroke) {
        // Skip the keystroke if ctrl, alt or shift was down
        if(!keyStroke.isAltDown() && !keyStroke.isCtrlDown() && !keyStroke.isShiftDown()) {
            switch(keyStroke.getKeyType()) {
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
                case MouseEvent:
                    getBasePane().setFocusedInteractable(this);
                    return Result.HANDLED;
                default:
            }
        }
        return Result.UNHANDLED;
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return getRenderer().getCursorLocation(self());
    }

    @Override
    public InputFilter getInputFilter() {
        return inputFilter;
    }

    @Override
    public synchronized T setInputFilter(InputFilter inputFilter) {
        this.inputFilter = inputFilter;
        return self();
    }
}
