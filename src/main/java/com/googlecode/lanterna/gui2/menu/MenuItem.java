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
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.googlecode.lanterna.gui2.menu;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

/**
 * Interface for menu items.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MenuItem {

    /**
     * Returns the title of the window (and text of menuitem).
     *
     * @return the title
     */
    public String getTitle();

    /**
     * Returns the Runnable to use.
     *
     * @param context the context to use
     * @return the runnable
     */
    public Runnable getRunnable(final WindowBasedTextGUI context);
}
