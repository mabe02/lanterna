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
 * A ThemeDefinition contains a collection of ThemeStyle:s, which defines on a lower level which colors and SGRs to
 * apply if you want to draw according to the theme. The different style names are directly inspired from GTK 2. You can
 * also fetch character definitions which are stored inside of the theme, for example if you want to draw a border and
 * make the characters that make up the border customizable.
 *
 * @author Martin
 */
public interface ThemeDefinition {
    /**
     * The normal style of the definition, which can be considered the default to be used.
     * @return ThemeStyle representation for the normal style
     */
    ThemeStyle getNormal();

    /**
     * The pre-light style of this definition, which can be used when a component has input focus but isn't active or
     * selected, similar to mouse-hoovering in modern GUIs
     * @return ThemeStyle representation for the pre-light style
     */
    ThemeStyle getPreLight();

    /**
     * The "selected" style of this definition, which can used when a component has been actively selected in some way.
     * @return ThemeStyle representation for the selected style
     */
    ThemeStyle getSelected();

    /**
     * The "active" style of this definition, which can be used when a component is being directly interacted with
     * @return ThemeStyle representation for the active style
     */
    ThemeStyle getActive();

    /**
     * The insensitive style of this definition, which can be used when a component has been disabled or in some other
     * way isn't able to be interacted with.
     * @return ThemeStyle representation for the insensitive style
     */
    ThemeStyle getInsensitive();

    /**
     * Retrieves a custom ThemeStyle, if one is available by this name. You can use this if you need more categories
     * than the ones available above.
     * @param name Name of the style to look up
     * @return The ThemeStyle associated with the name
     */
    ThemeStyle getCustom(String name);

    /**
     * Retrieves a custom {@link ThemeStyle}, if one is available by this name. Will return a supplied default value if
     * no such style could be found within this {@link ThemeDefinition}. You can use this if you need more categories
     * than the ones available above.
     * @param name Name of the style to look up
     * @param defaultValue What to return if the there is no custom style by the given name
     * @return The {@link ThemeStyle} associated with the name, or {@code defaultValue} if there was no such style
     */
    ThemeStyle getCustom(String name, ThemeStyle defaultValue);

    /**
     * Retrieves a custom boolean property, if one is available by this name. Will return a supplied default value if
     * no such property could be found within this {@link ThemeDefinition}.
     * @param name Name of the boolean property to look up
     * @param defaultValue What to return if the there is no property with this name
     * @return The property value stored in this theme definition, parsed as a boolean
     */
    boolean getBooleanProperty(String name, boolean defaultValue);

    /**
     * Asks the theme definition for this component if the theme thinks that the text cursor should be visible or not.
     * Note that certain components might have a visible state depending on the context and the current data set, in
     * those cases it can use {@link #getBooleanProperty(String, boolean)} to allow themes more fine-grained control
     * over when cursor should be visible or not.
     * @return A hint to the renderer as to if this theme thinks the cursor should be visible (returns {@code true}) or
     * not (returns {@code false})
     */
    boolean isCursorVisible();

    /**
     * Retrieves a character from this theme definition by the specified name. This method cannot return {@code null} so
     * you need to give a fallback in case the definition didn't have any character by this name.
     * @param name Name of the character to look up
     * @param fallback Character to return if there was no character by the name supplied in this definition
     * @return The character from this definition by the name entered, or {@code fallback} if the definition didn't have
     * any character defined with this name
     */
    char getCharacter(String name, char fallback);

    /**
     * Returns a {@link ComponentRenderer} attached to this definition for the specified type. Generally one theme
     * definition is linked to only one component type so it wouldn't need the type parameter to figure out what to
     * return. unlike the other methods of this interface, it will not traverse up in the theme hierarchy if this field
     * is not defined, instead the component will use its default component renderer.
     * @param type Component class to get the theme's renderer for
     * @return Renderer to use for the {@code type} component or {@code null} to use the default
     * @param <T> Type of component
     */
    <T extends Component> ComponentRenderer<T> getRenderer(Class<T> type);
}
