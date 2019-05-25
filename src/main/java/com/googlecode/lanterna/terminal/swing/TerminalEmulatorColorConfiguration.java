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

import com.googlecode.lanterna.TextColor;
import java.awt.Color;

/**
 * Color configuration settings to be using with SwingTerminal. This class contains color-related settings that is used
 * by SwingTerminal when it renders the component.
 * @author martin
 */
public class TerminalEmulatorColorConfiguration {

    /**
     * This is the default settings that is used when you create a new SwingTerminal without specifying any color
     * configuration. It will use classic VGA colors for the ANSI palette and bright colors on bold text.
     * @return A terminal emulator color configuration object with values set to classic VGA palette
     */
    public static TerminalEmulatorColorConfiguration getDefault() {
        return newInstance(TerminalEmulatorPalette.STANDARD_VGA);
    }

    /**
     * Creates a new color configuration based on a particular palette and with using brighter colors on bold text.
     * @param colorPalette Palette to use for this color configuration
     * @return The resulting color configuration
     */
    @SuppressWarnings("SameParameterValue")
    public static TerminalEmulatorColorConfiguration newInstance(TerminalEmulatorPalette colorPalette) {
        return new TerminalEmulatorColorConfiguration(colorPalette, true);
    }

    private final TerminalEmulatorPalette colorPalette;
    private final boolean useBrightColorsOnBold;

    private TerminalEmulatorColorConfiguration(TerminalEmulatorPalette colorPalette, boolean useBrightColorsOnBold) {
        this.colorPalette = colorPalette;
        this.useBrightColorsOnBold = useBrightColorsOnBold;
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
        if(color instanceof TextColor.ANSI) {
            return colorPalette.get((TextColor.ANSI)color, isForeground, inBoldContext && useBrightColorsOnBold);
        }
        return color.toColor();
    }
}
