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
package com.googlecode.lanterna;

import com.googlecode.lanterna.terminal.AbstractTerminal;

import java.io.IOException;

/**
 * This is an abstract base class for terminal color definitions. Since there are different ways of specifying terminal
 * colors, all with a different range of adoptions, this makes it possible to program an API against an implementation-
 * agnostic color definition. Please remember when using colors that not all terminals and terminal emulators supports
 * them. The 24-bit color mode is very unsupported, for example, and even the default Linux terminal doesn't support
 * the 256-color indexed mode.
 *
 * @author Martin
 */
public interface TextColor {

    /**
     * Apply this color representation as the foreground color on the specified terminal
     * @param terminal Terminal to set the foreground color on
     */
    void applyAsForeground(AbstractTerminal terminal) throws IOException;

    /**
     * Apply this color representation as the background color on the specified terminal
     * @param terminal Terminal to set the background color on
     */
    void applyAsBackground(AbstractTerminal terminal) throws IOException;

    /**
     * This class represent classic ANSI colors that are likely to be very compatible with most terminal
     * implementations. It is limited to 8 colors (plus the 'default' color) but as a norm, using bold mode (SGR code)
     * will slightly alter the color, giving it a bit brighter tone, so in total this will give you 16 (+1) colors.
     * </p>
     * For more information, see http://en.wikipedia.org/wiki/File:Ansi.png
     */
    public static enum ANSI implements TextColor {
        BLACK(0),
        RED(1),
        GREEN(2),
        YELLOW(3),
        BLUE(4),
        MAGENTA(5),
        CYAN(6),
        WHITE(7),
        DEFAULT(9);

        private final int index;

        private ANSI(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public void applyAsForeground(AbstractTerminal terminal) throws IOException {
            terminal.setForegroundColor(this);
        }

        @Override
        public void applyAsBackground(AbstractTerminal terminal) throws IOException {
            terminal.setBackgroundColor(this);
        }
    }

