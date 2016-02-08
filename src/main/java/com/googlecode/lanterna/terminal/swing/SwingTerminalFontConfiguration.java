package com.googlecode.lanterna.terminal.swing;

import java.awt.*;

/**
 * Created by martin on 08/02/16.
 */
public class SwingTerminalFontConfiguration extends AWTTerminalFontConfiguration {
    /**
     * This is the default font settings that will be used if you don't specify anything
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

    public SwingTerminalFontConfiguration(boolean useAntiAliasing, BoldMode boldMode, Font... fontsInOrderOfPriority) {
        super(useAntiAliasing, boldMode, fontsInOrderOfPriority);
    }
}
