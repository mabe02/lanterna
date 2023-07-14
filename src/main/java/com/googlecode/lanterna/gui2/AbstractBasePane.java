/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.Interactable.Result;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;

/**
 * This abstract implementation of {@code BasePane} has the common code shared by all different concrete
 * implementations.
 */
public abstract class AbstractBasePane<T extends BasePane> implements BasePane {
    protected final ContentHolder contentHolder;
    private final CopyOnWriteArrayList<BasePaneListener<T>> listeners;
    protected InteractableLookupMap interactableLookupMap;
    private Interactable focusedInteractable;
    private boolean invalid;
    private boolean strictFocusChange;
    private boolean enableDirectionBasedMovements;
    private Theme theme;
    
    private Interactable mouseDownForDrag = null;

    protected AbstractBasePane() {
        this.contentHolder = new ContentHolder();
        this.listeners = new CopyOnWriteArrayList<>();
        this.interactableLookupMap = new InteractableLookupMap(new TerminalSize(80, 25));
        this.invalid = false;
        this.strictFocusChange = false;
        this.enableDirectionBasedMovements = true;
        this.theme = null;
    }

    @Override
    public boolean isInvalid() {
        return invalid || contentHolder.isInvalid();
    }

    @Override
    public void invalidate() {
        invalid = true;

        //Propagate
        contentHolder.invalidate();
    }

    @Override
    public void draw(TextGUIGraphics graphics) {
        graphics.applyThemeStyle(getTheme().getDefinition(Window.class).getNormal());
        graphics.fill(' ');

        if(!interactableLookupMap.getSize().equals(graphics.getSize())) {
            interactableLookupMap = new InteractableLookupMap(graphics.getSize());
        } else {
            interactableLookupMap.reset();
        }

        contentHolder.draw(graphics);
        contentHolder.updateLookupMap(interactableLookupMap);
        //interactableLookupMap.debug();
        invalid = false;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        // Fire events first and decide if the event should be sent to the focused component or not
        AtomicBoolean deliverEvent = new AtomicBoolean(true);
        for (BasePaneListener<T> listener : listeners) {
            listener.onInput(self(), key, deliverEvent);
        }
        if (!deliverEvent.get()) {
            return true;
        }

        // Now try to deliver the event to the focused component
        boolean handled = doHandleInput(key);

        // If it wasn't handled, fire the listeners and decide what to report to the TextGUI
        if(!handled) {
            AtomicBoolean hasBeenHandled = new AtomicBoolean(false);
            for(BasePaneListener<T> listener: listeners) {
                listener.onUnhandledInput(self(), key, hasBeenHandled);
            }
            handled = hasBeenHandled.get();
        }
        return handled;
    }

    abstract T self();