    /**
     * This class represents a color expressed in the indexed XTerm 256 color extension, where each color is defined in a
     * lookup-table. All in all, there are 256 codes, but in order to know which one to know you either need to have the
     * table at hand, or you can use the two static helper methods which can help you convert from three 8-bit
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
    public static class Indexed implements TextColor {
        private static final byte[][] COLOR_TABLE = new byte[][] {
            //These are the standard 16-color VGA palette entries
            {(byte)0,(byte)0,(byte)0 },
            {(byte)170,(byte)0,(byte)0 },
            {(byte)0,(byte)170,(byte)0 },
            {(byte)170,(byte)85,(byte)0 },
            {(byte)0,(byte)0,(byte)170 },
            {(byte)170,(byte)0,(byte)170 },
            {(byte)0,(byte)170,(byte)170 },
            {(byte)170,(byte)170,(byte)170 },
            {(byte)85,(byte)85,(byte)85 },
            {(byte)255,(byte)85,(byte)85 },
            {(byte)85,(byte)255,(byte)85 },
            {(byte)255,(byte)255,(byte)85 },
            {(byte)85,(byte)85,(byte)255 },
            {(byte)255,(byte)85,(byte)255 },
            {(byte)85,(byte)255,(byte)255 },
            {(byte)255,(byte)255,(byte)255 },

            //Starting 6x6x6 RGB color cube from 16
            {(byte)0x00,(byte)0x00,(byte)0x00 },
            {(byte)0x00,(byte)0x00,(byte)0x5f },
            {(byte)0x00,(byte)0x00,(byte)0x87 },
            {(byte)0x00,(byte)0x00,(byte)0xaf },
            {(byte)0x00,(byte)0x00,(byte)0xd7 },
            {(byte)0x00,(byte)0x00,(byte)0xff },
            {(byte)0x00,(byte)0x5f,(byte)0x00 },
            {(byte)0x00,(byte)0x5f,(byte)0x5f },
            {(byte)0x00,(byte)0x5f,(byte)0x87 },
            {(byte)0x00,(byte)0x5f,(byte)0xaf },
            {(byte)0x00,(byte)0x5f,(byte)0xd7 },
            {(byte)0x00,(byte)0x5f,(byte)0xff },
            {(byte)0x00,(byte)0x87,(byte)0x00 },
            {(byte)0x00,(byte)0x87,(byte)0x5f },
            {(byte)0x00,(byte)0x87,(byte)0x87 },
            {(byte)0x00,(byte)0x87,(byte)0xaf },
            {(byte)0x00,(byte)0x87,(byte)0xd7 },
            {(byte)0x00,(byte)0x87,(byte)0xff },
            {(byte)0x00,(byte)0xaf,(byte)0x00 },
            {(byte)0x00,(byte)0xaf,(byte)0x5f },
            {(byte)0x00,(byte)0xaf,(byte)0x87 },
            {(byte)0x00,(byte)0xaf,(byte)0xaf },
            {(byte)0x00,(byte)0xaf,(byte)0xd7 },
            {(byte)0x00,(byte)0xaf,(byte)0xff },
            {(byte)0x00,(byte)0xd7,(byte)0x00 },
            {(byte)0x00,(byte)0xd7,(byte)0x5f },
            {(byte)0x00,(byte)0xd7,(byte)0x87 },
            {(byte)0x00,(byte)0xd7,(byte)0xaf },
            {(byte)0x00,(byte)0xd7,(byte)0xd7 },
            {(byte)0x00,(byte)0xd7,(byte)0xff },
            {(byte)0x00,(byte)0xff,(byte)0x00 },
            {(byte)0x00,(byte)0xff,(byte)0x5f },
            {(byte)0x00,(byte)0xff,(byte)0x87 },
            {(byte)0x00,(byte)0xff,(byte)0xaf },
            {(byte)0x00,(byte)0xff,(byte)0xd7 },
            {(byte)0x00,(byte)0xff,(byte)0xff },
            {(byte)0x5f,(byte)0x00,(byte)0x00 },
            {(byte)0x5f,(byte)0x00,(byte)0x5f },
            {(byte)0x5f,(byte)0x00,(byte)0x87 },
            {(byte)0x5f,(byte)0x00,(byte)0xaf },
            {(byte)0x5f,(byte)0x00,(byte)0xd7 },
            {(byte)0x5f,(byte)0x00,(byte)0xff },
            {(byte)0x5f,(byte)0x5f,(byte)0x00 },
            {(byte)0x5f,(byte)0x5f,(byte)0x5f },
            {(byte)0x5f,(byte)0x5f,(byte)0x87 },
            {(byte)0x5f,(byte)0x5f,(byte)0xaf },
            {(byte)0x5f,(byte)0x5f,(byte)0xd7 },
            {(byte)0x5f,(byte)0x5f,(byte)0xff },
            {(byte)0x5f,(byte)0x87,(byte)0x00 },
            {(byte)0x5f,(byte)0x87,(byte)0x5f },
            {(byte)0x5f,(byte)0x87,(byte)0x87 },
            {(byte)0x5f,(byte)0x87,(byte)0xaf },
            {(byte)0x5f,(byte)0x87,(byte)0xd7 },
            {(byte)0x5f,(byte)0x87,(byte)0xff },
            {(byte)0x5f,(byte)0xaf,(byte)0x00 },
            {(byte)0x5f,(byte)0xaf,(byte)0x5f },
            {(byte)0x5f,(byte)0xaf,(byte)0x87 },
            {(byte)0x5f,(byte)0xaf,(byte)0xaf },
            {(byte)0x5f,(byte)0xaf,(byte)0xd7 },
            {(byte)0x5f,(byte)0xaf,(byte)0xff },
            {(byte)0x5f,(byte)0xd7,(byte)0x00 },
            {(byte)0x5f,(byte)0xd7,(byte)0x5f },
            {(byte)0x5f,(byte)0xd7,(byte)0x87 },
            {(byte)0x5f,(byte)0xd7,(byte)0xaf },
            {(byte)0x5f,(byte)0xd7,(byte)0xd7 },
            {(byte)0x5f,(byte)0xd7,(byte)0xff },
            {(byte)0x5f,(byte)0xff,(byte)0x00 },
            {(byte)0x5f,(byte)0xff,(byte)0x5f },
            {(byte)0x5f,(byte)0xff,(byte)0x87 },
            {(byte)0x5f,(byte)0xff,(byte)0xaf },
            {(byte)0x5f,(byte)0xff,(byte)0xd7 },
            {(byte)0x5f,(byte)0xff,(byte)0xff },
            {(byte)0x87,(byte)0x00,(byte)0x00 },
            {(byte)0x87,(byte)0x00,(byte)0x5f },
            {(byte)0x87,(byte)0x00,(byte)0x87 },
            {(byte)0x87,(byte)0x00,(byte)0xaf },
            {(byte)0x87,(byte)0x00,(byte)0xd7 },
            {(byte)0x87,(byte)0x00,(byte)0xff },
            {(byte)0x87,(byte)0x5f,(byte)0x00 },
            {(byte)0x87,(byte)0x5f,(byte)0x5f },
            {(byte)0x87,(byte)0x5f,(byte)0x87 },
            {(byte)0x87,(byte)0x5f,(byte)0xaf },
            {(byte)0x87,(byte)0x5f,(byte)0xd7 },
            {(byte)0x87,(byte)0x5f,(byte)0xff },
            {(byte)0x87,(byte)0x87,(byte)0x00 },
            {(byte)0x87,(byte)0x87,(byte)0x5f },
            {(byte)0x87,(byte)0x87,(byte)0x87 },
            {(byte)0x87,(byte)0x87,(byte)0xaf },
            {(byte)0x87,(byte)0x87,(byte)0xd7 },
            {(byte)0x87,(byte)0x87,(byte)0xff },
            {(byte)0x87,(byte)0xaf,(byte)0x00 },
            {(byte)0x87,(byte)0xaf,(byte)0x5f },
            {(byte)0x87,(byte)0xaf,(byte)0x87 },
            {(byte)0x87,(byte)0xaf,(byte)0xaf },
            {(byte)0x87,(byte)0xaf,(byte)0xd7 },
            {(byte)0x87,(byte)0xaf,(byte)0xff },
            {(byte)0x87,(byte)0xd7,(byte)0x00 },
            {(byte)0x87,(byte)0xd7,(byte)0x5f },
            {(byte)0x87,(byte)0xd7,(byte)0x87 },
            {(byte)0x87,(byte)0xd7,(byte)0xaf },
            {(byte)0x87,(byte)0xd7,(byte)0xd7 },
            {(byte)0x87,(byte)0xd7,(byte)0xff },
            {(byte)0x87,(byte)0xff,(byte)0x00 },
            {(byte)0x87,(byte)0xff,(byte)0x5f },
            {(byte)0x87,(byte)0xff,(byte)0x87 },
            {(byte)0x87,(byte)0xff,(byte)0xaf },
            {(byte)0x87,(byte)0xff,(byte)0xd7 },
            {(byte)0x87,(byte)0xff,(byte)0xff },
            {(byte)0xaf,(byte)0x00,(byte)0x00 },
            {(byte)0xaf,(byte)0x00,(byte)0x5f },
            {(byte)0xaf,(byte)0x00,(byte)0x87 },
            {(byte)0xaf,(byte)0x00,(byte)0xaf },
            {(byte)0xaf,(byte)0x00,(byte)0xd7 },
            {(byte)0xaf,(byte)0x00,(byte)0xff },
            {(byte)0xaf,(byte)0x5f,(byte)0x00 },
            {(byte)0xaf,(byte)0x5f,(byte)0x5f },
            {(byte)0xaf,(byte)0x5f,(byte)0x87 },
            {(byte)0xaf,(byte)0x5f,(byte)0xaf },
            {(byte)0xaf,(byte)0x5f,(byte)0xd7 },
            {(byte)0xaf,(byte)0x5f,(byte)0xff },
            {(byte)0xaf,(byte)0x87,(byte)0x00 },
            {(byte)0xaf,(byte)0x87,(byte)0x5f },
            {(byte)0xaf,(byte)0x87,(byte)0x87 },
            {(byte)0xaf,(byte)0x87,(byte)0xaf },
            {(byte)0xaf,(byte)0x87,(byte)0xd7 },
            {(byte)0xaf,(byte)0x87,(byte)0xff },
            {(byte)0xaf,(byte)0xaf,(byte)0x00 },
            {(byte)0xaf,(byte)0xaf,(byte)0x5f },
            {(byte)0xaf,(byte)0xaf,(byte)0x87 },
            {(byte)0xaf,(byte)0xaf,(byte)0xaf },
            {(byte)0xaf,(byte)0xaf,(byte)0xd7 },
            {(byte)0xaf,(byte)0xaf,(byte)0xff },
            {(byte)0xaf,(byte)0xd7,(byte)0x00 },
            {(byte)0xaf,(byte)0xd7,(byte)0x5f },
            {(byte)0xaf,(byte)0xd7,(byte)0x87 },
            {(byte)0xaf,(byte)0xd7,(byte)0xaf },
            {(byte)0xaf,(byte)0xd7,(byte)0xd7 },
            {(byte)0xaf,(byte)0xd7,(byte)0xff },
            {(byte)0xaf,(byte)0xff,(byte)0x00 },
            {(byte)0xaf,(byte)0xff,(byte)0x5f },
            {(byte)0xaf,(byte)0xff,(byte)0x87 },
            {(byte)0xaf,(byte)0xff,(byte)0xaf },
            {(byte)0xaf,(byte)0xff,(byte)0xd7 },
            {(byte)0xaf,(byte)0xff,(byte)0xff },
            {(byte)0xd7,(byte)0x00,(byte)0x00 },
            {(byte)0xd7,(byte)0x00,(byte)0x5f },
            {(byte)0xd7,(byte)0x00,(byte)0x87 },
            {(byte)0xd7,(byte)0x00,(byte)0xaf },
            {(byte)0xd7,(byte)0x00,(byte)0xd7 },
            {(byte)0xd7,(byte)0x00,(byte)0xff },
            {(byte)0xd7,(byte)0x5f,(byte)0x00 },
            {(byte)0xd7,(byte)0x5f,(byte)0x5f },
            {(byte)0xd7,(byte)0x5f,(byte)0x87 },
            {(byte)0xd7,(byte)0x5f,(byte)0xaf },
            {(byte)0xd7,(byte)0x5f,(byte)0xd7 },
            {(byte)0xd7,(byte)0x5f,(byte)0xff },
            {(byte)0xd7,(byte)0x87,(byte)0x00 },
            {(byte)0xd7,(byte)0x87,(byte)0x5f },
            {(byte)0xd7,(byte)0x87,(byte)0x87 },
            {(byte)0xd7,(byte)0x87,(byte)0xaf },
            {(byte)0xd7,(byte)0x87,(byte)0xd7 },
            {(byte)0xd7,(byte)0x87,(byte)0xff },
            {(byte)0xd7,(byte)0xaf,(byte)0x00 },
            {(byte)0xd7,(byte)0xaf,(byte)0x5f },
            {(byte)0xd7,(byte)0xaf,(byte)0x87 },
            {(byte)0xd7,(byte)0xaf,(byte)0xaf },
            {(byte)0xd7,(byte)0xaf,(byte)0xd7 },
            {(byte)0xd7,(byte)0xaf,(byte)0xff },
            {(byte)0xd7,(byte)0xd7,(byte)0x00 },
            {(byte)0xd7,(byte)0xd7,(byte)0x5f },
            {(byte)0xd7,(byte)0xd7,(byte)0x87 },
            {(byte)0xd7,(byte)0xd7,(byte)0xaf },
            {(byte)0xd7,(byte)0xd7,(byte)0xd7 },
            {(byte)0xd7,(byte)0xd7,(byte)0xff },
            {(byte)0xd7,(byte)0xff,(byte)0x00 },
            {(byte)0xd7,(byte)0xff,(byte)0x5f },
            {(byte)0xd7,(byte)0xff,(byte)0x87 },
            {(byte)0xd7,(byte)0xff,(byte)0xaf },
            {(byte)0xd7,(byte)0xff,(byte)0xd7 },
            {(byte)0xd7,(byte)0xff,(byte)0xff },
            {(byte)0xff,(byte)0x00,(byte)0x00 },
            {(byte)0xff,(byte)0x00,(byte)0x5f },
            {(byte)0xff,(byte)0x00,(byte)0x87 },
            {(byte)0xff,(byte)0x00,(byte)0xaf },
            {(byte)0xff,(byte)0x00,(byte)0xd7 },
            {(byte)0xff,(byte)0x00,(byte)0xff },
            {(byte)0xff,(byte)0x5f,(byte)0x00 },
            {(byte)0xff,(byte)0x5f,(byte)0x5f },
            {(byte)0xff,(byte)0x5f,(byte)0x87 },
            {(byte)0xff,(byte)0x5f,(byte)0xaf },
            {(byte)0xff,(byte)0x5f,(byte)0xd7 },
            {(byte)0xff,(byte)0x5f,(byte)0xff },
            {(byte)0xff,(byte)0x87,(byte)0x00 },
            {(byte)0xff,(byte)0x87,(byte)0x5f },
            {(byte)0xff,(byte)0x87,(byte)0x87 },
            {(byte)0xff,(byte)0x87,(byte)0xaf },
            {(byte)0xff,(byte)0x87,(byte)0xd7 },
            {(byte)0xff,(byte)0x87,(byte)0xff },
            {(byte)0xff,(byte)0xaf,(byte)0x00 },
            {(byte)0xff,(byte)0xaf,(byte)0x5f },
            {(byte)0xff,(byte)0xaf,(byte)0x87 },
            {(byte)0xff,(byte)0xaf,(byte)0xaf },
            {(byte)0xff,(byte)0xaf,(byte)0xd7 },
            {(byte)0xff,(byte)0xaf,(byte)0xff },
            {(byte)0xff,(byte)0xd7,(byte)0x00 },
            {(byte)0xff,(byte)0xd7,(byte)0x5f },
            {(byte)0xff,(byte)0xd7,(byte)0x87 },
            {(byte)0xff,(byte)0xd7,(byte)0xaf },
            {(byte)0xff,(byte)0xd7,(byte)0xd7 },
            {(byte)0xff,(byte)0xd7,(byte)0xff },
            {(byte)0xff,(byte)0xff,(byte)0x00 },
            {(byte)0xff,(byte)0xff,(byte)0x5f },
            {(byte)0xff,(byte)0xff,(byte)0x87 },
            {(byte)0xff,(byte)0xff,(byte)0xaf },
            {(byte)0xff,(byte)0xff,(byte)0xd7 },
            {(byte)0xff,(byte)0xff,(byte)0xff },

            //Grey-scale ramp from 232
            {(byte)0x08,(byte)0x08,(byte)0x08 },
            {(byte)0x12,(byte)0x12,(byte)0x12 },
            {(byte)0x1c,(byte)0x1c,(byte)0x1c },
            {(byte)0x26,(byte)0x26,(byte)0x26 },
            {(byte)0x30,(byte)0x30,(byte)0x30 },
            {(byte)0x3a,(byte)0x3a,(byte)0x3a },
            {(byte)0x44,(byte)0x44,(byte)0x44 },
            {(byte)0x4e,(byte)0x4e,(byte)0x4e },
            {(byte)0x58,(byte)0x58,(byte)0x58 },
            {(byte)0x62,(byte)0x62,(byte)0x62 },
            {(byte)0x6c,(byte)0x6c,(byte)0x6c },
            {(byte)0x76,(byte)0x76,(byte)0x76 },
            {(byte)0x80,(byte)0x80,(byte)0x80 },
            {(byte)0x8a,(byte)0x8a,(byte)0x8a },
            {(byte)0x94,(byte)0x94,(byte)0x94 },
            {(byte)0x9e,(byte)0x9e,(byte)0x9e },
            {(byte)0xa8,(byte)0xa8,(byte)0xa8 },
            {(byte)0xb2,(byte)0xb2,(byte)0xb2 },
            {(byte)0xbc,(byte)0xbc,(byte)0xbc },
            {(byte)0xc6,(byte)0xc6,(byte)0xc6 },
            {(byte)0xd0,(byte)0xd0,(byte)0xd0 },
            {(byte)0xda,(byte)0xda,(byte)0xda },
            {(byte)0xe4,(byte)0xe4,(byte)0xe4 },
            {(byte)0xee,(byte)0xee,(byte)0xee }
        };

        private final int colorIndex;

        /**
         * Creates a new TextColor using the XTerm 256 color indexed mode, with the specified index value. You must
         * choose a value between 0 and 255.
         * @param colorIndex Index value to use for this color.
         */
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
        public void applyAsForeground(AbstractTerminal terminal) throws IOException {
            terminal.setForegroundColor(colorIndex);
        }

