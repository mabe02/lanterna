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

import java.util.Collection;

/**
 * Extension of the TextGUI interface, this is intended as the base interface for any TextGUI that intends to make use
 * of the Window class.
 * @author Martin
 */
public interface WindowBasedTextGUI extends ComponentBasedTextGUI {
    /**
     * Returns the window manager that is currently controlling this TextGUI. The window manager is in charge of placing
     * the windows on the surface and also deciding how they behave and move around.
     * @return Window manager that is currently controlling the windows in the terminal
     */
    WindowManager getWindowManager();

    WindowBasedTextGUI addWindow(Window window);

    WindowBasedTextGUI removeWindow(Window window);

    Collection<Window> getWindows();

    Window getActiveWindow();
}