    private boolean doHandleInput(KeyStroke key) {
        boolean result = false;
        if(key.getKeyType() == KeyType.MouseEvent) {
           return handleMouseInput((MouseAction) key);
        }
        Interactable.FocusChangeDirection direction = Interactable.FocusChangeDirection.TELEPORT; // Default
        Interactable nextFocus = null;
        if(focusedInteractable == null) {
            // If nothing is focused and the user presses certain navigation keys, try to find if there is an
            // Interactable component we can move focus to.
            MenuBar menuBar = getMenuBar();
            Component baseComponent = getComponent();
            switch (key.getKeyType()) {
                case Tab:
                case ArrowRight:
                case ArrowDown:
                    direction = Interactable.FocusChangeDirection.NEXT;
                    // First try the menu, then the actual component
                    nextFocus = menuBar.nextFocus(null);
                    if (nextFocus == null) {
                        if (baseComponent instanceof Container) {
                            nextFocus = ((Container) baseComponent).nextFocus(null);
                        } else if (baseComponent instanceof Interactable) {
                            nextFocus = (Interactable) baseComponent;
                        }
                    }
                    break;

                case ReverseTab:
                case ArrowUp:
                case ArrowLeft:
                    direction = Interactable.FocusChangeDirection.PREVIOUS;
                    if (baseComponent instanceof Container) {
                        nextFocus = ((Container) baseComponent).previousFocus(null);
                    }
                    else if (baseComponent instanceof Interactable) {
                        nextFocus = (Interactable) baseComponent;
                    }
                    // If no component can take focus, try the menu
                    if (nextFocus == null) {
                        nextFocus = menuBar.previousFocus(null);
                    }
                    break;
            }
            if (nextFocus != null) {
                setFocusedInteractable(nextFocus, direction);
                result = true;
            }
        } else {
            Interactable.Result handleResult = focusedInteractable.handleInput(key);
            if(!enableDirectionBasedMovements) {
                if(handleResult == Interactable.Result.MOVE_FOCUS_DOWN || handleResult == Interactable.Result.MOVE_FOCUS_RIGHT) {
                    handleResult = Interactable.Result.MOVE_FOCUS_NEXT;
                }
                else if(handleResult == Interactable.Result.MOVE_FOCUS_UP || handleResult == Interactable.Result.MOVE_FOCUS_LEFT) {
                    handleResult = Interactable.Result.MOVE_FOCUS_PREVIOUS;
                }
            }
            switch (handleResult) {
                case HANDLED:
                    result = true;
                    break;
                case UNHANDLED:
                    //Filter the event recursively through all parent containers until we hit null; give the containers
                    //a chance to absorb the event
                    Container parent = focusedInteractable.getParent();
                    while(parent != null) {
                        if(parent.handleInput(key)) {
                            return true;
                        }
                        parent = parent.getParent();
                    }
                    result = false;
                    break;
                case MOVE_FOCUS_NEXT:
                    nextFocus = contentHolder.nextFocus(focusedInteractable);
                    if(nextFocus == null) {
                        nextFocus = contentHolder.nextFocus(null);
                    }
                    direction = Interactable.FocusChangeDirection.NEXT;
                    break;
                case MOVE_FOCUS_PREVIOUS:
                    nextFocus = contentHolder.previousFocus(focusedInteractable);
                    if(nextFocus == null) {
                        nextFocus = contentHolder.previousFocus(null);
                    }
                    direction = Interactable.FocusChangeDirection.PREVIOUS;
                    break;
                case MOVE_FOCUS_DOWN:
                    nextFocus = interactableLookupMap.findNextDown(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.DOWN;
                    if(nextFocus == null && !strictFocusChange) {
                        nextFocus = contentHolder.nextFocus(focusedInteractable);
                        direction = Interactable.FocusChangeDirection.NEXT;
                    }
                    break;
                case MOVE_FOCUS_LEFT:
                    nextFocus = interactableLookupMap.findNextLeft(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.LEFT;
                    break;
                case MOVE_FOCUS_RIGHT:
                    nextFocus = interactableLookupMap.findNextRight(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.RIGHT;
                    break;
                case MOVE_FOCUS_UP:
                    nextFocus = interactableLookupMap.findNextUp(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.UP;
                    if(nextFocus == null && !strictFocusChange) {
                        nextFocus = contentHolder.previousFocus(focusedInteractable);
                        direction = Interactable.FocusChangeDirection.PREVIOUS;
                    }
                    break;
            }
        }
        if(nextFocus != null) {
            setFocusedInteractable(nextFocus, direction);
            result = true;
        }
        return result;
    }
    
    private boolean handleMouseInput(MouseAction mouseAction) {
        TerminalPosition localCoordinates = fromGlobal(mouseAction.getPosition());
        if (localCoordinates == null) {
           return false;
        }
        Interactable interactable = interactableLookupMap.getInteractableAt(localCoordinates);
        if (mouseAction.isMouseDown()) {
            mouseDownForDrag = interactable;
        }
        Interactable wasMouseDownForDrag = mouseDownForDrag;
        if (mouseAction.isMouseUp()) {
            mouseDownForDrag = null;
        }
        if (mouseAction.isMouseDrag() && mouseDownForDrag != null) {
            return mouseDownForDrag.handleInput(mouseAction) == Result.HANDLED;
        }
        if (interactable == null) {
           return false;
        }
        if (mouseAction.isMouseUp()) {
            // MouseUp only handled by same interactable as MouseDown
            if (wasMouseDownForDrag == interactable) {
                return interactable.handleInput(mouseAction) == Result.HANDLED;
            }
            // did not handleInput because mouse up was not on component mouse down was on
            return false;
        }
        return interactable.handleInput(mouseAction) == Result.HANDLED;
     }

    @Override
    public Component getComponent() {
        return contentHolder.getComponent();
    }

    @Override
    public void setComponent(Component component) {
        contentHolder.setComponent(component);
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
        return focusedInteractable.toBasePane(position);
    }

    @Override
    public void setFocusedInteractable(Interactable toFocus) {
        setFocusedInteractable(toFocus,
                toFocus != null ?
                    Interactable.FocusChangeDirection.TELEPORT : Interactable.FocusChangeDirection.RESET);
    }

    protected void setFocusedInteractable(Interactable toFocus, Interactable.FocusChangeDirection direction) {
        if(focusedInteractable == toFocus) {
            return;
        }
        if(toFocus != null && !toFocus.isEnabled()) {
            return;
        }
        if(focusedInteractable != null) {
            focusedInteractable.onLeaveFocus(direction, toFocus);
        }
        Interactable previous = focusedInteractable;
        focusedInteractable = toFocus;
        if(toFocus != null) {
            toFocus.onEnterFocus(direction, previous);
        }
        invalidate();
    }

    @Override
    public void setStrictFocusChange(boolean strictFocusChange) {
        this.strictFocusChange = strictFocusChange;
    }

    @Override
    public void setEnableDirectionBasedMovements(boolean enableDirectionBasedMovements) {
        this.enableDirectionBasedMovements = enableDirectionBasedMovements;
    }

    @Override
    public synchronized Theme getTheme() {
        if(theme != null) {
            return theme;
        }
        else if(getTextGUI() != null) {
            return getTextGUI().getTheme();
        }
        return null;
    }

    @Override
    public synchronized void setTheme(Theme theme) {
        this.theme = theme;
        invalidate();
    }

    @Override
    public MenuBar getMenuBar() {
        return contentHolder.getMenuBar();
    }

    @Override
    public void setMenuBar(MenuBar menuBar) {
        contentHolder.setMenuBar(menuBar);
    }

    protected void addBasePaneListener(BasePaneListener<T> basePaneListener) {
        listeners.addIfAbsent(basePaneListener);
    }

    protected void removeBasePaneListener(BasePaneListener<T> basePaneListener) {
        listeners.remove(basePaneListener);
    }

    protected List<BasePaneListener<T>> getBasePaneListeners() {
        return listeners;
    }

    protected class ContentHolder extends AbstractComposite<Container> {
        private MenuBar menuBar;

        ContentHolder() {
            this.menuBar = new EmptyMenuBar();
        }

        private void setMenuBar(MenuBar menuBar) {
            if (menuBar == null) {
                menuBar = new EmptyMenuBar();
            }

            if (this.menuBar != menuBar) {
                menuBar.onAdded(this);
                this.menuBar.onRemoved(this);
                this.menuBar = menuBar;
                if(focusedInteractable == null) {
                    setFocusedInteractable(menuBar.nextFocus(null));
                }
                invalidate();
            }
        }

        private MenuBar getMenuBar() {
            return menuBar;
        }

        @Override
        public boolean isInvalid() {
            return super.isInvalid() || menuBar.isInvalid();
        }

        @Override
        public void invalidate() {
            super.invalidate();
            menuBar.invalidate();
        }

        @Override
        public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
            super.updateLookupMap(interactableLookupMap);
            menuBar.updateLookupMap(interactableLookupMap);
        }

        @Override
        public void setComponent(Component component) {
            if(getComponent() == component) {
                return;
            }
            setFocusedInteractable(null);
            super.setComponent(component);
            if(focusedInteractable == null && component instanceof Interactable) {
                setFocusedInteractable((Interactable)component);
            }
            else if(focusedInteractable == null && component instanceof Container) {
                setFocusedInteractable(((Container)component).nextFocus(null));
            }
        }

        public boolean removeComponent(Component component) {
            boolean removed = super.removeComponent(component);
            if (removed) {
                focusedInteractable = null;
            }
            return removed;
        }

        @Override
        public TextGUI getTextGUI() {
            return AbstractBasePane.this.getTextGUI();
        }

        @Override
        protected ComponentRenderer<Container> createDefaultRenderer() {
            return new ComponentRenderer<Container>() {
                @Override
                public TerminalSize getPreferredSize(Container component) {
                    Component subComponent = getComponent();
                    if(subComponent == null) {
                        return TerminalSize.ZERO;
                    }
                    return subComponent.getPreferredSize();
                }

                @Override
                public void drawComponent(TextGUIGraphics graphics, Container component) {
                    if (!(menuBar instanceof EmptyMenuBar)) {
                        int menuBarHeight = menuBar.getPreferredSize().getRows();
                        TextGUIGraphics menuGraphics = graphics.newTextGraphics(TerminalPosition.TOP_LEFT_CORNER, graphics.getSize().withRows(menuBarHeight));
                        menuBar.draw(menuGraphics);
                        graphics = graphics.newTextGraphics(TerminalPosition.TOP_LEFT_CORNER.withRelativeRow(menuBarHeight), graphics.getSize().withRelativeRows(-menuBarHeight));
                    }

                    Component subComponent = getComponent();
                    if(subComponent == null) {
                        return;
                    }
                    subComponent.draw(graphics);
                }
            };
        }

        @Override
        public TerminalPosition toGlobal(TerminalPosition position) {
            return AbstractBasePane.this.toGlobal(position);
        }

        @Override
        public TerminalPosition toBasePane(TerminalPosition position) {
            return position;
        }

        @Override
        public BasePane getBasePane() {
            return AbstractBasePane.this;
        }
    }

    private static class EmptyMenuBar extends MenuBar {
        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public synchronized void onAdded(Container container) {
        }

        @Override
        public synchronized void onRemoved(Container container) {
        }
        
        @Override
        public boolean isEmptyMenuBar() {
            return true;
        }
    }
}
