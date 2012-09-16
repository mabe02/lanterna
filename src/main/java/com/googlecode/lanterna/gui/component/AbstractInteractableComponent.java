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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.listener.ComponentListener;
import com.googlecode.lanterna.terminal.TerminalPosition;

/**
 *
 * @author Martin
 */
public abstract class AbstractInteractableComponent extends AbstractComponent implements InteractableComponent {
    private boolean hasFocus;
    private TerminalPosition hotspot;

    public AbstractInteractableComponent() {
        hotspot = null;
    }

    @Override
    public final void onEnterFocus(FocusChangeDirection direction) {
        hasFocus = true;
        for(ComponentListener cl: getComponentListeners())
            cl.onComponentReceivedFocus(this);
        afterEnteredFocus(direction);
    }

    @Override
    public final void onLeaveFocus(FocusChangeDirection direction) {
        hasFocus = false;
        for(ComponentListener cl: getComponentListeners())
            cl.onComponentLostFocus(this);
        afterLeftFocus(direction);
    }

    protected void afterEnteredFocus(FocusChangeDirection direction) {
    }

    protected void afterLeftFocus(FocusChangeDirection direction) {
    }

    /**
     * This method will return true if the component currently has input focus in the GUI
     * @return {@code true} if the component has input focus, otherwise {@code false}
     */
    public boolean hasFocus() {
        return hasFocus;
    }

    @Override
    public TerminalPosition getHotspot() {
        return hotspot;
    }

    /**
     * Sets the hotspot, in global coordinates, of where to put the text cursor when this component
     * is active. If called with a {@code null} argument, the cursor will be hidden while this 
     * component has focus.
     * @param point Global coordinates of the hotspot position for this component, or {@code null}
     * if the cursor is to be hidden
     */
    protected void setHotspot(TerminalPosition point) {
        this.hotspot = point;
    }
}
