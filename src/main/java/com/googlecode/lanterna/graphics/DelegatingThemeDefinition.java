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

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.ComponentRenderer;

/**
 * Allows you to more easily wrap an existing theme definion and alter the behaviour in some special cases. You normally
 * create a new class that extends from this and override some of the methods to divert the call depending on what you
 * are trying to do. For an example, please see Issue409 in the test code.
 * @see DelegatingTheme
 * @see DefaultMutableThemeStyle
 * @see Theme
 */
public class DelegatingThemeDefinition implements ThemeDefinition {
    private final ThemeDefinition themeDefinition;

    /**
     * Creates a new {@link DelegatingThemeDefinition} with a default implementation that will forward all calls to the
     * {@link ThemeDefinition} that is passed in.
     * @param themeDefinition Other theme definition to delegate all calls to
     */
    public DelegatingThemeDefinition(ThemeDefinition themeDefinition) {
        this.themeDefinition = themeDefinition;
    }

    @Override
    public ThemeStyle getNormal() {
        return themeDefinition.getNormal();
    }

    @Override
    public ThemeStyle getPreLight() {
        return themeDefinition.getPreLight();
    }

    @Override
    public ThemeStyle getSelected() {
        return themeDefinition.getSelected();
    }

    @Override
    public ThemeStyle getActive() {
        return themeDefinition.getActive();
    }

    @Override
    public ThemeStyle getInsensitive() {
        return themeDefinition.getInsensitive();
    }

    @Override
    public ThemeStyle getCustom(String name) {
        return themeDefinition.getCustom(name);
    }

    @Override
    public ThemeStyle getCustom(String name, ThemeStyle defaultValue) {
        return themeDefinition.getCustom(name, defaultValue);
    }

    @Override
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        return themeDefinition.getBooleanProperty(name, defaultValue);
    }

    @Override
    public boolean isCursorVisible() {
        return themeDefinition.isCursorVisible();
    }

    @Override
    public char getCharacter(String name, char fallback) {
        return themeDefinition.getCharacter(name, fallback);
    }

    @Override
    public <T extends Component> ComponentRenderer<T> getRenderer(Class<T> type) {
        return themeDefinition.getRenderer(type);
    }
}
