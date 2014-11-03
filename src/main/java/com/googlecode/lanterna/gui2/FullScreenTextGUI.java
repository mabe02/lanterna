package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

/**
 * This class is a TextGUI implementation that consists of one large object that takes up the entire Screen. You
 * probably set a container object and fill it with sub-components, but you can also just use one large component for
 * the whole TextGUI. This class doesn't support Windows directly, but you can open modal dialogs.
 * @author Martin
 */
public class FullScreenTextGUI extends AbstractTextGUI {

    private final ThisRootContainer rootContainer;

    /**
     * Creates a new FullScreenTextGUI targeting the supplied Screen
     *
     * @param screen Screen this FullScreenTextGUI should use
     */
    public FullScreenTextGUI(Screen screen) {
        super(screen);
        this.rootContainer = new ThisRootContainer();
    }

    /**
     * Sets which component (probably a Container) that is the given the full screen. Probably you will assign a panel
     * or some other container that can be filled with other components. You cannot pass in {@code null} here.
     * @param component Component to display in this TextGUI
     */
    public void setComponent(Component component) {
        if(component == null) {
            throw new IllegalArgumentException("Cannot set FullScreenTextGUI component to null");
        }
        rootContainer.getContentArea().removeAllComponents();
        rootContainer.getContentArea().addComponent(component);
    }

    /**
     * Returns the one component which this FullScreenTextGUI is drawing in full screen.
     * @return Component this FullScreenTextGUI is using as the root
     */
    public Component getComponent() {
        if(rootContainer.getContentArea().getNumberOfComponents() == 0) {
            return null;
        }
        return rootContainer.getContentArea().getComponentAt(0);
    }

    @Override
    protected void drawGUI(TextGUIGraphics graphics) {
        rootContainer.draw(graphics);
    }

    @Override
    protected TerminalPosition getCursorPosition() {
        return rootContainer.getCursorPosition();
    }

    @Override
    protected boolean handleInput(KeyStroke key) {
        return rootContainer.handleInput(key);
    }

    private class ThisRootContainer extends AbstractRootContainer {
        @Override
        public TerminalPosition toGlobal(TerminalPosition localPosition) {
            return localPosition;
        }

        @Override
        public void draw(TextGUIGraphics graphics) {
            super.draw(graphics);
        }
    }
}
