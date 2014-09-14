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

/**
 *
 * @author Martin
 */
public interface Component extends TextGUIElement {
    /**
     * Returns the top-left corner of this component, measured from its parent.
     * @return Position of this component
     */
    TerminalPosition getPosition();

    /**
     * This method will be called by the layout manager when it has decided where the component is to be located. If you
     * call this method yourself, prepare for unexpected results.
     * @param position Top-left position of the component, relative to its parent
     */
    void setPosition(TerminalPosition position);

    /**
     * Returns how large this component is. If the layout manager has not yet laid this component out, it will return
     * an empty size (0x0)
     * @return How large this component is
     */
    TerminalSize getSize();

    /**
     * This method will be called by the layout manager when it has decided how large the component will be. If you call
     * this method yourself, prepare for unexpected results.
     * @param size Current size of the component
     */
    void setSize(TerminalSize size);

    /**
     * Returns the ideal size this component would like to have, in order to draw itself properly. There are no
     * guarantees the GUI system will decide to give it this size though.
     * @return Size we would like to be
     */
    TerminalSize getPreferredSize();

    /**
     * Adds a border around the component.
     * @param border Border to use around the component
     * @return Itself
     */
    Component withBorder(Border border);
}
