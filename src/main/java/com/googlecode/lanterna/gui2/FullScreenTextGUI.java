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
