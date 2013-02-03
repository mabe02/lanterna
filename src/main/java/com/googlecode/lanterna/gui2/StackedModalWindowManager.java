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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.Key;

/**
 *
 * @author Martin
 */
public class StackedModalWindowManager implements WindowManager {

    @Override
    public void addWindow(Window window, Hint... windowManagerHints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeWindow(Window window) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Window getActiveWindow() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void handleInput(Key key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