        @Override
        public void applyAsBackground(AbstractTerminal terminal) throws IOException {
            terminal.setBackgroundColor(colorIndex);
        }

        /**
         * @return Red intensity of this color, from 0 to 255
         */
        public int getRed() {
            return COLOR_TABLE[colorIndex][0] & 0x000000ff;
        }

        /**
         * @return Green intensity of this color, from 0 to 255
         */
        public int getGreen() {
            return COLOR_TABLE[colorIndex][1] & 0x000000ff;
        }

        /**
         * @return Blue intensity of this color, from 0 to 255
         */
        public int getBlue() {
            return COLOR_TABLE[colorIndex][2] & 0x000000ff;
        }

        @Override
        public String toString() {
            return "{IndexedColor:" + colorIndex + "}";
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 43 * hash + this.colorIndex;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            final Indexed other = (Indexed) obj;
            return this.colorIndex == other.colorIndex;
        }

        /**
         * Picks out a color approximated from the supplied RGB components
         * @param red Red intensity, from 0 to 255
         * @param green Red intensity, from 0 to 255
         * @param blue Red intensity, from 0 to 255
         * @return Nearest color from the 6x6x6 RGB color cube
         */
        public static Indexed fromRGB(int red, int green, int blue) {
            if(red < 0 || red > 255) {
                throw new IllegalArgumentException("fromRGB: red is outside of valid range (0-255)");
            }
            if(green < 0 || green > 255) {
                throw new IllegalArgumentException("fromRGB: green is outside of valid range (0-255)");
            }
            if(blue < 0 || blue > 255) {
                throw new IllegalArgumentException("fromRGB: blue is outside of valid range (0-255)");
            }

            int rescaledRed = (int)(((double)red / 255.0) * 6.0);
            int rescaledGreen = (int)(((double)green / 255.0) * 6.0);
            int rescaledBlue = (int)(((double)blue / 255.0) * 6.0);

            int index = rescaledBlue + (6 * rescaledGreen) + (36 * rescaledRed) + 16;
            return new Indexed(index);
        }

