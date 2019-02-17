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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.gui2.WindowDecorationRenderer;
import com.googlecode.lanterna.gui2.WindowPostRenderer;

/**
 * Allows you to more easily wrap an existing theme and alter the behaviour in some special cases. You normally create a
 * new class that extends from this and override some of the methods to divert the call depending on what you are trying
 * to do. For an example, please see Issue409 in the test code.
 * @see DelegatingThemeDefinition
 * @see DefaultMutableThemeStyle
 * @see Theme
 */
public class DelegatingTheme implements Theme {
    private final Theme theme;

    /**
     * Creates a new {@link DelegatingTheme} with a default implementation that will forward all calls to the
     * {@link Theme} that is passed in.
     * @param theme Other theme to delegate all calls to
     */
    public DelegatingTheme(Theme theme) {
        this.theme = theme;
    }

    @Override
    public ThemeDefinition getDefaultDefinition() {
        return theme.getDefaultDefinition();
    }

    @Override
    public ThemeDefinition getDefinition(Class<?> clazz) {
        return theme.getDefinition(clazz);
    }

    @Override
    public WindowPostRenderer getWindowPostRenderer() {
        return theme.getWindowPostRenderer();
    }

    @Override
    public WindowDecorationRenderer getWindowDecorationRenderer() {
        return theme.getWindowDecorationRenderer();
    }
}
