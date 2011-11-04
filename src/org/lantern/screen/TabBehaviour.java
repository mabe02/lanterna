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

package org.lantern.screen;

/**
 *
 * @author martin
 */
public class TabBehaviour {
    
    public static final int CONVERT_TO_ONE_SPACE_ID = 1;
    public static final int CONVERT_TO_FOUR_SPACES_ID = 2;
    public static final int CONVERT_TO_EIGHT_SPACES_ID = 3;
    public static final int ALIGN_TO_COLUMN_4_ID = 4;
    public static final int ALIGN_TO_COLUMN_8_ID = 5;
    
    public static final TabBehaviour CONVERT_TO_ONE_SPACE = new TabBehaviour(CONVERT_TO_ONE_SPACE_ID);
    public static final TabBehaviour CONVERT_TO_FOUR_SPACES = new TabBehaviour(CONVERT_TO_FOUR_SPACES_ID);
    public static final TabBehaviour CONVERT_TO_EIGHT_SPACES = new TabBehaviour(CONVERT_TO_EIGHT_SPACES_ID);
    public static final TabBehaviour ALIGN_TO_COLUMN_4 = new TabBehaviour(ALIGN_TO_COLUMN_4_ID);
    public static final TabBehaviour ALIGN_TO_COLUMN_8 = new TabBehaviour(ALIGN_TO_COLUMN_8_ID);

    private final int index;

    private TabBehaviour(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
