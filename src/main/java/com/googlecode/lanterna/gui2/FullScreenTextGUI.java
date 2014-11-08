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
public class FullScreenTextGUI extends AbstractTextGUI implements BasePane {

    private final ThisBasePane basePane;

    /**
     * Creates a new FullScreenTextGUI targeting the supplied Screen
     *
     * @param screen Screen this FullScreenTextGUI should use
     */
    public FullScreenTextGUI(Screen screen) {
        super(screen);
        this.basePane = new ThisBasePane();
    }
    
    @Override
    protected void drawGUI(TextGUIGraphics graphics) {
        draw(graphics);
    }
    
    private class ThisBasePane extends AbstractBasePane {
        @Override
        public TerminalPosition toGlobal(TerminalPosition localPosition) {
            return localPosition;
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // Delegate all BasePane method calls to the ThisBasePane instance //
    /////////////////////////////////////////////////////////////////////
    @Override
    public boolean isInvalid() {
        return basePane.isInvalid();
    }

    @Override
    public void draw(TextGUIGraphics graphics) {
        basePane.draw(graphics);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return basePane.handleInput(key);
    }
    
    @Override
    public void setComponent(Component component) {
        basePane.setComponent(component);
    }
    
    @Override
    public Component getComponent() {
        return basePane.getComponent();
    }
    
    @Override
    public Interactable getFocusedInteractable() {
        return basePane.getFocusedInteractable();
    }

    @Override
    public TerminalPosition getCursorPosition() {
        return basePane.getCursorPosition();
    }
    
    @Override
    public void setFocusedInteractable(Interactable toFocus) {
        basePane.setFocusedInteractable(toFocus);
    }

    @Override
    public TerminalPosition toGlobal(TerminalPosition localPosition) {
        return basePane.toGlobal(localPosition);
    }
}
