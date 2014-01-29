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
package com.googlecode.lanterna;

import java.awt.FontMetrics;

/**
 *
 * @author Martin
 */
public class LanternaUtils {
	
	public static final int COMPARE_CHAR = (int) '.';

    //Shamelessly stolen from http://stackoverflow.com/questions/1499804/how-can-i-detect-japanese-text-in-a-java-string
    //Contributed there by user Rakesh N
    public static boolean isCharCJK(final char c) {
        if ((Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
                || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B)
                || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS)
                || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
                || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT)
                || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
                || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS)) {
            return true;
        }
        return false;
    }
    
    public static boolean isWideChar(FontMetrics metrics, final char c) {
    	return metrics.charWidth((int)c) > metrics.charWidth(COMPARE_CHAR);
    }
}
