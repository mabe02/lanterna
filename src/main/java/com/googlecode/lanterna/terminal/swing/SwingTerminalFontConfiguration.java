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
package com.googlecode.lanterna.terminal.swing;

import java.awt.*;

/**
 * Font configuration class for {@link SwingTerminal} that is extending from {@link AWTTerminalFontConfiguration}
 */
public class SwingTerminalFontConfiguration extends AWTTerminalFontConfiguration {
    /**
     * This is the default font settings that will be used if you don't specify anything
     * @return A {@link SwingTerminal} font configuration object with default values set up
     */
    public static SwingTerminalFontConfiguration getDefault() {
        return newInstance(filterMonospaced(selectDefaultFont()));
    }

    /**
     * Creates a new font configuration from a list of fonts in order of priority. This works by having the terminal
     * attempt to draw each character with the fonts in the order they are specified in and stop once we find a font
     * that can actually draw the character. For ASCII characters, it's very likely that the first font will always be
     * used.
     * @param fontsInOrderOfPriority Fonts to use when drawing text, in order of priority
     * @return Font configuration built from the font list
     */
    @SuppressWarnings("WeakerAccess")
    public static SwingTerminalFontConfiguration newInstance(Font... fontsInOrderOfPriority) {
        return new SwingTerminalFontConfiguration(true, BoldMode.EVERYTHING_BUT_SYMBOLS, fontsInOrderOfPriority);
    }

    /**
     * Creates a new font configuration from a list of fonts in order of priority. This works by having the terminal
     * attempt to draw each character with the fonts in the order they are specified in and stop once we find a font
     * that can actually draw the character. For ASCII characters, it's very likely that the first font will always be
     * used.
     * @param useAntiAliasing If {@code true} then anti-aliasing should be enabled when drawing text
     * @param boldMode Option to control what to do when drawing text with the bold SGR enabled
     * @param fontsInOrderOfPriority Fonts to use when drawing text, in order of priority
     */
    public SwingTerminalFontConfiguration(boolean useAntiAliasing, BoldMode boldMode, Font... fontsInOrderOfPriority) {
        super(useAntiAliasing, boldMode, fontsInOrderOfPriority);
    }
}
