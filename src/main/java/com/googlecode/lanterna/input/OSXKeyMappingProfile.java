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
 * Copyright (C) 2010-2012
 */
package com.googlecode.lanterna.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author smozes
 */
public class OSXKeyMappingProfile extends KeyMappingProfile {
    private static final List<CharacterPattern> OSX_PATTERNS = new ArrayList<CharacterPattern>(
            Arrays.asList(
                new CharacterPattern[] {
                    new BasicCharacterPattern(new Key(Key.Kind.Enter), '\r', '\u0000')
                }));

    @Override
    Collection<CharacterPattern> getPatterns() {
        return OSX_PATTERNS;
    }
}
