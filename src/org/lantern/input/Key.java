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

/**
 *
 * @author mabe02
 */
public class Key
{
    private Kind kind;
    private char character;

    public Key(char character)
    {
        this.character = character;
        this.kind = Kind.NormalKey;
    }

    public Key(Kind kind)
    {
        this.kind = kind;
        this.character = kind.getRepresentationKey();
    }

    public Kind getKind()
    {
        return kind;
    }

    public char getCharacter()
    {
        return character;
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
        return getKind().toString() + (getKind() == Kind.NormalKey ? ": " + character : "");
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
