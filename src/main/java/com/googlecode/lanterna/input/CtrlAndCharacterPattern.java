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
 * Character pattern that matches characters pressed while CTRL key is held down
 * @author Martin
 */
public class CtrlAndCharacterPattern implements CharacterPattern {
    @Override
    public KeyStroke getResult(List<Character> matching) {
        int firstCode = 'a' - 1;
        return new KeyStroke((char) (firstCode + (int) matching.get(0)), true, false);
    }

    @Override
    public boolean isCompleteMatch(List<Character> currentMatching) {
        return matches(currentMatching);
    }

    @Override
    public boolean matches(List<Character> currentMatching) {
        return currentMatching.size() <= 1 &&
                !(currentMatching.get(0) == '\n' ||
                        currentMatching.get(0) == '\r' ||
                        currentMatching.get(0) == '\t') &&
                currentMatching.get(0) <= 26;

    }
}
