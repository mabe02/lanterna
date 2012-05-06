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
 * Some text graphics, taken from http://en.wikipedia.org/wiki/Codepage_437
 * but converted to its UTF-8 counterpart.
 * @author martin
 */
public class ACS {
    private ACS() {}
    
    public static final char ULCORNER = 0x250C;
    public static final char URCORNER = 0x2510;
    public static final char LLCORNER = 0x2514;
    public static final char LRCORNER = 0x2518;
    public static final char HLINE = 0x2500;
    public static final char VLINE = 0x2502;
    public static final char FACE_WHITE = 0x263A;
    public static final char FACE_BLACK = 0x263B;
    public static final char HEART = 0x2665;
    public static final char CLUB = 0x2663;
    public static final char DIAMOND = 0x2666;
    public static final char SPADES = 0x2660;
    public static final char DOT = 0x2022;
    
    public static final char ARROW_UP = 0x2191;
    public static final char ARROW_DOWN = 0x2193;
    public static final char ARROW_RIGHT = 0x2192;
    public static final char ARROW_LEFT = 0x2190;
    public static final char BLOCK_SOLID = 0x2588;
    public static final char BLOCK_DENSE = 0x2593;
    public static final char BLOCK_MIDDLE = 0x2592;
    public static final char BLOCK_SPARSE = 0x2591;

    public static final char SINGLE_LINE_HORIZONTAL = HLINE;
    public static final char DOUBLE_LINE_HORIZONTAL = 0x2550;
    public static final char SINGLE_LINE_VERTICAL = VLINE;
    public static final char DOUBLE_LINE_VERTICAL = 0x2551;

    public static final char SINGLE_LINE_UP_LEFT_CORNER = ULCORNER;
    public static final char DOUBLE_LINE_UP_LEFT_CORNER = 0x2554;
    public static final char SINGLE_LINE_UP_RIGHT_CORNER = URCORNER;
    public static final char DOUBLE_LINE_UP_RIGHT_CORNER = 0x2557;

    public static final char SINGLE_LINE_LOW_LEFT_CORNER = LLCORNER;
    public static final char DOUBLE_LINE_LOW_LEFT_CORNER = 0x255A;
    public static final char SINGLE_LINE_LOW_RIGHT_CORNER = LRCORNER;
    public static final char DOUBLE_LINE_LOW_RIGHT_CORNER = 0x255D;

    public static final char SINGLE_LINE_CROSS = 0x253C;
    public static final char DOUBLE_LINE_CROSS = 0x256C;

    public static final char SINGLE_LINE_T_UP = 0x2534;
    public static final char SINGLE_LINE_T_DOWN = 0x252C;
    public static final char SINGLE_LINE_T_RIGHT = 0x251c;
    public static final char SINGLE_LINE_T_LEFT = 0x2524;

    public static final char SINGLE_LINE_T_DOUBLE_UP = 0x256B;
    public static final char SINGLE_LINE_T_DOUBLE_DOWN = 0x2565;
    public static final char SINGLE_LINE_T_DOUBLE_RIGHT = 0x255E;
    public static final char SINGLE_LINE_T_DOUBLE_LEFT = 0x2561;

    public static final char DOUBLE_LINE_T_UP = 0x2569;
    public static final char DOUBLE_LINE_T_DOWN = 0x2566;
    public static final char DOUBLE_LINE_T_RIGHT = 0x2560;
    public static final char DOUBLE_LINE_T_LEFT = 0x2563;

    public static final char DOUBLE_LINE_T_SINGLE_UP = 0x2567;
    public static final char DOUBLE_LINE_T_SINGLE_DOWN = 0x2564;
    public static final char DOUBLE_LINE_T_SINGLE_RIGHT = 0x255F;
    public static final char DOUBLE_LINE_T_SINGLE_LEFT = 0x2562;
}
