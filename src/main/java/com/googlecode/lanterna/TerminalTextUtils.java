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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class contains a number of utility methods for analyzing characters and strings in a terminal context. The main
 * purpose is to make it easier to work with text that may or may not contain double-width text characters, such as CJK
 * (Chinese, Japanese, Korean) and other special symbols. This class assumes those are all double-width and in case the
 * terminal (-emulator) chooses to draw them (somehow) as single-column then all the calculations in this class will be
 * wrong. It seems safe to assume what this class considers double-width really is taking up two columns though.
 * 
 * @author Martin
 */
public class TerminalTextUtils {
    private TerminalTextUtils() {
    }

    /**
     * Given a character, is this character considered to be a CJK character?
     * Shamelessly stolen from
     * <a href="http://stackoverflow.com/questions/1499804/how-can-i-detect-japanese-text-in-a-java-string">StackOverflow</a>
     * where it was contributed by user Rakesh N
     * @param c Character to test
     * @return {@code true} if the character is a CJK character
     *
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
     * Checks if a character is expected to be taking up two columns if printed to a terminal. This will generally be
     * {@code true} for CJK (Chinese, Japanese and Korean) characters.
     * @param c Character to test if it's double-width when printed to a terminal
     * @return {@code true} if this character is expected to be taking up two columns when printed to the terminal,
     * otherwise {@code false}
     */
    public static boolean isCharDoubleWidth(final char c) {
        return isCharCJK(c);
    }

    /**
     * Checks if a particular character is a control character, in Lanterna this currently means it's 0-31 or 127 in the
     * ascii table.
     * @param c character to test
     * @return {@code true} if the character is a control character, {@code false} otherwise
     */
    public static boolean isControlCharacter(char c) {
        return c < 32 || c == 127;
    }

    /**
     * Checks if a particular character is printable. This generally means that the code is not a control character that
     * isn't able to be printed to the terminal properly. For example, NULL, ENQ, BELL and ESC and all control codes
     * that has no proper character associated with it so the behaviour is undefined and depends completely on the
     * terminal what happens if you try to print them. However, certain control characters have a particular meaning to
     * the terminal and are as such considered printable. In Lanterna, we consider these control characters printable:
     * <ul>
     *     <li>Backspace</li>
     *     <li>Horizontal Tab</li>
     *     <li>Line feed</li>
     * </ul>
     *
     * @param c character to test
     * @return {@code true} if the character is considered printable, {@code false} otherwise
     */
    public static boolean isPrintableCharacter(char c) {
        return !isControlCharacter(c) || c == '\t' || c == '\n' || c == '\b';
    }

    /**
     * @deprecated Call {@code getColumnWidth(s)} instead
     */
    @Deprecated
    public static int getTrueWidth(String s) {
        return getColumnWidth(s);
    }

    /**
     * Given a string, returns how many columns this string would need to occupy in a terminal, taking into account that
     * CJK characters takes up two columns.
     * @param s String to check length
     * @return Number of actual terminal columns the string would occupy
     */
    public static int getColumnWidth(String s) {
        return getColumnIndex(s, s.length());
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
     */
    public static int getColumnIndex(String s, int stringCharacterIndex) throws StringIndexOutOfBoundsException {
        int index = 0;
        for(int i = 0; i < stringCharacterIndex; i++) {
            if(isCharCJK(s.charAt(i))) {
                index++;
            }
            index++;
        }
        return index;
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
     */
    public static int getStringCharacterIndex(String s, int columnIndex) {
        int index = 0;
        int counter = 0;
        while(counter < columnIndex) {
            if(isCharCJK(s.charAt(index++))) {
                counter++;
                if(counter == columnIndex) {
                    return index - 1;
                }
            }
            counter++;
        }
        return index;
    }

    /**
     * Given a string that may or may not contain CJK characters, returns the substring which will fit inside
     * <code>availableColumnSpace</code> columns. This method does not handle special cases like tab or new-line.
     * <p>
     * Calling this method is the same as calling {@code fitString(string, 0, availableColumnSpace)}.
     * @param string The string to fit inside the availableColumnSpace
     * @param availableColumnSpace Number of columns to fit the string inside
     * @return The whole or part of the input string which will fit inside the supplied availableColumnSpace
     */
    public static String fitString(String string, int availableColumnSpace) {
        return fitString(string, 0, availableColumnSpace);
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
            column += TerminalTextUtils.isCharCJK(c) ? 2 : 1;
        }
        if(column > fromColumn) {
            bob.append(" ");
            availableColumnSpace--;
        }

        while(availableColumnSpace > 0 && index < string.length()) {
            char c = string.charAt(index++);
            availableColumnSpace -= TerminalTextUtils.isCharCJK(c) ? 2 : 1;
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
     * This method will calculate word wrappings given a number of lines of text and how wide the text can be printed.
     * The result is a list of new rows where word-wrapping was applied.
     * @param maxWidth Maximum number of columns that can be used before word-wrapping is applied, if <= 0 then the
     *                 lines will be returned unchanged
     * @param lines Input text
     * @return The input text word-wrapped at {@code maxWidth}; this may contain more rows than the input text
     */
    public static List<String> getWordWrappedText(int maxWidth, String... lines) {
        //Bounds checking
        if(maxWidth <= 0) {
            return Arrays.asList(lines);
        }

        List<String> result = new ArrayList<String>();
        LinkedList<String> linesToBeWrapped = new LinkedList<String>(Arrays.asList(lines));
        while(!linesToBeWrapped.isEmpty()) {
            String row = linesToBeWrapped.removeFirst();
            int rowWidth = getColumnWidth(row);
            if(rowWidth <= maxWidth) {
                result.add(row);
            }
            else {
                //Now search in reverse and find the first possible line-break
                final int characterIndexMax = getStringCharacterIndex(row, maxWidth);
                int characterIndex = characterIndexMax;
                while(characterIndex >= 0 &&
                        !Character.isSpaceChar(row.charAt(characterIndex)) &&
                        !isCharCJK(row.charAt(characterIndex))) {
                    characterIndex--;
                }
                // right *after* a CJK is also a "nice" spot to break the line!
                if (characterIndex >= 0 && characterIndex < characterIndexMax &&
                      isCharCJK(row.charAt(characterIndex))) {
                    characterIndex++; // with these conditions it fits!
                }

                if(characterIndex < 0) {
                    //Failed! There was no 'nice' place to cut so just cut it at maxWidth
                    characterIndex = Math.max(characterIndexMax, 1); // at least 1 char
                    result.add(row.substring(0, characterIndex));
                    linesToBeWrapped.addFirst(row.substring(characterIndex));
                }
                else {
                    // characterIndex == 0 only happens, if either
                    //   - first char is CJK and maxWidth==1   or
                    //   - first char is whitespace
                    // either way: put it in row before break to prevent infinite loop.
                    characterIndex = Math.max( characterIndex, 1); // at least 1 char
                    
                    //Ok, split the row, add it to the result and continue processing the second half on a new line
                    result.add(row.substring(0, characterIndex));
                    while(characterIndex < row.length() &&
                          Character.isSpaceChar(row.charAt(characterIndex))) {
                        characterIndex++;
                    }
                    if (characterIndex < row.length()) { // only if rest contains non-whitespace
                        linesToBeWrapped.addFirst(row.substring(characterIndex));
                    }
                }
            }
        }
        return result;
    }
}
