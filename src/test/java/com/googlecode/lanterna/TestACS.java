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
 * Copyright (C) 2010-2017 Martin Berglund
 */

package com.googlecode.lanterna;

import java.lang.reflect.Field;

/**
 * This program will print all ACS symbols to standard out, it's a good test
 * to see if your terminal emulator supports these UTF-8 characters or not.
 * @author Martin
 */
public class TestACS
{
    public static void main(String[] args)
    {
        for(Field field : Symbols.class.getFields()) {
            field.setAccessible(true);
            try {
                System.out.printf("%1$s = %2$s%n", field.getName(), field.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
