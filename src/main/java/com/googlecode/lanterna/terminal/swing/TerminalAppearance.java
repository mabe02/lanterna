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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.terminal.swing;

import java.awt.Font;

/**
 * This class will describe how a {@code SwingTerminal} is to be visually presented. 
 * You can create custom objects of this class to control how you want your
 * {@code SwingTerminal} to look.
 * @see SwingTerminal
 * @author Martin
 */
public class TerminalAppearance {
    
    public static final Font DEFAULT_NORMAL_FONT = createDefaultNormalFont();
    public static final Font DEFAULT_BOLD_FONT = createDefaultBoldFont();    
    public static final TerminalAppearance DEFAULT_APPEARANCE 
            = new TerminalAppearance(
                    DEFAULT_NORMAL_FONT,
                    DEFAULT_BOLD_FONT,
                    TerminalPalette.DEFAULT,
                    true);
    
    private static Font createDefaultNormalFont() {
        if(System.getProperty("os.name","").toLowerCase().indexOf("win") >= 0)
            return new Font("Courier New", Font.PLAIN, 14); //Monospaced can look pretty bad on Windows, so let's override it
        else
            return new Font("Monospaced", Font.PLAIN, 14);
    }

    private static Font createDefaultBoldFont() {
        if(System.getProperty("os.name","").toLowerCase().indexOf("win") >= 0)
            return new Font("Courier New", Font.BOLD, 14); //Monospaced can look pretty bad on Windows, so let's override it
        else
            return new Font("Monospaced", Font.BOLD, 14);
    }
        
    private final Font normalTextFont;
    private final Font boldTextFont;
    private final TerminalPalette colorPalette;
    private final boolean useBrightColorsOnBold;

    public TerminalAppearance(
            Font normalTextFont, 
            Font boldTextFont, 
            TerminalPalette colorPalette,
            boolean useBrightColorsOnBold) {
        
        this.normalTextFont = normalTextFont;
        this.boldTextFont = boldTextFont;
        this.colorPalette = colorPalette;
        this.useBrightColorsOnBold = useBrightColorsOnBold;
    }

    public Font getNormalTextFont() {
        return normalTextFont;
    }

    public Font getBoldTextFont() {
        return boldTextFont;
    }

    public TerminalPalette getColorPalette() {
        return colorPalette;
    }

    public boolean useBrightColorsOnBold() {
        return useBrightColorsOnBold;
    }
    
    public TerminalAppearance withFont(Font textFont) {
        return withFont(textFont, textFont);
    }
    
    public TerminalAppearance withFont(Font normalTextFont, Font boldTextFont) {
        return new TerminalAppearance(normalTextFont, boldTextFont, colorPalette, useBrightColorsOnBold);
    }
    
    public TerminalAppearance withPalette(TerminalPalette palette) {
        return new TerminalAppearance(normalTextFont, boldTextFont, palette, useBrightColorsOnBold);
    }
    
    public TerminalAppearance withUseBrightColors(boolean useBrightColorsOnBold) {
        return new TerminalAppearance(normalTextFont, boldTextFont, colorPalette, useBrightColorsOnBold);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TerminalAppearance other = (TerminalAppearance) obj;
        if (this.normalTextFont != other.normalTextFont && (this.normalTextFont == null || !this.normalTextFont.equals(other.normalTextFont))) {
            return false;
        }
        if (this.boldTextFont != other.boldTextFont && (this.boldTextFont == null || !this.boldTextFont.equals(other.boldTextFont))) {
            return false;
        }
        if (this.colorPalette != other.colorPalette && (this.colorPalette == null || !this.colorPalette.equals(other.colorPalette))) {
            return false;
        }
        if (this.useBrightColorsOnBold != other.useBrightColorsOnBold) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.normalTextFont != null ? this.normalTextFont.hashCode() : 0);
        hash = 37 * hash + (this.boldTextFont != null ? this.boldTextFont.hashCode() : 0);
        hash = 37 * hash + (this.colorPalette != null ? this.colorPalette.hashCode() : 0);
        hash = 37 * hash + (this.useBrightColorsOnBold ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "TerminalAppearance{" + 
                  "normalTextFont=" + normalTextFont + 
                ", boldTextFont=" + boldTextFont + 
                ", colorPalette=" + colorPalette + 
                ", useBrightColorsOnBold=" + useBrightColorsOnBold + '}';
    }
}
