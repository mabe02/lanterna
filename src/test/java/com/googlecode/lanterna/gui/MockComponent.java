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
package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.TerminalSize;

/**
 *
 * @author Martin
 */
public class MockComponent extends AbstractComponent {
    private final char fillCharacter;

    public MockComponent(char fillCharacter, TerminalSize preferredSize) {
        this.fillCharacter = fillCharacter;
        setPreferredSize(preferredSize);
    }

    //Should never be called
    @Override
    protected TerminalSize calculatePreferredSize() {
        return new TerminalSize(1, 1);
    }

    @Override
    public void repaint(TextGraphics graphics) {
        graphics.fillArea(fillCharacter);
    }
}
