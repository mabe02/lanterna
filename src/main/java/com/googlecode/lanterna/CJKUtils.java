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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities class for analyzing and working with CJK (Chinese, Japanese, Korean) characters. The main purpose of this
 * class is to assist in figuring out how many terminal columns a character (and in extension, a String) takes up. The
 * main issue is that while most latin (and latin-related) character can be trusted to consume one column in the
 * terminal, CJK characters tends to take two, partly due to the square nature of the characters but mostly due to the
 * fact that they require most space to distinguish.
 * 
 * @author Martin
 */
public class CJKUtils {    
    private CJKUtils() {
    }

    /**
     * Given a character, is this character considered to be a CJK character?
     * Shamelessly stolen from
     * <a href="http://stackoverflow.com/questions/1499804/how-can-i-detect-japanese-text-in-a-java-string>StackOverflow</a>
     * where it was contributed by user Rakesh N
     * @param c Character to test
     * @return {@code true} if the character is a CJK character
     */
    public static boolean isCharCJK(final char c) {
        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
        return (unicodeBlock == Character.UnicodeBlock.HIRAGANA)
                || (unicodeBlock == Character.UnicodeBlock.KATAKANA)
                || (unicodeBlock == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS)
                || (unicodeBlock == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO)
                || (unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO)
                || (unicodeBlock == Character.UnicodeBlock.HANGUL_SYLLABLES)
                || (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                || (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
                || (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B)
                || (unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS)
                || (unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
                || (unicodeBlock == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT)
                || (unicodeBlock == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
                || (unicodeBlock == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS)
                || (unicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS && c < 0xFF61);    //The magic number here is the separating index between full-width and half-width
    }

    /**
     * Given a string, returns how many columns this string would need to occupy in a terminal, taking into account that
     * CJK characters takes up two columns.
     * @param s String to check length
     * @return Number of actual terminal columns the string would occupy
     */
    public static int getTrueWidth(String s) {
        int count = 0;
        for(int i = 0; i < s.length(); i++) {
            if(isCharCJK(s.charAt(i))) {
                count++;
            }
            count++;
        }
        return count;
    }

    /**
     * Given a string that may or may not contain CJK characters, returns the substring which will fit inside
     * <code>availableColumnSpace</code> columns. This method does not handle special cases like tab or new-line. Please
     * notice that {@code fromColumn} is not a character index inside the string, but a column index as if the string
     * has been printed from the left-most side of the terminal. So if the string is "日本語", fromColumn set to 1 will
     * not starting counting from the second character ("本") in the string but from the CJK filler character belonging
     * to "日". If you want to count from a particular character index inside the string, please pass in a substring
     * and use fromColumn set to 0.
     * @param string The string to fit inside the availableColumnSpace
     * @param fromColumn From what column of the input string to start fitting (see description above!)
     * @param availableColumnSpace Number of columns to fit the string inside
     * @return The whole or part of the input string which will fit inside the supplied availableColumnSpace
     */
    public static String fitString(String string, int fromColumn, int availableColumnSpace) {
        if(availableColumnSpace <= 0) {
            return "";
        }

        StringBuilder bob = new StringBuilder();
        int column = 0;
        int index = 0;
        while(index < string.length() && column < fromColumn) {
            char c = string.charAt(index++);
            column += CJKUtils.isCharCJK(c) ? 2 : 1;
        }
        if(column > fromColumn) {
            bob.append(" ");
            availableColumnSpace--;
        }

        while(availableColumnSpace > 0 && index < string.length()) {
            char c = string.charAt(index++);
            availableColumnSpace -= CJKUtils.isCharCJK(c) ? 2 : 1;
            if(availableColumnSpace < 0) {
                bob.append(' ');
            }
            else {
                bob.append(c);
            }
        }
        return bob.toString();
    }

    /**
     * Finds and returns the character in the supplied string at the particular column specified. The difference between
     * calling this method and using {@code charAt(..)} directly on the string is that this method will take CJK
     * character spacing into account. For example, if the String contains あいうえお and you call
     * {@code getCharacterInColumn(4, 0)}, it will return う and not お.
     *
     * @param string String to look for the character in
     * @param column Column to fetch the character from, assuming CJK characters take up two columns
     * @return The character at the specified coordinates. If the character at the index is the second half of a CJK
     * character, it will return the char at the position before (i.e. the CJK character).
     * @throws IndexOutOfBoundsException If the column value is outside of the valid range
     */
    public static char getCharacterInColumn(String string, int column) {
        if(column < 0) {
            throw new IllegalArgumentException("Cannot call getCharacterInColumn(..) with negative column index!");
        }
        int characterIndex = 0;
        int currentColumn = 0;
        while(currentColumn < column) {
            if(CJKUtils.isCharCJK(string.charAt(characterIndex++))) {
                currentColumn += 2;
                if(currentColumn > column) {
                    characterIndex--;
                }
            }
            else {
                currentColumn += 1;
            }
        }
        return string.charAt(characterIndex);
    }

    /**
     * Returns {@code true} if and only if the character in the string at the specified column is the second half of a
     * double-width CJK character. If a 'regular' character is at this position, or if it's the first half of a CJK
     * character, this will return {@code false}. This is essentially pretending the string has been printed in the
     * top-left corner of a terminal and looks at what's in the specified column.
     * @param column Column to fetch the character from, assuming CJK characters take up two columns
     * @return {@code true} if the character is a CJK filler space, {@code false} otherwise
     * @throws IndexOutOfBoundsException If the column values are outside of the valid range
     */
    public static boolean isColumnCJKFillerCharacter(String string, int column) {
        if(column < 0) {
            throw new IllegalArgumentException("Cannot call isColumnCJKFillerCharacter(..) with negative column index!");
        }
        int characterIndex = 0;
        int currentColumn = 0;
        while(currentColumn < column) {
            if(CJKUtils.isCharCJK(string.charAt(characterIndex++))) {
                currentColumn += 2;
                if(currentColumn > column) {
                    return true;
                }
            }
            else {
                currentColumn += 1;
            }
        }
        //Make sure out position is valid within the string (if not, this will throw)
        string.charAt(characterIndex);
        //Must be a non-filler at this point
        return false;
    }
}