        /**
         * Picks out a color from the grey-scale ramp area of the color index.
         * @param intensity Intensity, 0 - 255
         * @return Indexed color from the grey-scale ramp which is the best match for the supplied intensity
         */
        public static Indexed fromGreyRamp(int intensity) {
            int rescaled = (int)(((double)intensity / 255.0) * 24.0) + 232;
            return new Indexed(rescaled);
        }
    }

    /**
     * This class can be used to specify a color in 24-bit color space (RGB with 8-bit resolution per color). Please be
     * aware that only a few terminal support 24-bit color control codes, please avoid using this class unless you know
     * all users will have compatible terminals. For details, please see
     * <a href="https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors">
     * this</a> commit log. Behavior on terminals that don't support these codes is undefined.
     */
    public static class RGB implements TextColor {
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
                throw new IllegalArgumentException("RGB: r is outside of valid range (0-255)");
            }
            if(g < 0 || g > 255) {
                throw new IllegalArgumentException("RGB: g is outside of valid range (0-255)");
            }
            if(b < 0 || b > 255) {
                throw new IllegalArgumentException("RGB: b is outside of valid range (0-255)");
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
        public void applyAsForeground(AbstractTerminal terminal) throws IOException {
            terminal.setForegroundColor(r, g, b);
        }

        @Override
        public void applyAsBackground(AbstractTerminal terminal) throws IOException {
            terminal.setBackgroundColor(r, g, b);
        }

        @Override
        public String toString() {
            return "{RGB:" + r + "," + g + "," + b + "}";
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + this.r;
            hash = 29 * hash + this.g;
            hash = 29 * hash + this.b;
            return hash;
        }

        @SuppressWarnings("SimplifiableIfStatement")
        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            final RGB other = (RGB) obj;
            if(this.r != other.r) {
                return false;
            }
            if(this.g != other.g) {
                return false;
            }
            return this.b == other.b;
        }
    }
}
