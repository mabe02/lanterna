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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 *
 * @author martin
 */
public class Environment
{
    public static void main(String[] args)
    {
        Properties properties = System.getProperties();
        ArrayList<String> keys = new ArrayList(properties.keySet());
        Collections.sort(keys);
        for(String key: keys)
            System.out.println(key + " = " + properties.getProperty(key));

        keys = new ArrayList<String>(System.getenv().keySet());
        Collections.sort(keys);
        for(String key: keys)
            System.out.println(key + " = " + System.getenv(key));
    }
}
