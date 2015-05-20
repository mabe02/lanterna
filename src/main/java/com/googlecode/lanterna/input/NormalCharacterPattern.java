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
 * Character pattern that matches one character as one KeyStroke with the character that was read
 */
public class NormalCharacterPattern implements CharacterPattern {
    @Override
    public KeyStroke getResult(List<Character> matching) {
        return new KeyStroke(matching.get(0), false, false);
    }

    @Override
    public boolean isCompleteMatch(List<Character> currentMatching) {
        return currentMatching.size() == 1;
    }

    @Override
    public boolean matches(List<Character> currentMatching) {
        return currentMatching.size() == 1 && isPrintableChar(currentMatching.get(0));
    }

    /**
     * From http://stackoverflow.com/questions/220547/printable-char-in-java
     * @param c character to test
     * @return True if this is a 'normal', printable character, false otherwise
     */
    private static boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }
}
