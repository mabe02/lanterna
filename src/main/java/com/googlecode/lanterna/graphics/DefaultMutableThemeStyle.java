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

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * This basic implementation of ThemeStyle keeps the styles in its internal state and allows you to mutate them. It can
 * be used to more easily override an existing theme and make small changes programmatically to it, see Issue409 in the
 * test section for an example of how to do this.
 * @see DelegatingThemeDefinition
 * @see DelegatingTheme
 * @see Theme
 */
public class DefaultMutableThemeStyle implements ThemeStyle {
    private TextColor foreground;
    private TextColor background;
    private EnumSet<SGR> sgrs;

    /**
     * Creates a new {@link DefaultMutableThemeStyle} based on an existing {@link ThemeStyle}. The values of this style
     * that is passed in will be copied into the new object that is created.
     * @param themeStyleToCopy {@link ThemeStyle} object to copy the style parameters from
     */
    public DefaultMutableThemeStyle(ThemeStyle themeStyleToCopy) {
        this(themeStyleToCopy.getForeground(),
                themeStyleToCopy.getBackground(),
                themeStyleToCopy.getSGRs());
    }

    /**
     * Creates a new {@link DefaultMutableThemeStyle} with a specified style (foreground, background and SGR state)
     * @param foreground Foreground color of the text with this style
     * @param background Background color of the text with this style
     * @param sgrs Modifiers to apply to the text with this style
     */
    public DefaultMutableThemeStyle(TextColor foreground, TextColor background, SGR... sgrs) {
        this(foreground, background, EnumSet.copyOf(Arrays.asList(sgrs)));
    }

    private DefaultMutableThemeStyle(TextColor foreground, TextColor background, EnumSet<SGR> sgrs) {
        if(foreground == null) {
            throw new IllegalArgumentException("Cannot set SimpleTheme's style foreground to null");
        }
        if(background == null) {
            throw new IllegalArgumentException("Cannot set SimpleTheme's style background to null");
        }
        this.foreground = foreground;
        this.background = background;
        this.sgrs = EnumSet.copyOf(sgrs);
    }

    @Override
    public TextColor getForeground() {
        return foreground;
    }

    @Override
    public TextColor getBackground() {
        return background;
    }

    @Override
    public EnumSet<SGR> getSGRs() {
        return EnumSet.copyOf(sgrs);
    }

    /**
     * Modifies the foreground color of this {@link DefaultMutableThemeStyle} to the value passed in
     * @param foreground New foreground color for this theme style
     * @return Itself
     */
    public DefaultMutableThemeStyle setForeground(TextColor foreground) {
        this.foreground = foreground;
        return this;
    }

    /**
     * Modifies the background color of this {@link DefaultMutableThemeStyle} to the value passed in
     * @param background New background color for this theme style
     * @return Itself
     */
    public DefaultMutableThemeStyle setBackground(TextColor background) {
        this.background = background;
        return this;
    }

    /**
     * Modifies the SGR modifiers of this {@link DefaultMutableThemeStyle} to the values passed it.
     * @param sgrs New SGR modifiers for this theme style, the values in this set will be copied into the internal state
     * @return Itself
     */
    public DefaultMutableThemeStyle setSGRs(EnumSet<SGR> sgrs) {
        this.sgrs = EnumSet.copyOf(sgrs);
        return this;
    }
}
