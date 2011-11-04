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
    private static final List COMMON_PATTERNS =
            new ArrayList(Arrays.asList(
                new CharacterPattern[] {
                    new BasicCharacterPattern(new Key(Key.Kind.ArrowUp), new char[] {ESC_CODE, '[', 'A' }),
                    new BasicCharacterPattern(new Key(Key.Kind.ArrowDown), new char[] {ESC_CODE, '[', 'B' }),
                    new BasicCharacterPattern(new Key(Key.Kind.ArrowRight), new char[] {ESC_CODE, '[', 'C' }),
                    new BasicCharacterPattern(new Key(Key.Kind.ArrowLeft), new char[] {ESC_CODE, '[', 'D' }),
                    new BasicCharacterPattern(new Key(Key.Kind.Tab), new char[] {'\t' }),
                    new BasicCharacterPattern(new Key(Key.Kind.Enter), new char[] {'\n' }),
                    new BasicCharacterPattern(new Key(Key.Kind.ReverseTab), new char[] {ESC_CODE, '[', 'Z' }),
                    new BasicCharacterPattern(new Key(Key.Kind.Backspace), new char[] {(char)0x7f }),
                    new BasicCharacterPattern(new Key(Key.Kind.Insert), new char[] {ESC_CODE, '[', '2', '~' }),
                    new BasicCharacterPattern(new Key(Key.Kind.Delete), new char[] {ESC_CODE, '[', '3', '~' }),
                    new BasicCharacterPattern(new Key(Key.Kind.Home), new char[] {ESC_CODE, '[', 'H' }),
                    new BasicCharacterPattern(new Key(Key.Kind.End), new char[] {ESC_CODE, '[', 'F' }),
                    new BasicCharacterPattern(new Key(Key.Kind.PageUp), new char[] {ESC_CODE, '[', '5', '~' }),
                    new BasicCharacterPattern(new Key(Key.Kind.PageDown), new char[] {ESC_CODE, '[', '6', '~' }),
                    new ScreenInfoCharacterPattern()
                }));

    Collection getPatterns()
    {
        return new ArrayList(COMMON_PATTERNS);
    }

}
