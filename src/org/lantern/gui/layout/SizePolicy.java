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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui.layout;

/**
 *
 * @author mabe02
 */
public class SizePolicy {
    
    public static final int CONSTANT_ID = 1;
    public static final int GROWING_ID = 2;
    public static final int MAXIMUM_ID = 3;
    
    public static final SizePolicy CONSTANT = new SizePolicy(CONSTANT_ID);
    public static final SizePolicy GROWING = new SizePolicy(GROWING_ID);
    public static final SizePolicy MAXIMUM = new SizePolicy(MAXIMUM_ID);
    
    private final int index;

    private SizePolicy(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
