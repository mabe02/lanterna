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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Created by martin on 27/10/14.
 */
public abstract class AbstractBasePane implements BasePane {
    protected final ContentArea contentArea;
    protected InteractableLookupMap interactableLookupMap;
    private Interactable focusedInteractable;
    private boolean invalid;

    protected AbstractBasePane() {
        this.contentArea = new ContentArea();
        this.interactableLookupMap = new InteractableLookupMap(new TerminalSize(80, 20));
        this.invalid = false;
    }

    @Override
    public boolean isInvalid() {
        return invalid || contentArea.isInvalid();
    }

    protected void invalidate() {
        invalid = true;
    }

    @Override
    public void draw(TextGUIGraphics graphics) {
        graphics.applyThemeStyle(graphics.getThemeDefinition(Window.class).getNormal());
        graphics.fill(' ');
        contentArea.draw(graphics);

        if(!interactableLookupMap.getSize().equals(graphics.getSize())) {
            interactableLookupMap = new InteractableLookupMap(graphics.getSize());
        }
        interactableLookupMap.reset();
        contentArea.updateLookupMap(interactableLookupMap);
        //interactableLookupMap.debug();
        invalid = false;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if(focusedInteractable != null) {
            Interactable next = null;
            Interactable.FocusChangeDirection direction = Interactable.FocusChangeDirection.TELEPORT; //Default
            switch (focusedInteractable.handleKeyStroke(key)) {
                case HANDLED:
                    return true;
                case UNHANDLED:
                    break;
                case MOVE_FOCUS_NEXT:
                    next = contentArea.nextFocus(focusedInteractable);
                    if(next == null) {
                        next = contentArea.nextFocus(null);
                    }
                    direction = Interactable.FocusChangeDirection.NEXT;
                    break;
                case MOVE_FOCUS_PREVIOUS:
                    next = contentArea.previousFocus(focusedInteractable);
                    if(next == null) {
                        next = contentArea.previousFocus(null);
                    }
                    direction = Interactable.FocusChangeDirection.PREVIOUS;
                    break;
                case MOVE_FOCUS_DOWN:
                    next = interactableLookupMap.findNextDown(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.DOWN;
                    break;
                case MOVE_FOCUS_LEFT:
                    next = interactableLookupMap.findNextLeft(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.LEFT;
                    break;
                case MOVE_FOCUS_RIGHT:
                    next = interactableLookupMap.findNextRight(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.RIGHT;
                    break;
                case MOVE_FOCUS_UP:
                    next = interactableLookupMap.findNextUp(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.UP;
                    break;
            }
            if(next != null) {
                setFocusedInteractable(next, direction);
            }
            return true;
        }
        return false;
    }

    @Override
    public Composite getContentArea() {
        return contentArea;
    }

    @Override
    public Interactable getFocusedInteractable() {
        return focusedInteractable;
    }

    @Override
    public TerminalPosition getCursorPosition() {
        if(focusedInteractable == null) {
            return null;
        }
        TerminalPosition position = focusedInteractable.getCursorLocation();
        if(position == null) {
            return null;
        }
        //Don't allow the component to set the cursor outside of its own boundaries
        if(position.getColumn() < 0 ||
                position.getRow() < 0 ||
                position.getColumn() >= focusedInteractable.getSize().getColumns() ||
                position.getRow() >= focusedInteractable.getSize().getRows()) {
            return null;
        }
        return focusedInteractable.toRootContainer(position);
    }

    @Override
    public void setFocusedInteractable(Interactable toFocus) {
        setFocusedInteractable(toFocus,
                toFocus != null ?
                    Interactable.FocusChangeDirection.TELEPORT : Interactable.FocusChangeDirection.RESET);
    }

    protected void setFocusedInteractable(Interactable toFocus, Interactable.FocusChangeDirection direction) {
        if(focusedInteractable != null) {
            focusedInteractable.onLeaveFocus(direction, focusedInteractable);
        }
        Interactable previous = focusedInteractable;
        focusedInteractable = toFocus;
        if(toFocus != null) {
            toFocus.onEnterFocus(direction, previous);
        }
    }

    protected class ContentArea extends AbstractInteractableComposite {
        @Override
        public void setComponent(Component component) {
            super.addComponent(component);
            if(focusedInteractable == null && component instanceof Interactable) {
                setFocusedInteractable((Interactable)component);
            }
            else if(focusedInteractable == null && component instanceof InteractableContainer) {
                setFocusedInteractable(((InteractableContainer)component).nextFocus(null));
            }
        }

        @Override
        public TerminalSize calculatePreferredSize() {
            Component component = getComponent();
            if(component == null) {
                return TerminalSize.ZERO;
            }
            return component.getPreferredSize();
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics) {
            Component component = getComponent();
            if(component == null) {
                return;
            }
            component.draw(graphics);
        }

        @Override
        public TerminalPosition toRootContainer(TerminalPosition position) {
            return position;
        }

        @Override
        public void removeComponent(Component component) {
            super.removeComponent(component);
            focusedInteractable = null;
        }

        @Override
        public BasePane getRootContainer() {
            return AbstractBasePane.this;
        }
    }
}
