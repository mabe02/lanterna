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

/**
 * Represents a key pressed. Use getKind() to see if it's a normal alpha-numeric
 * key or any special key. Currently the function keys F1 - F12 are not implemented.
 * Also, sorry if the special keys are sort of european-language centered, that's
 * unfortunately the only keyboard I have to test this with.
 * @author Martin
 */
public class Key
{
    private final Kind kind;
    private final char character;
    private final boolean altPressed;
    private final boolean ctrlPressed;

    public Key(char character) {
        this(character, false, false);
    }
    
    public Key(char character, boolean ctrlPressed, boolean altPressed) {
        this.character = character;
        this.kind = Kind.NormalKey;
        this.ctrlPressed = ctrlPressed;
        this.altPressed = altPressed;
    }

    public Key(Kind kind) {
        this.kind = kind;
        this.character = kind.getRepresentationKey();
        this.altPressed = false;
        this.ctrlPressed = false;
    }

    public Kind getKind()
    {
        return kind;
    }

    public char getCharacter()
    {
        return character;
    }

    public boolean isAltPressed() {
        return altPressed;
    }

    public boolean isCtrlPressed() {
        return ctrlPressed;
    }

    public enum Kind
    {
        NormalKey('N'),
        Escape('\\'),
        Backspace('B'),
        ArrowLeft('L'),
        ArrowRight('R'),
        ArrowUp('U'),
        ArrowDown('D'),
        Insert('I'),
        Delete('T'),
        Home('H'),
        End('E'),
        PageUp('P'),
        PageDown('O'),
        Tab('\t'),
        ReverseTab('/'),
        Enter('\n'),
        F1('1'),
        F2('2'),
        F3('3'),
        F4('4'),
        F5('5'),
        F6('6'),
        F7('7'),
        F8('8'),
        F9('9'),
        F10('Q'),   //No idea what to pick here, but it doesn't really matter
        F11('W'),   //No idea what to pick here, but it doesn't really matter
        F12('Y'),   //No idea what to pick here, but it doesn't really matter
        Unknown('!'),
        CursorLocation('£');

        private char representationKey;

        private Kind(char representationKey)
        {
            this.representationKey = representationKey;
        }

        public char getRepresentationKey()
        {
            return representationKey;
        }
    }

    @Override
    public String toString()
    {
        return getKind().toString() + (getKind() == Kind.NormalKey ? ": " + character : "") +
                (ctrlPressed ? " (ctrl)" : "") + (altPressed ? " (alt)" : "");
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(obj.getClass() != getClass())
            return false;
        return character == ((Key)(obj)).character;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 73 * hash + this.character;
        return hash;
    }
}
