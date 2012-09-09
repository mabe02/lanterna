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
package com.googlecode.lanterna.gui.listener;

import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.input.Key;

/**
 * This adapter class is implementing {@code WindowListener} and provides an implementation for all
 * the methods that does nothing. By extending this class, you can provide your own implementation
 * to only the events you care about.
 * @see WindowListener
 * @author Martin
 */
public class WindowAdapter implements WindowListener {
    
    @Override
    public void onWindowInvalidated(Window window) {
        
    }

    @Override
    public void onWindowClosed(Window window) {
        
    }

    @Override
    public void onWindowShown(Window window) {
        
    }

    @Override
    public void onUnhandledKeyboardInteraction(Window window, Key key) {
        
    }

    @Override
    public void onFocusChanged(Window window, Interactable fromComponent, Interactable toComponent) {

    }
}
