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

import java.util.Collection;

/**
 * Adds some codes from the gnome-terminal, commonly used in Ubuntu.
 * @author Martin
 */
public class GnomeTerminalProfile extends CommonProfile
{
    @Override
    Collection<CharacterPattern> getPatterns()
    {
        Collection<CharacterPattern> gnomePatterns = super.getPatterns();
        gnomePatterns.add(new BasicCharacterPattern(new Key(Key.Kind.Home), ESC_CODE, 'O', 'H'));
        gnomePatterns.add(new BasicCharacterPattern(new Key(Key.Kind.End), ESC_CODE, 'O', 'F'));
        return gnomePatterns;
    }
}
