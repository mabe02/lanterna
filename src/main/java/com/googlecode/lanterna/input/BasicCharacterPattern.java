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
package com.googlecode.lanterna.input;

import java.util.Arrays;
import java.util.List;

/**
 * Very simple pattern that matches the input stream against a pre-defined list of characters. For the pattern to match,
 * the list of characters must match exactly what's coming in on the input stream.
 * 
 * @author Martin, Andreas
 */
public class BasicCharacterPattern implements CharacterPattern {
    private final KeyStroke result;
    private final char[] pattern;

    /**
     * Creates a new BasicCharacterPattern that matches a particular sequence of characters into a {@code KeyStroke}
     * @param result {@code KeyStroke} that this pattern will translate to
     * @param pattern Sequence of characters that translates into the {@code KeyStroke}
     */
    public BasicCharacterPattern(KeyStroke result, char... pattern) {
        this.result = result;
        this.pattern = pattern;
    }

    /**
     * Returns the characters that makes up this pattern, as an array that is a copy of the array used internally
     * @return Array of characters that defines this pattern
     */
    public char[] getPattern() {
        return Arrays.copyOf(pattern, pattern.length);
    }

    /**
     * Returns the keystroke that this pattern results in
     * @return The keystoke this pattern will return if it matches
     */
    public KeyStroke getResult() {
        return result;
    }

    @Override
    public Matching match(List<Character> seq) {
        int size = seq.size();
        
        if(size > pattern.length) {
            return null; // nope
        }
        for (int i = 0; i < size; i++) {
            if (pattern[i] != seq.get(i)) {
                return null; // nope
            }
        }
        if (size == pattern.length) {
            return new Matching( getResult() ); // yep
        } else {
            return Matching.NOT_YET; // maybe later
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicCharacterPattern)) {
            return false;
        }

        BasicCharacterPattern other = (BasicCharacterPattern) obj;
        return Arrays.equals(this.pattern, other.pattern);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Arrays.hashCode(this.pattern);
        return hash;
    }
}
