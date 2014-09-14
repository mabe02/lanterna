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
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 *
 * @author Martin
 */
public abstract class AbstractComponent implements Component {
    private TerminalSize size;
    private TerminalPosition position;
    private boolean invalid;
    private Border border;

    public AbstractComponent() {
        size = TerminalSize.ZERO;
        position = TerminalPosition.TOP_LEFT_CORNER;
        invalid = true;
        border = null;
    }

    /**
     * Implement this method and return how large you want the component to be, taking no borders into account.
     * AbstractComponent calls this in its implementation of {@code getPreferredSize()} and adds on border size
     * automatically.
     * @return Size this component would like to have, without taking borders into consideration
     */
    protected abstract TerminalSize getPreferredSizeWithoutBorder();

    protected void invalidate() {
        invalid = true;
    }

    @Override
    public void setSize(TerminalSize size) {
        this.size = size;
    }

    @Override
    public TerminalSize getSize() {
        return size;
    }

    @Override
    public void setPosition(TerminalPosition position) {
        this.position = position;
    }

    @Override
    public TerminalPosition getPosition() {
        return position;
    }

    @Override
    public TerminalSize getPreferredSize() {
        return getPreferredSizeWithoutBorder();
    }
    
    @Override
    public boolean isInvalid() {
        return invalid;
    }

    @Override
    public AbstractComponent withBorder(Border border) {
        this.border = border;
        return this;
    }
}
