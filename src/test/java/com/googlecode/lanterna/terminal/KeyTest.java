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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeyTest {

    @Test
    public void testFromVim() {
        {
            KeyStroke k = KeyStroke.fromString("a");
            assertEquals(KeyType.Character, k.getKeyType());
            assertEquals(new Character('a'), k.getCharacter());
            assertEquals(false, k.isCtrlDown());
            assertEquals(false, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<c-a>");
            assertEquals(KeyType.Character, k.getKeyType());
            assertEquals(new Character('a'), k.getCharacter());
            assertEquals(true, k.isCtrlDown());
            assertEquals(false, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<a-a>");
            assertEquals(KeyType.Character, k.getKeyType());
            assertEquals(new Character('a'), k.getCharacter());
            assertEquals(false, k.isCtrlDown());
            assertEquals(true, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<c-a-a>");
            assertEquals(k.getKeyType(), KeyType.Character);
            assertEquals(new Character('a'), k.getCharacter());
            assertEquals(true, k.isCtrlDown());
            assertEquals(true, k.isAltDown());
        }
        assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<s-tab>").getKeyType());
        assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<S-tab>").getKeyType());
        assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<S-Tab>").getKeyType());
        assertEquals(KeyType.Enter, KeyStroke.fromString("<cr>").getKeyType());
        assertEquals(KeyType.PageUp, KeyStroke.fromString("<PageUp>").getKeyType());
    }

}
