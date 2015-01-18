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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.input;

import java.util.Arrays;
import java.util.List;

/**
 * Matcher for a sequence of characters and turns them into a KeyStroke
 */
class BasicCharacterPattern implements CharacterPattern {
    private final KeyStroke result;
    private final char[] pattern;

    BasicCharacterPattern(KeyStroke result, char... pattern) {
        this.result = result;
        this.pattern = pattern;
    }

    @Override
    public boolean matches(List<Character> currentMatching) {
        if(currentMatching.size() > pattern.length) {
            return false;
        }
        int minSize = Math.min(currentMatching.size(), pattern.length);
        for (int i = 0; i < minSize; i++) {
            if (pattern[i] != currentMatching.get(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public KeyStroke getResult(List<Character> matching) {
        return result;
    }

    @Override
    public boolean isCompleteMatch(List<Character> currentMatching) {
        return pattern.length == currentMatching.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicCharacterPattern)) {
            return false;
        }

        BasicCharacterPattern other = (BasicCharacterPattern) obj;
        return Arrays.equals(pattern, other.pattern);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Arrays.hashCode(this.pattern);
        return hash;
    }
}
