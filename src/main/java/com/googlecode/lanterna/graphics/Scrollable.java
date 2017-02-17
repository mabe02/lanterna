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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.graphics;

import java.io.IOException;

/**
 * Describes an area that can be 'scrolled', by moving a range of lines up or down. Certain terminals will implement
 * this through extensions and are much faster than if lanterna tries to manually erase and re-print the text.
 *
 * @author Andreas
 */
public interface Scrollable {
    /**
     * Scroll a range of lines of this Scrollable according to given distance.
     * 
     * If scroll-range is empty (firstLine &gt; lastLine || distance == 0) then
     * this method does nothing.
     * 
     * Lines that are scrolled away from are cleared.
     * 
     * If absolute value of distance is equal or greater than number of lines
     * in range, then all lines within the range will be cleared.
     *  
     * @param firstLine first line of the range to be scrolled (top line is 0)
     * @param lastLine last (inclusive) line of the range to be scrolled
     * @param distance if &gt; 0: move lines up, else if &lt; 0: move lines down.
     * @throws IOException If there was an I/O error when running the operation
     */
    void scrollLines(int firstLine, int lastLine, int distance) throws IOException;
}
