/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */

package com.googlecode.lanterna;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * This program will print all ACS symbols to standard out, it's a good test
 * to see if your terminal emulator supports these UTF-8 characters or not.
 * @author Martin
 */
public class TestACS
{
    private static final Set<String> NEW_LINE_AFTER = new HashSet<String>() {{
        add("MALE");
        add("ARROW_LEFT");
        add("BLOCK_SPARSE");
        add("DOUBLE_LINE_VERTICAL");
        add("DOUBLE_LINE_TOP_RIGHT_CORNER");
        add("DOUBLE_LINE_BOTTOM_RIGHT_CORNER");
        add("DOUBLE_LINE_CROSS");
        add("SINGLE_LINE_T_LEFT");
        add("SINGLE_LINE_T_DOUBLE_LEFT");
        add("DOUBLE_LINE_T_LEFT");
        add("DOUBLE_LINE_T_SINGLE_LEFT");
    }};

    public static void main(String[] args)
    {
        for(Field field : Symbols.class.getFields()) {
            field.setAccessible(true);
            try {
                System.out.printf("%1$s = %2$s%n%3$s", field.getName(), field.get(null),
                        NEW_LINE_AFTER.contains(field.getName()) ? System.lineSeparator() : "");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
