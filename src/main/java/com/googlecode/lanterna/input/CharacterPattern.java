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
package com.googlecode.lanterna.input;

import java.util.List;

/**
 * Used to compare a list of character if they match a particular pattern, and in that case, return the kind of 
 * keystroke this pattern represents
 *
 * @author martin
 */
@SuppressWarnings("WeakerAccess")
public interface CharacterPattern {

    /**
     * Given a list of characters that this pattern is matching, which keystroke does it map to?
     * @param matching List of characters
     * @return The {@code Key} that this pattern represents
     */
    KeyStroke getResult(List<Character> matching);

    /**
     * Returns true if this pattern is a perfect match (all characters matching and the pattern has no more characters
     * to match) of the supplied characters
     * @param currentMatching List of characters
     * @return True if the list of characters makes a complete (non-partial) match of this pattern
     */
    boolean isCompleteMatch(List<Character> currentMatching);

    /**
     * Returns true if this pattern partially or fully matches the supplied characters
     * @param currentMatching List of characters
     * @return True if this pattern matches fully or partially the supplied list of characters
     */
    boolean matches(List<Character> currentMatching);

}
