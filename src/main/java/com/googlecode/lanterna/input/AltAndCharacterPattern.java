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

import java.util.List;

/**
 * Character pattern that matches characters pressed while ALT key is held down
 * 
 * @author Martin, Andreas
 */
public class AltAndCharacterPattern implements CharacterPattern {

    @Override
    public Matching match(List<Character> seq) {
        int size = seq.size();
        if (size > 2 || seq.get(0) != KeyDecodingProfile.ESC_CODE) {
            return null; // nope
        }
        if (size == 1) {
            return Matching.NOT_YET; // maybe later
        }
        if ( Character.isISOControl(seq.get(1)) ) {
            return null; // nope
        }
        KeyStroke ks = new KeyStroke(seq.get(1), false, true);
        return new Matching( ks ); // yep
    }
}
