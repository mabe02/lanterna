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

package com.googlecode.lanterna.test;

import com.googlecode.lanterna.terminal.ACS;

/**
 * This program will print all ACS symbols to standard out, it's a good test
 * to see if your terminal emulator supports these UTF-8 characters or not.
 * @author Martin
 */
public class TestACS
{
    public static void main(String[] args)
    {        
        System.out.println("FACE_WHITE = " + ACS.FACE_WHITE);
        System.out.println("FACE_BLACK = " + ACS.FACE_BLACK);
        System.out.println("HEART = " + ACS.HEART);
        System.out.println("CLUB = " + ACS.CLUB);
        System.out.println("DIAMOND = " + ACS.DIAMOND);
        System.out.println("SPADES = " + ACS.SPADES);
        System.out.println("DOT = " + ACS.DOT);
        System.out.println();
        System.out.println("ARROW_UP = " + ACS.ARROW_UP);
        System.out.println("ARROW_DOWN = " + ACS.ARROW_DOWN);
        System.out.println("ARROW_RIGHT = " + ACS.ARROW_RIGHT);
        System.out.println("ARROW_LEFT = " + ACS.ARROW_LEFT);
        System.out.println();
        System.out.println("BLOCK_SOLID = " + ACS.BLOCK_SOLID);
        System.out.println("BLOCK_DENSE = " + ACS.BLOCK_DENSE);
        System.out.println("BLOCK_MIDDLE = " + ACS.BLOCK_MIDDLE);
        System.out.println("BLOCK_SPARSE = " + ACS.BLOCK_SPARSE);
        System.out.println();
        System.out.println("SINGLE_LINE_HORIZONTAL = " + ACS.SINGLE_LINE_HORIZONTAL);
        System.out.println("DOUBLE_LINE_HORIZONTAL = " + ACS.DOUBLE_LINE_HORIZONTAL);
        System.out.println("SINGLE_LINE_VERTICAL = " + ACS.SINGLE_LINE_VERTICAL);
        System.out.println("DOUBLE_LINE_VERTICAL = " + ACS.DOUBLE_LINE_VERTICAL);
        System.out.println();
        System.out.println("SINGLE_LINE_UP_LEFT_CORNER = " + ACS.SINGLE_LINE_UP_LEFT_CORNER);
        System.out.println("DOUBLE_LINE_UP_LEFT_CORNER = " + ACS.DOUBLE_LINE_UP_LEFT_CORNER);
        System.out.println("SINGLE_LINE_UP_RIGHT_CORNER = " + ACS.SINGLE_LINE_UP_RIGHT_CORNER);
        System.out.println("DOUBLE_LINE_UP_RIGHT_CORNER = " + ACS.DOUBLE_LINE_UP_RIGHT_CORNER);
        System.out.println();
        System.out.println("SINGLE_LINE_LOW_LEFT_CORNER = " + ACS.SINGLE_LINE_LOW_LEFT_CORNER);
        System.out.println("DOUBLE_LINE_LOW_LEFT_CORNER = " + ACS.DOUBLE_LINE_LOW_LEFT_CORNER);
        System.out.println("SINGLE_LINE_LOW_RIGHT_CORNER = " + ACS.SINGLE_LINE_LOW_RIGHT_CORNER);
        System.out.println("DOUBLE_LINE_LOW_RIGHT_CORNER = " + ACS.DOUBLE_LINE_LOW_RIGHT_CORNER);
        System.out.println();
        System.out.println("SINGLE_LINE_CROSS = " + ACS.SINGLE_LINE_CROSS);
        System.out.println("DOUBLE_LINE_CROSS = " + ACS.DOUBLE_LINE_CROSS);
        System.out.println();
        System.out.println("SINGLE_LINE_T_UP = " + ACS.SINGLE_LINE_T_UP);
        System.out.println("SINGLE_LINE_T_DOWN = " + ACS.SINGLE_LINE_T_DOWN);
        System.out.println("SINGLE_LINE_T_RIGHT = " + ACS.SINGLE_LINE_T_RIGHT);
        System.out.println("SINGLE_LINE_T_LEFT = " + ACS.SINGLE_LINE_T_LEFT);
        System.out.println();
        System.out.println("SINGLE_LINE_T_DOUBLE_UP = " + ACS.SINGLE_LINE_T_DOUBLE_UP);
        System.out.println("SINGLE_LINE_T_DOUBLE_DOWN = " + ACS.SINGLE_LINE_T_DOUBLE_DOWN);
        System.out.println("SINGLE_LINE_T_DOUBLE_RIGHT = " + ACS.SINGLE_LINE_T_DOUBLE_RIGHT);
        System.out.println("SINGLE_LINE_T_DOUBLE_LEFT = " + ACS.SINGLE_LINE_T_DOUBLE_LEFT);
        System.out.println();
        System.out.println("DOUBLE_LINE_T_UP = " + ACS.DOUBLE_LINE_T_UP);
        System.out.println("DOUBLE_LINE_T_DOWN = " + ACS.DOUBLE_LINE_T_DOWN);
        System.out.println("DOUBLE_LINE_T_RIGHT = " + ACS.DOUBLE_LINE_T_RIGHT);
        System.out.println("DOUBLE_LINE_T_LEFT = " + ACS.DOUBLE_LINE_T_LEFT);
        System.out.println();
        System.out.println("DOUBLE_LINE_T_SINGLE_UP = " + ACS.DOUBLE_LINE_T_SINGLE_UP);
        System.out.println("DOUBLE_LINE_T_SINGLE_DOWN = " + ACS.DOUBLE_LINE_T_SINGLE_DOWN);
        System.out.println("DOUBLE_LINE_T_SINGLE_RIGHT = " + ACS.DOUBLE_LINE_T_SINGLE_RIGHT);
        System.out.println("DOUBLE_LINE_T_SINGLE_LEFT = " + ACS.DOUBLE_LINE_T_SINGLE_LEFT);
    }
}
