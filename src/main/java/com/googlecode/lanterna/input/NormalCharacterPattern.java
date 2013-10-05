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
package com.googlecode.lanterna.input;

import java.util.List;

public class NormalCharacterPattern implements CharacterPattern {
    
    @Override
    public Key getResult(List<Character> matching) {
        return new Key(matching.get(0), false, false);
    }

    @Override
    public boolean isCompleteMatch(List<Character> currentMatching) {
        if(currentMatching.size() != 1)
            return false;
        if(!Character.isISOControl(currentMatching.get(0)))
            return false;
        return true;
    }

    @Override
    public boolean matches(List<Character> currentMatching) {
        if(currentMatching.size() != 1)
            return false;
        if(!Character.isLetterOrDigit(currentMatching.get(0)))
            return false;
        return true;
    }    
}
