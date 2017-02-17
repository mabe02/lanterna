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
                                new BasicCharacterPattern(new KeyStroke(KeyType.Tab), '\t'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Enter), '\n'),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Enter), '\r', '\u0000'), //OS X
                                new BasicCharacterPattern(new KeyStroke(KeyType.Backspace), (char) 0x7f),
                                new BasicCharacterPattern(new KeyStroke(KeyType.Backspace), (char) 0x08),
                                new BasicCharacterPattern(new KeyStroke(KeyType.F1), ESC_CODE, '[', '[', 'A'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F2), ESC_CODE, '[', '[', 'B'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F3), ESC_CODE, '[', '[', 'C'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F4), ESC_CODE, '[', '[', 'D'), //Linux
                                new BasicCharacterPattern(new KeyStroke(KeyType.F5), ESC_CODE, '[', '[', 'E'), //Linux

                                new EscapeSequenceCharacterPattern(),
                                new NormalCharacterPattern(),
                                new AltAndCharacterPattern(),
                                new CtrlAndCharacterPattern(),
                                new CtrlAltAndCharacterPattern(),
                                new ScreenInfoCharacterPattern(),
                                new MouseCharacterPattern()
                            }));

    @Override
    public Collection<CharacterPattern> getPatterns() {
        return new ArrayList<CharacterPattern>(COMMON_PATTERNS);
    }

}
