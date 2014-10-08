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
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TextColor;
import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Color configuration settings to be using with SwingTerminal. This class contains color-related settings that is used
 * by SwingTerminal when it renders the component.
 * @author martin
 */
public class SwingTerminalColorConfiguration {

    /**
     * This is the default settings that is used when you create a new SwingTerminal without specifying any color
     * configuration. It will use classic VGA colors for the ANSI palette and bright colors on bold text.
     */
    public static final SwingTerminalColorConfiguration DEFAULT = newInstance(SwingTerminalPalette.STANDARD_VGA);
    private static final Map<TextColor, Color> COLOR_STORAGE = new ConcurrentHashMap<TextColor, Color>();

    /**
     * Creates a new color configuration based on a particular palette and with using brighter colors on bold text.
     * @param colorPalette Palette to use for this color configuration
     * @return The resulting color configuration
     */
    @SuppressWarnings("SameParameterValue")
    public static SwingTerminalColorConfiguration newInstance(SwingTerminalPalette colorPalette) {
        return new SwingTerminalColorConfiguration(colorPalette, true);
    }

    private final SwingTerminalPalette colorPalette;
    private final boolean useBrightColorsOnBold;

    private SwingTerminalColorConfiguration(SwingTerminalPalette colorPalette, boolean useBrightColorsOnBold) {
        this.colorPalette = colorPalette;
        this.useBrightColorsOnBold = useBrightColorsOnBold;
    }

    boolean isUsingBrightColorsOnBold() {
        return useBrightColorsOnBold;
    }

    /**
     * Returns a copy of this color configuration without the bright-colors-on-bold setting changed. With
     * the setting turned off, bold text will be rendered with the same foreground color as non-bold text. Default is
     * on, which renders bold text in a slightly brighter tone. This is commonly used by many terminal emulators.
     * @return Copy of this color configuration but with the bright-on-bold setting as specified
     */
    public SwingTerminalColorConfiguration withoutBrightColorsOnBold() {
        return new SwingTerminalColorConfiguration(colorPalette, false);
    }

    /**
     * Given a TextColor and a hint as to if the color is to be used as foreground or not and if we currently have
     * bold text enabled or not, it returns the closest AWT color that matches this.
     * @param color What text color to convert
     * @param isForeground Is the color intended to be used as foreground color
     * @param inBoldContext Is the color intended to be used for on a character this is bold
     * @return The AWT color that represents this text color
     */
    public Color toAWTColor(TextColor color, boolean isForeground, boolean inBoldContext) {
        if(COLOR_STORAGE.containsKey(color)) {
            return COLOR_STORAGE.get(color);
        }
        else if(color instanceof TextColor.ANSI) {
            return colorPalette.get((TextColor.ANSI)color, isForeground, inBoldContext && useBrightColorsOnBold);
        }
        else if(color instanceof TextColor.Indexed) {
            TextColor.Indexed indexedColor = (TextColor.Indexed)color;
            Color awtColor = new Color(indexedColor.getRed(), indexedColor.getGreen(), indexedColor.getBlue());
            COLOR_STORAGE.put(color, awtColor);
            return awtColor;
        }
        else if(color instanceof TextColor.RGB) {
            TextColor.RGB rgbColor = (TextColor.RGB)color;
            return new Color(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
        }
        throw new IllegalArgumentException("Unknown color " + color);
    }
}
