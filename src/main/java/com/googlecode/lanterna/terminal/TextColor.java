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
package com.googlecode.lanterna.terminal;

/**
 *
 * @author Martin
 */
public abstract class TextColor {
    
    protected TextColor() {}
    
    public abstract void applyAsForeground(Terminal terminal);
    public abstract void applyAsBackground(Terminal terminal);
    
    public static TextColor fromOldFormat(Terminal.Color color) {
        switch(color) {
            case BLACK:
                return ANSI.BLACK;
            case RED:
                return ANSI.RED;
            case GREEN:
                return ANSI.GREEN;
            case YELLOW:
                return ANSI.YELLOW;
            case BLUE:
                return ANSI.BLUE;
            case MAGENTA:
                return ANSI.MAGENTA;
            case CYAN:
                return ANSI.CYAN;
            case WHITE:
                return ANSI.WHITE;
            case DEFAULT:
                return ANSI.DEFAULT;
            default:
                return null;
        }
    }
    
    public static class ANSI extends TextColor {
        
        public static final ANSI BLACK = new ANSI(Terminal.Color.BLACK);
        public static final ANSI RED = new ANSI(Terminal.Color.RED);
        public static final ANSI GREEN = new ANSI(Terminal.Color.GREEN);
        public static final ANSI YELLOW = new ANSI(Terminal.Color.YELLOW);
        public static final ANSI BLUE = new ANSI(Terminal.Color.BLUE);
        public static final ANSI MAGENTA = new ANSI(Terminal.Color.MAGENTA);
        public static final ANSI CYAN = new ANSI(Terminal.Color.CYAN);
        public static final ANSI WHITE = new ANSI(Terminal.Color.WHITE);
        public static final ANSI DEFAULT = new ANSI(Terminal.Color.DEFAULT);
        
        private final Terminal.Color ansiColor;
        
        private ANSI(Terminal.Color ansiColor) {
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
    
    public static class Indexed extends TextColor {
        private final int colorIndex;

        public Indexed(int colorIndex) {
            if(colorIndex > 255 || colorIndex < 0) {
                throw new IllegalArgumentException("Cannot create a Color.Indexed with a color index of " + colorIndex + 
                        ", must be in the range of 0-255");
            }
            this.colorIndex = colorIndex;
        }

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
    
    public static class RGB extends TextColor {
        private final int r;
        private final int g;
        private final int b;

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

        public int getRed() {
            return r;
        }

        public int getGreen() {
            return g;
        }

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
