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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Common key press profiles that seems to be shared by most terminals/terminal emulators.
 * @author mabe02
 */
public class CommonProfile extends KeyMappingProfile
{
    private static final List<CharacterPattern> COMMON_PATTERNS =
            new ArrayList<CharacterPattern>(Arrays.asList(
                new CharacterPattern[] {
                    new BasicCharacterPattern(new Key(Key.Kind.ArrowUp), ESC_CODE, '[', 'A'),
                    new BasicCharacterPattern(new Key(Key.Kind.ArrowDown), ESC_CODE, '[', 'B'),
                    new BasicCharacterPattern(new Key(Key.Kind.ArrowRight), ESC_CODE, '[', 'C'),
                    new BasicCharacterPattern(new Key(Key.Kind.ArrowLeft), ESC_CODE, '[', 'D'),
                    new BasicCharacterPattern(new Key(Key.Kind.Tab), '\t'),
                    new BasicCharacterPattern(new Key(Key.Kind.Enter), '\n'),
                    new BasicCharacterPattern(new Key(Key.Kind.ReverseTab), ESC_CODE, '[', 'Z'),
                    new BasicCharacterPattern(new Key(Key.Kind.Backspace), (char)0x7f),
                    new BasicCharacterPattern(new Key(Key.Kind.Insert), ESC_CODE, '[', '2', '~'),
                    new BasicCharacterPattern(new Key(Key.Kind.Delete), ESC_CODE, '[', '3', '~'),
                    new BasicCharacterPattern(new Key(Key.Kind.Home), ESC_CODE, '[', 'H'),
                    new BasicCharacterPattern(new Key(Key.Kind.End), ESC_CODE, '[', 'F'),
                    new BasicCharacterPattern(new Key(Key.Kind.PageUp), ESC_CODE, '[', '5', '~'),
                    new BasicCharacterPattern(new Key(Key.Kind.PageDown), ESC_CODE, '[', '6', '~'),
                    new ScreenInfoCharacterPattern()
                }));

    @Override
    Collection<CharacterPattern> getPatterns()
    {
        return new ArrayList<CharacterPattern>(COMMON_PATTERNS);
    }

}
