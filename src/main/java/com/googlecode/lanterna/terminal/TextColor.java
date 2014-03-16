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
package com.googlecode.lanterna.terminal;

/**
 * This is an abstract base class for terminal color definitions. Since there are different ways of specifying terminal
 * colors, all with a different range of adoptions, this makes it possible to program an API against an implementation-
 * agnostic color definition.
 * @author Martin
 */
public abstract class TextColor {

    protected TextColor() {}

    /**
     * Apply this color representation as the foreground color on the specified terminal
     * @param terminal Terminal to set the foreground color on
     */
    public abstract void applyAsForeground(Terminal terminal);

    /**
     * Apply this color representation as the background color on the specified terminal
     * @param terminal Terminal to set the background color on
     */
    public abstract void applyAsBackground(Terminal terminal);

    /**
     * This class represent classic ANSI colors that are likely to be very compatible with most terminal
     * implementations. It is limited to 8 colors (plus the 'default' color) but as a norm, using bold mode (SGR code)
     * will slightly alter the color, giving it a bit brighter tone, so in total this will give you 16 (+1) colors.
     */
    public static class ANSI extends TextColor {
        public static final ANSI BLACK = new ANSI(Terminal.ANSIColor.BLACK);
        public static final ANSI RED = new ANSI(Terminal.ANSIColor.RED);
        public static final ANSI GREEN = new ANSI(Terminal.ANSIColor.GREEN);
        public static final ANSI YELLOW = new ANSI(Terminal.ANSIColor.YELLOW);
        public static final ANSI BLUE = new ANSI(Terminal.ANSIColor.BLUE);
        public static final ANSI MAGENTA = new ANSI(Terminal.ANSIColor.MAGENTA);
        public static final ANSI CYAN = new ANSI(Terminal.ANSIColor.CYAN);
        public static final ANSI WHITE = new ANSI(Terminal.ANSIColor.WHITE);
        public static final ANSI DEFAULT = new ANSI(Terminal.ANSIColor.DEFAULT);

        private final Terminal.ANSIColor ansiColor;

        private ANSI(Terminal.ANSIColor ansiColor) {
            this.ansiColor = ansiColor;
        }

        @Override
        public void applyAsForeground(Terminal terminal) {
            terminal.applyForegroundColor(ansiColor);
        }

        @Override
        public void applyAsBackground(Terminal terminal) {
            terminal.applyBackgroundColor(ansiColor);
        }
    }

    /**
     * This class represents a color expressed in the indexed XTerm 256 color extension, where each color is defined in a
     * lookup-table. All in all, there are 256 codes, but in order to know which one to know you either need to have the
     * table at hand, or you can use the XTerm8bitIndexedColorUtils class which can help you convert from three 8-bit
     * RGB values to the closest approximate indexed color number. If you are interested, the 256 index values are
     * actually divided like this:</br>
     * 0 .. 15 - System colors, same as ANSI, but the actual rendered color depends on the terminal emulators color scheme<br>
     * 16 .. 231 - Forms a 6x6x6 RGB color cube<br>
     * 232 .. 255 - A gray scale ramp (without black and white endpoints)<br>
     *</p>
     * Support for indexed colors is somewhat widely adopted, not as much as the ANSI colors (TextColor.ANSI) but more
     * than the RGB (TextColor.RGB).
     * </p>
     * For more details on this, please see <a
     * href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit message to Konsole.
     */
    public static class Indexed extends TextColor {
        private final int colorIndex;

        public Indexed(int colorIndex) {
            if(colorIndex > 255 || colorIndex < 0) {
                throw new IllegalArgumentException("Cannot create a Color.Indexed with a color index of " + colorIndex +
                        ", must be in the range of 0-255");
            }
            this.colorIndex = colorIndex;
        }

        /**
         * Retrieves the exact index of this color. See the class documentation for more information of what this index
         * represents.
         * @return Color index
         */
        public int getColorIndex() {
            return colorIndex;
        }

        @Override
        public void applyAsForeground(Terminal terminal) {
            terminal.applyForegroundColor(colorIndex);
        }

        @Override
        public void applyAsBackground(Terminal terminal) {
            terminal.applyBackgroundColor(colorIndex);
        }
    }

    /**
     * This class can be used to specify a color in 24-bit color space (RGB with 8-bit resolution per color). Please be
     * aware that only a few terminal support 24-bit color control codes, please avoid using this class unless you know
     * all users will have compatible terminals. For details, please see
     * <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit log. Behavior on terminals that don't support these codes is undefined.
     */
    public static class RGB extends TextColor {
        private final int r;
        private final int g;
        private final int b;

        /**
         * This class can be used to specify a color in 24-bit color space (RGB with 8-bit resolution per color). Please be
         * aware that only a few terminal support 24-bit color control codes, please avoid using this class unless you know
         * all users will have compatible terminals. For details, please see
         * <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
         * this</a> commit log. Behavior on terminals that don't support these codes is undefined.
         *
         * @param r Red intensity, from 0 to 255
         * @param g Green intensity, from 0 to 255
         * @param b Blue intensity, from 0 to 255
         */
        public RGB(int r, int g, int b) {
            if(r < 0 || r > 255) {
                throw new IllegalArgumentException("applyForegroundColor: r is outside of valid range (0-255)");
            }
            if(g < 0 || g > 255) {
                throw new IllegalArgumentException("applyForegroundColor: g is outside of valid range (0-255)");
            }
            if(b < 0 || b > 255) {
                throw new IllegalArgumentException("applyForegroundColor: b is outside of valid range (0-255)");
            }
            this.r = r;
            this.g = g;
            this.b = b;
        }

        /**
         * @return Red intensity of this color, from 0 to 255
         */
        public int getRed() {
            return r;
        }

        /**
         * @return Green intensity of this color, from 0 to 255
         */
        public int getGreen() {
            return g;
        }

        /**
         * @return Blue intensity of this color, from 0 to 255
         */
        public int getBlue() {
            return b;
        }

        @Override
        public void applyAsForeground(Terminal terminal) {
            terminal.applyForegroundColor(r, g, b);
        }

        @Override
        public void applyAsBackground(Terminal terminal) {
            terminal.applyBackgroundColor(r, g, b);
        }
    }
}
