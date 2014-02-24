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

import com.googlecode.lanterna.input.Key;
import java.util.Collection;

/**
 *
 * @author Martin
 */
public interface WindowManager {
    
    void addWindow(Window window, Hint... windowManagerHints);
    void removeWindow(Window window);
    
    Collection<Window> getWindows();
    Window getActiveWindow();
    boolean handleInput(Key key);
    boolean isInvalid();
    
    public static class Hint {
        protected Hint() {
        }
    }
}
