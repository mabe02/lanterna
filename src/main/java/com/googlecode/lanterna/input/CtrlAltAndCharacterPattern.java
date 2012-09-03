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

/**
 *
 * @author Martin
 */
public class CtrlAltAndCharacterPattern  implements CharacterPattern {
    
    @Override
    public Key getResult(List<Character> matching) {
        int firstCode = 'a' - 1;
        return new Key((char)(firstCode + (int)matching.get(1).charValue()), true, true);
    }

    @Override
    public boolean isCompleteMatch(List<Character> currentMatching) {
        if(currentMatching.size() != 2)
            return false;
        if(currentMatching.get(0) != KeyMappingProfile.ESC_CODE)
            return false;
        if(currentMatching.get(1).charValue() > 26)
            return false;
        return true;
    }

    @Override
    public boolean matches(List<Character> currentMatching) {
        if(currentMatching.get(0) != KeyMappingProfile.ESC_CODE)
            return false;
        if(currentMatching.size() == 1)
            return true;
        if(currentMatching.get(1).charValue() > 26)
            return false;
        if(currentMatching.size() == 2)
            return true;
        return false;
    }
}
