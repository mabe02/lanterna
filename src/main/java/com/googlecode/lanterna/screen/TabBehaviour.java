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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.screen;

/**
 * What to do about the tab character when putting on a {@code Screen}. Since tabs are a bit special, their meaning
 * depends on which column the cursor is in when it's printed, we'll need to have some way to tell the Screen what to
 * do when encountering a tab character.
 *
 * @author martin
 */
public enum TabBehaviour {
    /**
     * Tab characters are not replaced, this will probably have undefined and weird behaviour!
     */
    IGNORE(null, null),
    /**
     * Tab characters are replaced with a single blank space, no matter where the tab was placed.
     */
    CONVERT_TO_ONE_SPACE(1, null),
    /**
     * Tab characters are replaced with two blank spaces, no matter where the tab was placed.
     */
    CONVERT_TO_TWO_SPACES(2, null),
    /**
     * Tab characters are replaced with three blank spaces, no matter where the tab was placed.
     */
    CONVERT_TO_THREE_SPACES(3, null),
    /**
     * Tab characters are replaced with four blank spaces, no matter where the tab was placed.
     */
    CONVERT_TO_FOUR_SPACES(4, null),
    /**
     * Tab characters are replaced with eight blank spaces, no matter where the tab was placed.
     */
    CONVERT_TO_EIGHT_SPACES(8, null),
    /**
     * Tab characters are replaced with enough space characters to reach the next column index that is evenly divisible
     * by 4, simulating a normal tab character when placed inside a text document.
     */
    ALIGN_TO_COLUMN_4(null, 4),
    /**
     * Tab characters are replaced with enough space characters to reach the next column index that is evenly divisible
     * by 8, simulating a normal tab character when placed inside a text document.
     */
    ALIGN_TO_COLUMN_8(null, 8),
    ;

    private final Integer replaceFactor;
    private final Integer alignFactor;

    TabBehaviour(Integer replaceFactor, Integer alignFactor) {
        this.replaceFactor = replaceFactor;
        this.alignFactor = alignFactor;
    }
    
    /**
     * Given a string, being placed on the screen at column X, returns the same string with all tab characters (\t) 
     * replaced according to this TabBehaviour.
     * @param string String that is going to be put to the screen, potentially containing tab characters
     * @param columnIndex Column on the screen where the first character of the string is going to end up
     * @return The input string with all tab characters replaced with spaces, according to this TabBehaviour
     */
    public String replaceTabs(String string, int columnIndex) {
        int tabPosition = string.indexOf('\t');
        while(tabPosition != -1) {
            String tabReplacementHere = getTabReplacement(columnIndex + tabPosition);
            string = string.substring(0, tabPosition) + tabReplacementHere + string.substring(tabPosition + 1);
            tabPosition += tabReplacementHere.length();
            tabPosition = string.indexOf('\t', tabPosition);
        }
        return string;
    }

    /**
     * Returns the String that can replace a tab at the specified position, according to this TabBehaviour.
     * @param columnIndex Column index of where the tab character is placed
     * @return String consisting of 1 or more space character
     */
    public String getTabReplacement(int columnIndex) {
        int replaceCount;
        StringBuilder replace = new StringBuilder();
        if(replaceFactor != null) {
            replaceCount = replaceFactor;
        }
        else if (alignFactor != null) {
            replaceCount = alignFactor - (columnIndex % alignFactor);
        }
        else {
            return "\t";
        }
        for(int i = 0; i < replaceCount; i++) {
            replace.append(" ");
        }
        return replace.toString();
    }
}
