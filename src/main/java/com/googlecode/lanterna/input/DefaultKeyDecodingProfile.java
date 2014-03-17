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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This profile attempts to collect as many code combinations as possible without causing any collisions between 
 * patterns. The patterns in here are tested with Linux terminal, XTerm, Gnome terminal, XFCE terminal, Cygwin and 
 * Mac OS X terminal.
 *
 * @author Martin
 */
public class DefaultKeyDecodingProfile implements KeyDecodingProfile {

    private static final List<CharacterPattern> COMMON_PATTERNS
            = new ArrayList<CharacterPattern>(Arrays.asList(
                            new CharacterPattern[]{
                                new BasicCharacterPattern(new KeyStroke(KeyType.Escape), ESC_CODE),
                                new BasicCharacterPattern(new KeyStroke(KeyType.ArrowUp), ESC_CODE, '[', 'A'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.ArrowUp, false, true), ESC_CODE, ESC_CODE, '[', 'A'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.ArrowDown), ESC_CODE, '[', 'B'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.ArrowDown, false, true), ESC_CODE, ESC_CODE, '[', 'B'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.ArrowRight), ESC_CODE, '[', 'C'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.ArrowRight, false, true), ESC_CODE, ESC_CODE, '[', 'C'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.ArrowLeft), ESC_CODE, '[', 'D'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.ArrowLeft, false, true), ESC_CODE, ESC_CODE, '[', 'D'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Tab), '\t'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Enter), '\n'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Enter), '\r', '\u0000'), //OS X
                                new BasicCharacterPattern(new KeyStroke(KeyType.ReverseTab), ESC_CODE, '[', 'Z'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Backspace), (char) 0x7f),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Insert), ESC_CODE, '[', '2', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Insert, false, true), ESC_CODE, ESC_CODE, '[', '2', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Delete), ESC_CODE, '[', '3', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Delete, false, true), ESC_CODE, ESC_CODE, '[', '3', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Home), ESC_CODE, '[', 'H'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Home, false, true), ESC_CODE, ESC_CODE, '[', 'H'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Home), ESC_CODE, 'O', 'H'),  //Gnome terminal
                                new BasicCharacterPattern(new KeyStroke(KeyType.Home), ESC_CODE, '[', '1', '~'), //Putty
                                new BasicCharacterPattern(new KeyStroke(KeyType.End), ESC_CODE, '[', 'F'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.End, false, true), ESC_CODE, ESC_CODE, '[', 'F'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.End), ESC_CODE, 'O', 'F'),   //Gnome terminal
                                new BasicCharacterPattern(new KeyStroke(KeyType.End), ESC_CODE, '[', '4', '~'),  //Putty
                                new BasicCharacterPattern(new KeyStroke(KeyType.PageUp), ESC_CODE, '[', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.PageUp, false, true), ESC_CODE, ESC_CODE, '[', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.PageDown), ESC_CODE, '[', '6', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.PageDown, false, true), ESC_CODE, ESC_CODE, '[', '6', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F1), ESC_CODE, '[', '1', '1', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F1), ESC_CODE, 'O', 'P'), //Cygwin
                                new BasicCharacterPattern(new KeyStroke(KeyType.F1), ESC_CODE, '[', '[', 'A'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F2), ESC_CODE, '[', '1', '2', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F2), ESC_CODE, 'O', 'Q'), //Cygwin
                                new BasicCharacterPattern(new KeyStroke(KeyType.F2), ESC_CODE, '[', '[', 'B'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F3), ESC_CODE, '[', '1', '3', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F3), ESC_CODE, 'O', 'R'), //Cygwin
                                new BasicCharacterPattern(new KeyStroke(KeyType.F3), ESC_CODE, '[', '[', 'C'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F4), ESC_CODE, '[', '1', '4', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F4), ESC_CODE, 'O', 'S'), //Cygwin
                                new BasicCharacterPattern(new KeyStroke(KeyType.F4), ESC_CODE, '[', '[', 'D'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F5), ESC_CODE, '[', '1', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F5), ESC_CODE, '[', '[', 'E'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F6), ESC_CODE, '[', '1', '7', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F7), ESC_CODE, '[', '1', '8', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F8), ESC_CODE, '[', '1', '9', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F9), ESC_CODE, '[', '2', '0', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F10), ESC_CODE, '[', '2', '1', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F11), ESC_CODE, '[', '2', '3', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F12), ESC_CODE, '[', '2', '4', '~'),

                                //Function keys with alt
                                new BasicCharacterPattern(new KeyStroke(KeyType.F1, false, true), ESC_CODE, ESC_CODE, '[', '1', '1', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F1, false, true), ESC_CODE, ESC_CODE, 'O', 'P'), //Cygwin
                                new BasicCharacterPattern(new KeyStroke(KeyType.F2, false, true), ESC_CODE, ESC_CODE, '[', '1', '2', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F2, false, true), ESC_CODE, ESC_CODE, 'O', 'Q'), //Cygwin
                                new BasicCharacterPattern(new KeyStroke(KeyType.F3, false, true), ESC_CODE, ESC_CODE, '[', '1', '3', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F3, false, true), ESC_CODE, ESC_CODE, 'O', 'R'), //Cygwin
                                new BasicCharacterPattern(new KeyStroke(KeyType.F4, false, true), ESC_CODE, ESC_CODE, '[', '1', '4', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F4, false, true), ESC_CODE, ESC_CODE, 'O', 'S'), //Cygwin
                                new BasicCharacterPattern(new KeyStroke(KeyType.F5, false, true), ESC_CODE, ESC_CODE, '[', '1', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F6, false, true), ESC_CODE, ESC_CODE, '[', '1', '7', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F7, false, true), ESC_CODE, ESC_CODE, '[', '1', '8', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F8, false, true), ESC_CODE, ESC_CODE, '[', '1', '9', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F9, false, true), ESC_CODE, ESC_CODE, '[', '2', '0', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F10, false, true), ESC_CODE, ESC_CODE, '[', '2', '1', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F11, false, true), ESC_CODE, ESC_CODE, '[', '2', '3', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F12, false, true), ESC_CODE, ESC_CODE, '[', '2', '4', '~'),

                                //Function keys with ctrl
                                new BasicCharacterPattern(new KeyStroke(KeyType.F1, true, false), ESC_CODE, '[', '1', ';', '5', 'P'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F2, true, false), ESC_CODE, '[', '1', ';', '5', 'Q'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F3, true, false), ESC_CODE, '[', '1', ';', '5', 'R'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F4, true, false), ESC_CODE, '[', '1', ';', '5', 'S'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F5, true, false), ESC_CODE, '[', '1', '5', ';', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F6, true, false), ESC_CODE, '[', '1', '7', ';', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F7, true, false), ESC_CODE, '[', '1', '8', ';', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F8, true, false), ESC_CODE, '[', '1', '9', ';', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F9, true, false), ESC_CODE, '[', '2', '0', ';', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F10, true, false), ESC_CODE, '[', '2', '1', ';', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F11, true, false), ESC_CODE, '[', '2', '3', ';', '5', '~'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F12, true, false), ESC_CODE, '[', '2', '4', ';', '5', '~'),

                                new NormalCharacterPattern(),
                                new AltAndCharacterPattern(),
                                new CtrlAndCharacterPattern(),
                                new CtrlAltAndCharacterPattern(),
                                new ScreenInfoCharacterPattern()
                            }));

    @Override
    public Collection<CharacterPattern> getPatterns() {
        return new ArrayList<CharacterPattern>(COMMON_PATTERNS);
    }

}
