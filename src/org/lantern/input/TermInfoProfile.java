/*
 *  Copyright (C) 2010 mabe02
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lantern.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.lantern.terminal.TerminalProperties;

/**
 *
 * @author mabe02
 */
public class TermInfoProfile extends KeyMappingProfile
{
    private static final List<CharacterPattern> TERMINFO_PATTERNS =
            new ArrayList<CharacterPattern>();

    public TermInfoProfile(TerminalProperties terminalProperties)
    {
        TERMINFO_PATTERNS.addAll(
                Arrays.asList(
                    new CharacterPattern(new Key(Key.Kind.ArrowUp), decodeTermInfoString(terminalProperties.getKeyCursorUp())),
                    new CharacterPattern(new Key(Key.Kind.ArrowDown), decodeTermInfoString(terminalProperties.getKeyCursorDown())),
                    new CharacterPattern(new Key(Key.Kind.ArrowRight), decodeTermInfoString(terminalProperties.getKeyCursorRight())),
                    new CharacterPattern(new Key(Key.Kind.ArrowLeft), decodeTermInfoString(terminalProperties.getKeyCursorLeft())),
                    new CharacterPattern(new Key(Key.Kind.Tab), '\t'),
                    new CharacterPattern(new Key(Key.Kind.Enter), System.getProperty("line.separator").toCharArray()),
                    new CharacterPattern(new Key(Key.Kind.ReverseTab), decodeTermInfoString(terminalProperties.getKeyReverseTab())),
                    new CharacterPattern(new Key(Key.Kind.Backspace), (char)0x7f),
                    new CharacterPattern(new Key(Key.Kind.Insert), decodeTermInfoString(terminalProperties.getKeyInsert())),
                    new CharacterPattern(new Key(Key.Kind.Delete), decodeTermInfoString(terminalProperties.getKeyDelete())),
                    new CharacterPattern(new Key(Key.Kind.Home), decodeTermInfoString(terminalProperties.getKeyHome())),
                    new CharacterPattern(new Key(Key.Kind.End), decodeTermInfoString(terminalProperties.getKeyEnd())),
                    new CharacterPattern(new Key(Key.Kind.PageUp), decodeTermInfoString(terminalProperties.getKeyPageUp())),
                    new CharacterPattern(new Key(Key.Kind.PageDown), decodeTermInfoString(terminalProperties.getKeyPageDown()))));
    }

    @Override
    Collection<CharacterPattern> getPatterns()
    {
        return new ArrayList<CharacterPattern>(TERMINFO_PATTERNS);
    }

    private static char[] decodeTermInfoString(String terminfoString)
    {
        terminfoString.replace("\\E", new String(new char[] { 0x1b }));
        return terminfoString.toCharArray();    //Bytes? Chars? See TerminfoTerminal
    }
}
