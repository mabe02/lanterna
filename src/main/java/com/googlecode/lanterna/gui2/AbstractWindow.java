package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;

import java.util.Collections;
import java.util.Set;

/**
 * Abstract Window implementation that contains much code that is shared between different concrete Window
 * implementations.
 * @author Martin
 */
public class AbstractWindow implements Window {
    private final ContentArea contentArea;

    private String title;
    private WindowManager windowManager;
    private boolean visible;
    private boolean invalid;
    private TerminalSize lastKnownSize;
    private TerminalSize lastKnownDecoratedSize;
    private TerminalPosition lastKnownPosition;
    private Interactable focusedInteractable;
    private InteractableLookupMap interactableLookupMap;

    public AbstractWindow() {
        this("");
    }

    public AbstractWindow(String title) {
        this.contentArea = new ContentArea();
        this.title = title;
        this.visible = true;
        this.invalid = false;
        this.lastKnownPosition = null;
        this.lastKnownSize = null;
        this.lastKnownDecoratedSize = null;
        this.interactableLookupMap = new InteractableLookupMap(new TerminalSize(80, 20));
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean isInvalid() {
        return invalid || contentArea.isInvalid();
    }

    @Override
    public void draw(TextGUIGraphics graphics) {
        lastKnownSize = graphics.getSize();
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
        if(key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
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
    public Container getContentArea() {
        return contentArea;
    }

    @Override
    public TerminalSize getPreferredSize() {
        return contentArea.getPreferredSize();
    }

    @Override
    public Set<Hint> getHints() {
        return Collections.emptySet();
    }

    @Override
    public Interactable getFocusedInteractable() {
        return focusedInteractable;
    }

    @Override
    public final TerminalPosition getPosition() {
        return lastKnownPosition;
    }

    @Override
    public final void setPosition(TerminalPosition topLeft) {
        this.lastKnownPosition = topLeft;
    }

    @Override
    public final TerminalSize getSize() {
        return lastKnownSize;
    }

    @Override
    public final TerminalSize getDecoratedSize() {
        return lastKnownDecoratedSize;
    }

    @Override
    public final void setDecoratedSize(TerminalSize decoratedSize) {
        this.lastKnownDecoratedSize = decoratedSize;
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
    public TerminalPosition toGlobal(TerminalPosition localPosition) {
        if(localPosition == null) {
            return null;
        }
        return lastKnownPosition.withRelative(localPosition);
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

    @Override
    public void close() {
        contentArea.dispose();
        if(windowManager == null) {
            throw new IllegalStateException("Cannot close " + toString() + " because it is not managed by any window manager");
        }
        windowManager.removeWindow(this);
    }

    private void invalidate() {
        invalid = true;
    }

    private class ContentArea extends AbstractInteractableComposite {
        @Override
        public void addComponent(Component component) {
            super.addComponent(component);
            if(focusedInteractable == null && component instanceof Interactable) {
                setFocusedInteractable((Interactable)component);
            }
            else if(focusedInteractable == null && component instanceof InteractableComposite) {
                setFocusedInteractable(((InteractableComposite)component).nextFocus(null));
            }
        }

        @Override
        public TerminalPosition toRootContainer(TerminalPosition position) {
            return position;
        }

        @Override
        public void removeComponent(Component component) {
            super.removeComponent(component);
        }

        @Override
        public RootContainer getRootContainer() {
            return AbstractWindow.this;
        }
    }
}
