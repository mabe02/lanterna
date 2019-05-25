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

/**
 * Expanded TextGraphics that adds methods to interact with themes
 * @author Martin
 */
public interface ThemedTextGraphics extends TextGraphics {
    /**
     * Takes a ThemeStyle as applies it to this TextGraphics. This will effectively set the foreground color, the
     * background color and all the SGRs.
     * @param themeStyle ThemeStyle to apply
     * @return Itself
     */
    ThemedTextGraphics applyThemeStyle(ThemeStyle themeStyle);
}
