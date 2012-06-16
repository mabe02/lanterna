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

/**
 * Prints the whole symbol table, this is debug stuff for UTF-8 to non-UTF-8
 * symbol character conversions...
 * @author Martin
 */
public class TestAllCodes {
    public static void main(String[] args) throws Exception
    {
        System.out.write(new byte[] { (byte)0x1B, 0x28, 0x30 });
        for(int i = 0; i < 200; i++) {
            System.out.write((i + " = " + ((char)i) + "\n").getBytes());
        }
        System.out.write(new byte[] { (byte)0x1B, 0x28, 0x42 });
        //System.out.write(new byte[] { (byte)0x1B, (byte)0x21, (byte)0x40, 15 });
    }
}
