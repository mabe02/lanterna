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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna;

/**
 * Utilities class for analyzing and working with CJK (Chinese, Japanese, Korean) characters. The main purpose of this
 * class is to assist in figuring out how many terminal columns a character (and in extension, a String) takes up. The
 * main issue is that while most latin (and latin-related) character can be trusted to consume one column in the
 * terminal, CJK characters tends to take two, partly due to the square nature of the characters but mostly due to the
 * fact that they require most space to distinguish.
 * 
 * @author Martin
 * @see TerminalTextUtils
 * @deprecated Use {@link TerminalTextUtils} instead
 */
public class CJKUtils {    
    private CJKUtils() {
    }

    /**
     * Given a character, is this character considered to be a CJK character?
     * Shamelessly stolen from
     * <a href="http://stackoverflow.com/questions/1499804/how-can-i-detect-japanese-text-in-a-java-string">StackOverflow</a>
     * where it was contributed by user Rakesh N
     * @param c Character to test
     * @return {@code true} if the character is a CJK character
     * @deprecated Use {@code TerminalTextUtils.isCharJCK(c)} instead
     * @see TerminalTextUtils#isCharCJK(char)
     */
    @Deprecated
    public static boolean isCharCJK(final char c) {
        return TerminalTextUtils.isCharCJK(c);
    }

    /**
     * @deprecated Call {@code getColumnWidth(s)} instead
     */
    @Deprecated
    public static int getTrueWidth(String s) {
        return TerminalTextUtils.getColumnWidth(s);
    }

    /**
     * Given a string, returns how many columns this string would need to occupy in a terminal, taking into account that
     * CJK characters takes up two columns.
     * @param s String to check length
     * @return Number of actual terminal columns the string would occupy
     * @deprecated Use {@code TerminalTextUtils.getColumnWidth(s)} instead
     * @see TerminalTextUtils#getColumnWidth(String)
     */
    @Deprecated
    public static int getColumnWidth(String s) {
        return TerminalTextUtils.getColumnIndex(s, s.length());
    }

    /**
     * Given a string and a character index inside that string, find out what the column index of that character would
     * be if printed in a terminal. If the string only contains non-CJK characters then the returned value will be same
     * as {@code stringCharacterIndex}, but if there are CJK characters the value will be different due to CJK
     * characters taking up two columns in width. If the character at the index in the string is a CJK character itself,
     * the returned value will be the index of the left-side of character.
     * @param s String to translate the index from
     * @param stringCharacterIndex Index within the string to get the terminal column index of
     * @return Index of the character inside the String at {@code stringCharacterIndex} when it has been writted to a
     * terminal
     * @throws StringIndexOutOfBoundsException if the index given is outside the String length or negative
     * @deprecated Use {@code TerminalTextUtils.getColumnIndex(s, stringCharacterIndex)} instead
     * @see TerminalTextUtils#getColumnIndex(String, int)
     */
    @Deprecated
    public static int getColumnIndex(String s, int stringCharacterIndex) throws StringIndexOutOfBoundsException {
        return TerminalTextUtils.getColumnIndex(s, stringCharacterIndex);
    }

    /**
     * This method does the reverse of getColumnIndex, given a String and imagining it has been printed out to the
     * top-left corner of a terminal, in the column specified by {@code columnIndex}, what is the index of that
     * character in the string. If the string contains no CJK characters, this will always be the same as
     * {@code columnIndex}. If the index specified is the right column of a CJK character, the index is the same as if
     * the column was the left column. So calling {@code getStringCharacterIndex("英", 0)} and
     * {@code getStringCharacterIndex("英", 1)} will both return 0.
     * @param s String to translate the index to
     * @param columnIndex Column index of the string written to a terminal
     * @return The index in the string of the character in terminal column {@code columnIndex}
     * @deprecated Use {@code TerminalTextUtils.getStringCharacterIndex(s, columnIndex} instead
     * @see TerminalTextUtils#getStringCharacterIndex(String, int)
     */
    @Deprecated
    public static int getStringCharacterIndex(String s, int columnIndex) {
        return TerminalTextUtils.getStringCharacterIndex(s, columnIndex);
    }

    /**
     * Given a string that may or may not contain CJK characters, returns the substring which will fit inside
     * <code>availableColumnSpace</code> columns. This method does not handle special cases like tab or new-line.
     * <p>
     * Calling this method is the same as calling {@code fitString(string, 0, availableColumnSpace)}.
     * @param string The string to fit inside the availableColumnSpace
     * @param availableColumnSpace Number of columns to fit the string inside
     * @return The whole or part of the input string which will fit inside the supplied availableColumnSpace
     * @deprecated Use {@code TerminalTextUtils.fitString(string, availableColumnSpace)} instead
     * @see TerminalTextUtils#fitString(String, int)
     */
    @Deprecated
    public static String fitString(String string, int availableColumnSpace) {
        return TerminalTextUtils.fitString(string, availableColumnSpace);
    }

    /**
     * Given a string that may or may not contain CJK characters, returns the substring which will fit inside
     * <code>availableColumnSpace</code> columns. This method does not handle special cases like tab or new-line.
     * <p>
     * This overload has a {@code fromColumn} parameter that specified where inside the string to start fitting. Please
     * notice that {@code fromColumn} is not a character index inside the string, but a column index as if the string
     * has been printed from the left-most side of the terminal. So if the string is "日本語", fromColumn set to 1 will
     * not starting counting from the second character ("本") in the string but from the CJK filler character belonging
     * to "日". If you want to count from a particular character index inside the string, please pass in a substring
     * and use fromColumn set to 0.
     * @param string The string to fit inside the availableColumnSpace
     * @param fromColumn From what column of the input string to start fitting (see description above!)
     * @param availableColumnSpace Number of columns to fit the string inside
     * @return The whole or part of the input string which will fit inside the supplied availableColumnSpace
     * @deprecated Use {@code TerminalTextUtils.fitString(string, fromColumn, availableColumnSpace)} instead
     * @see TerminalTextUtils#fitString(String, int, int)
     */
    @Deprecated
    public static String fitString(String string, int fromColumn, int availableColumnSpace) {
        return TerminalTextUtils.fitString(string, fromColumn, availableColumnSpace);
    }
}
