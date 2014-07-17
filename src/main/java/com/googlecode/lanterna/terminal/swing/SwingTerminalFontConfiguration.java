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

import com.googlecode.lanterna.TextCharacter;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author martin
 */
public class SwingTerminalFontConfiguration {

    public static final SwingTerminalFontConfiguration DEFAULT = newInstance(
            System.getProperty("os.name","").toLowerCase().contains("win") ?
                new Font("Courier New", Font.PLAIN, 14) //Monospaced can look pretty bad on Windows, so let's override it
                :
                new Font("Monospaced", Font.PLAIN, 14)
    );

    @SuppressWarnings("WeakerAccess")
    public static SwingTerminalFontConfiguration newInstance(Font... fontsInOrderOfPriority) {
        return new SwingTerminalFontConfiguration(true, fontsInOrderOfPriority);
    }

    private final List<Font> fontPriority;
    private final int fontWidth;
    private final int fontHeight;
    private final boolean useAntiAliasing;

    @SuppressWarnings("WeakerAccess")
    protected SwingTerminalFontConfiguration(boolean useAntiAliasing, Font... fontsInOrderOfPriority) {
        if(fontsInOrderOfPriority == null || fontsInOrderOfPriority.length == 0) {
            throw new IllegalArgumentException("Must pass in a valid list of fonts to SwingTerminalFontConfiguration");
        }
        this.useAntiAliasing = useAntiAliasing;
        this.fontPriority = Collections.unmodifiableList(Arrays.asList(fontsInOrderOfPriority));
        this.fontWidth = getFontWidth(fontPriority.get(0));
        this.fontHeight = getFontHeight(fontPriority.get(0));

        //Make sure all the fonts are monospace
        for(Font font: fontPriority) {
            if(!isFontMonospaced(font)) {
                throw new IllegalArgumentException("Font " + font + " isn't monospaced!");
            }
        }

        //Make sure all lower-priority fonts are less or equal in width and height
        for(Font font: fontPriority) {
            if(font == fontPriority.get(0)) {
                continue;
            }
            if(getFontWidth(font) > fontWidth) {
                throw new IllegalArgumentException("Font " + font + " is wider (" + getFontWidth(font) + " px) than the highest priority font (" + fontWidth + " px), must be smaller or equal in width");
            }
            if(getFontHeight(font) > fontHeight) {
                throw new IllegalArgumentException("Font " + font + " is taller (" + getFontHeight(font) + " px) than the highest priority font (" + fontHeight + " px), must be smaller or equal in height");
            }
        }
    }

    Font getFontForCharacter(TextCharacter character) {
        Font normalFont = getFontForCharacter(character.getCharacter());
        if(character.isBold()) {
            normalFont = normalFont.deriveFont(Font.BOLD);
        }
        return normalFont;
    }

    private Font getFontForCharacter(char c) {
        for(Font font: fontPriority) {
            if(font.canDisplay(c)) {
                return font;
            }
        }
        //No available font here, what to do...?
        return fontPriority.get(0);
    }

    boolean isUsingAntiAliasing() {
        return useAntiAliasing;
    }

    int getFontWidth() {
        return fontWidth;
    }

    int getFontHeight() {
        return fontHeight;
    }

    public SwingTerminalFontConfiguration withoutAntiAliasing() {
        return new SwingTerminalFontConfiguration(false, fontPriority.toArray(new Font[fontPriority.size()]));
    }

    private boolean isFontMonospaced(Font font) {
        FontRenderContext frc = getFontRenderContext();
        Rectangle2D iBounds = font.getStringBounds("i", frc);
        Rectangle2D mBounds = font.getStringBounds("W", frc);
        return iBounds.getWidth() == mBounds.getWidth();
    }

    private int getFontWidth(Font font) {
        return (int)font.getStringBounds("W", getFontRenderContext()).getWidth();
    }

    private int getFontHeight(Font font) {
        return (int)font.getStringBounds("W", getFontRenderContext()).getHeight();
    }

    private FontRenderContext getFontRenderContext() {
        return new FontRenderContext(
                null,
                useAntiAliasing ?
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
                RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
    }
}
