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

package com.googlecode.lanterna.screen;

/**
 * What to do about the tab character when putting on a {@code Screen}
 * @author martin
 */
public enum TabBehaviour {
    CONVERT_TO_ONE_SPACE,
    CONVERT_TO_FOUR_SPACES,
    CONVERT_TO_EIGHT_SPACES,
    ALIGN_TO_COLUMN_4,
    ALIGN_TO_COLUMN_8,
    ;

    public String replaceTabs(String string, int x) {
        int tabPosition = string.indexOf('\t');
        while(tabPosition != -1) {
            String tabReplacementHere = getTabReplacement(x);
            string = string.substring(0, tabPosition) + tabReplacementHere + string.substring(tabPosition + 1);
            tabPosition += tabReplacementHere.length();
            tabPosition = string.indexOf('\t', tabPosition);
        }
        return string;
    }
    
    private String getTabReplacement(int x) {
        int align = 0;
        switch(this) {
            case CONVERT_TO_ONE_SPACE:
                return " ";
            case CONVERT_TO_FOUR_SPACES:
                return "    ";
            case CONVERT_TO_EIGHT_SPACES:
                return "        ";
            case ALIGN_TO_COLUMN_4:
                align = 4 - (x % 4);
                break;
            case ALIGN_TO_COLUMN_8:
                align = 8 - (x % 8);
                break;
        }
        StringBuilder replace = new StringBuilder();
        for(int i = 0; i < align; i++)
            replace.append(" ");
        return replace.toString();
    }
}
