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

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextCharacter;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class encapsulates the font information used by an {@link AWTTerminal}. By customizing this class, you can
 * choose which fonts are going to be used by an {@link AWTTerminal} component and some other related settings.
 * @author martin
 */
public class AWTTerminalFontConfiguration {

    /**
     * Controls how the SGR bold will take effect when enabled on a character. Mainly this is controlling if the 
     * character should be rendered with a bold font or not. The reason for this is that some characters, notably the
     * lines and double-lines in defined in Symbol, usually doesn't look very good with bold font when you try to 
     * construct a GUI. 
     */
    public enum BoldMode {
        /**
         * All characters with SGR Bold enabled will be rendered using a bold font
         */
        EVERYTHING,
        /**
         * All characters with SGR Bold enabled, except for the characters defined as constants in Symbols class, will 
         * be rendered using a bold font
         */
        EVERYTHING_BUT_SYMBOLS,
        /**
         * Bold font will not be used for characters with SGR bold enabled
         */
        NOTHING,
        ;
    }

    private static final Set<String> MONOSPACE_CHECK_OVERRIDE = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "VL Gothic Regular",
            "NanumGothic",
            "WenQuanYi Zen Hei Mono",
            "WenQuanYi Zen Hei",
            "AR PL UMing TW",
            "AR PL UMing HK",
            "AR PL UMing CN"
    )));

    private static List<Font> getDefaultWindowsFonts() {
        int fontSize = getFontSize();
        return Collections.unmodifiableList(Arrays.asList(
                new Font("Consolas", Font.PLAIN, fontSize), //Monospaced can look pretty bad on Windows, so let's override it
                new Font("Courier New", Font.PLAIN, fontSize),
                new Font("Monospaced", Font.PLAIN, fontSize)));
    }

    private static List<Font> getDefaultLinuxFonts() {
        int fontSize = getFontSize();
        return Collections.unmodifiableList(Arrays.asList(
                new Font("DejaVu Sans Mono", Font.PLAIN, fontSize),
                new Font("Monospaced", Font.PLAIN, fontSize),
                //Below, these should be redundant (Monospaced is supposed to catch-all)
                // but Java 6 seems to have issues with finding monospaced fonts sometimes
                new Font("Ubuntu Mono", Font.PLAIN, fontSize),
                new Font("FreeMono", Font.PLAIN, fontSize),
                new Font("Liberation Mono", Font.PLAIN, fontSize),
                new Font("VL Gothic Regular", Font.PLAIN, fontSize),
                new Font("NanumGothic", Font.PLAIN, fontSize),
                new Font("WenQuanYi Zen Hei Mono", Font.PLAIN, fontSize),
                new Font("WenQuanYi Zen Hei", Font.PLAIN, fontSize),
                new Font("AR PL UMing TW", Font.PLAIN, fontSize),
                new Font("AR PL UMing HK", Font.PLAIN, fontSize),
                new Font("AR PL UMing CN", Font.PLAIN, fontSize)));
    }

    private static List<Font> getDefaultFonts() {
        int fontSize = getFontSize();
        return Collections.unmodifiableList(Collections.singletonList(
                new Font("Monospaced", Font.PLAIN, fontSize)));
    }

    // Here we check the screen resolution on the primary monitor and make a guess at if it's high-DPI or not
    private static int getFontSize() {
        int baseFontSize = 14;
        String[] javaVersion = System.getProperty("java.version", "1").split("\\.");
        if (System.getProperty("os.name", "").startsWith("Windows") && Integer.parseInt(javaVersion[0]) >= 9) {
            // Java 9+ reports itself as HiDPI-unaware on Windows and will be scaled by the OS
            // Keep in mind that Java 8 and earlier reports itself as version 1.X.0_YYY
            return baseFontSize;
        }
        else {
            return getHPIAdjustedFontSize(baseFontSize);
        }
    }

    private static int getHPIAdjustedFontSize(int baseFontSize) {
        if (Toolkit.getDefaultToolkit().getScreenResolution() >= 110) {
            // Rely on DPI if it is a high value.
            return Toolkit.getDefaultToolkit().getScreenResolution() / (baseFontSize/2) + 1;
        } else {
            // Otherwise try to guess it from the monitor size:
            // If the width is wider than Full HD (1080p, or 1920x1080), then assume it's high-DPI.
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (ge.getMaximumWindowBounds().getWidth() > 4096) {
                return baseFontSize * 4;
            } else if (ge.getMaximumWindowBounds().getWidth() > 2048) {
                return baseFontSize * 2;
            } else {
                return baseFontSize;
            }
        }
    }

    /**
     * Returns the default font to use depending on the platform
     * @return Default font to use, system-dependent
     */
    protected static Font[] selectDefaultFont() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        if(osName.contains("win")) {
            List<Font> windowsFonts = getDefaultWindowsFonts();
            return windowsFonts.toArray(new Font[windowsFonts.size()]);
        }
        else if(osName.contains("linux")) {
            List<Font> linuxFonts = getDefaultLinuxFonts();
            return linuxFonts.toArray(new Font[linuxFonts.size()]);
        }
        else {
            List<Font> defaultFonts = getDefaultFonts();
            return defaultFonts.toArray(new Font[defaultFonts.size()]);
        }
    }

    /**
     * This is the default font settings that will be used if you don't specify anything
     * @return An {@link AWTTerminal} font configuration object with default values set up
     */
    public static AWTTerminalFontConfiguration getDefault() {
        return newInstance(filterMonospaced(selectDefaultFont()));
    }

    /**
     * Given an array of fonts, returns another array with only the ones that are monospaced. The fonts in the result
     * will have the same order as in which they came in. A font is considered monospaced if the width of 'i' and 'W' is
     * the same.
     * @param fonts Fonts to filter monospaced fonts from
     * @return Array with the fonts from the input parameter that were monospaced
     */
    public static Font[] filterMonospaced(Font... fonts) {
        List<Font> result = new ArrayList<Font>(fonts.length);
        for(Font font: fonts) {
            if (isFontMonospaced(font)) {
                result.add(font);
            }
        }
        return result.toArray(new Font[result.size()]);
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
    public static AWTTerminalFontConfiguration newInstance(Font... fontsInOrderOfPriority) {
        return new AWTTerminalFontConfiguration(true, BoldMode.EVERYTHING_BUT_SYMBOLS, fontsInOrderOfPriority);
    }

    private final List<Font> fontPriority;
    private final int fontWidth;
    private final int fontHeight;
    private final boolean useAntiAliasing;
    private final BoldMode boldMode;

    @SuppressWarnings("WeakerAccess")
    protected AWTTerminalFontConfiguration(boolean useAntiAliasing, BoldMode boldMode, Font... fontsInOrderOfPriority) {
        if(fontsInOrderOfPriority == null || fontsInOrderOfPriority.length == 0) {
            throw new IllegalArgumentException("Must pass in a valid list of fonts to SwingTerminalFontConfiguration");
        }
        this.useAntiAliasing = useAntiAliasing;
        this.boldMode = boldMode;
        this.fontPriority = new ArrayList<Font>(Arrays.asList(fontsInOrderOfPriority));
        this.fontWidth = getFontWidth(fontPriority.get(0));
        this.fontHeight = getFontHeight(fontPriority.get(0));

        //Make sure all the fonts are monospace
        for(Font font: fontPriority) {
            if(!isFontMonospaced(font)) {
                throw new IllegalArgumentException("Font " + font + " isn't monospaced!");
            }
        }

        //Make sure all lower-priority fonts are less or equal in width and height, shrink if necessary
        for(int i = 1; i < fontPriority.size(); i++) {
            Font font = fontPriority.get(i);
            while(getFontWidth(font) > fontWidth || getFontHeight(font) > fontHeight) {
                float newSize = font.getSize2D() - 0.5f;
                if(newSize < 0.01) {
                    throw new IllegalStateException("Unable to shrink font " + (i+1) + " to fit the size of highest priority font " + fontPriority.get(0));
                }
                font = font.deriveFont(newSize);
                fontPriority.set(i, font);
            }
        }
    }

    /**
     * Given a certain character, return the font to use for drawing it. The method will go through all fonts passed in
     * to this {@link AWTTerminalFontConfiguration} in the order of priority specified and chose the first font which is
     * capable of drawing {@code character}. If no such font is found, the normal fonts is returned (and probably won't
     * be able to draw the character).
     * @param character Character to find a font for
     * @return Font which the {@code character} should be drawn using
     */
    Font getFontForCharacter(TextCharacter character) {
        Font normalFont = getFontForCharacter(character.getCharacter());
        if(boldMode == BoldMode.EVERYTHING || (boldMode == BoldMode.EVERYTHING_BUT_SYMBOLS && isNotASymbol(character.getCharacter()))) {
            if(character.isBold()) {
                normalFont = normalFont.deriveFont(Font.BOLD);
            }
        }
        if (character.isItalic() ) {
            normalFont = normalFont.deriveFont(Font.ITALIC);
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

    /**
     * Returns the horizontal size in pixels of the fonts configured
     * @return Horizontal size in pixels of the fonts configured
     */
    int getFontWidth() {
        return fontWidth;
    }

    /**
     * Returns the vertical size in pixels of the fonts configured
     * @return Vertical size in pixels of the fonts configured
     */
    int getFontHeight() {
        return fontHeight;
    }

    /**
     * Returns {@code true} if anti-aliasing has been enabled, {@code false} otherwise
     * @return {@code true} if anti-aliasing has been enabled, {@code false} otherwise
     */
    boolean isAntiAliased() {
        return useAntiAliasing;
    }

    private static boolean isFontMonospaced(Font font) {
        if(MONOSPACE_CHECK_OVERRIDE.contains(font.getName())) {
            return true;
        }
        FontRenderContext frc = new FontRenderContext(
                null,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
                RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
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

    
    private static final Set<Character> SYMBOLS_CACHE = new HashSet<Character>();
    static {
        for(Field field: Symbols.class.getFields()) {
            if(field.getType() == char.class &&
                    (field.getModifiers() & Modifier.FINAL) != 0 &&
                    (field.getModifiers() & Modifier.STATIC) != 0) {
                try {
                    SYMBOLS_CACHE.add(field.getChar(null));
                }
                catch(IllegalArgumentException ignore) {
                    //Should never happen!
                }
                catch(IllegalAccessException ignore) {
                    //Should never happen!
                }
            }
        }
    }
    
    private boolean isNotASymbol(char character) {
        return !SYMBOLS_CACHE.contains(character);
    }
}
