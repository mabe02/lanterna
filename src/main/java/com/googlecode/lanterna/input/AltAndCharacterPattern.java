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
 * Character pattern that matches characters pressed while ALT key is held down
 */
public class AltAndCharacterPattern implements CharacterPattern {
    @Override
    public KeyStroke getResult(List<Character> matching) {
        return new KeyStroke(matching.get(1), false, true);
    }

    @Override
    public boolean isCompleteMatch(List<Character> currentMatching) {
        return currentMatching.size() == 2 &&
                currentMatching.get(0) == KeyDecodingProfile.ESC_CODE &&
                Character.isLetterOrDigit(currentMatching.get(1));
    }

    @Override
    public boolean matches(List<Character> currentMatching) {
        return currentMatching.get(0) == KeyDecodingProfile.ESC_CODE &&
                (currentMatching.size() == 1 || (Character.isLetterOrDigit(currentMatching.get(1)) && currentMatching.size() == 2));
    }
}
