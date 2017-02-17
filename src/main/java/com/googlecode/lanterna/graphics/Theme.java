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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.gui2.WindowDecorationRenderer;
import com.googlecode.lanterna.gui2.WindowPostRenderer;

/**
 * The main theme interface, from which you can retrieve theme definitions
 * @author Martin
 */
public interface Theme {
    /**
     * Returns what this theme considers to be the default definition
     * @return The default theme definition
     */
    ThemeDefinition getDefaultDefinition();

    /**
     * Returns the theme definition associated with this class. The implementation of Theme should ensure that this
     * call never returns {@code null}, it should always give back a valid value (falling back to the default is nothing
     * else can be used).
     * @param clazz Class to get the theme definition for
     * @return The ThemeDefinition for the class passed in
     */
    ThemeDefinition getDefinition(Class<?> clazz);

    /**
     * Returns a post-renderer to invoke after drawing each window, unless the GUI system or individual windows has
     * their own renderers set. If {@code null}, no post-renderer will be done (unless the GUI system or the windows
     * has a post-renderer).
     * @return A {@link com.googlecode.lanterna.gui2.WindowPostRenderer} to invoke after drawing each window unless
     * overridden, or {@code null} if none
     */
    WindowPostRenderer getWindowPostRenderer();

    /**
     * Returns the {@link WindowDecorationRenderer} to use for windows drawn in this theme. If {@code null} then
     * lanterna will fall back to use {@link com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer}.
     *
     * @return The decoration renderer to use for this theme, or {@code null} to use system default
     */
    WindowDecorationRenderer getWindowDecorationRenderer();
}
