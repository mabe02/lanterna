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

/**
 * Represents a key pressed. Use getKind() to see if it's a normal alpha-numeric
 * key or any special key. Currently the function keys F1 - F12 are not implemented.
 * Also, sorry if the special keys are sort of european-language centered, that's
 * unfortunately the only keyboard I have to test this with.
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

    public static class Kind
    {
        public static final int NormalKey_ID = 1;
        public static final int Escape_ID = 2;
        public static final int Backspace_ID = 3;
        public static final int ArrowLeft_ID = 4;
        public static final int ArrowRight_ID = 5;
        public static final int ArrowUp_ID = 6;
        public static final int ArrowDown_ID = 7;
        public static final int Insert_ID = 8;
        public static final int Delete_ID = 9;
        public static final int Home_ID = 10;
        public static final int End_ID = 11;
        public static final int PageUp_ID = 12;
        public static final int PageDown_ID = 13;
        public static final int Tab_ID = 14;
        public static final int ReverseTab_ID = 15;
        public static final int Enter_ID = 16;
        public static final int Unknown_ID = 17;
        public static final int CursorLocation_ID = 18;
        
        public static final Kind NormalKey = new Kind(NormalKey_ID, 'N');
        public static final Kind Escape = new Kind(Escape_ID, '\\');
        public static final Kind Backspace = new Kind(Backspace_ID, 'B');
        public static final Kind ArrowLeft = new Kind(ArrowLeft_ID, 'L');
        public static final Kind ArrowRight = new Kind(ArrowRight_ID, 'R');
        public static final Kind ArrowUp = new Kind(ArrowUp_ID, 'U');
        public static final Kind ArrowDown = new Kind(ArrowDown_ID, 'D');
        public static final Kind Insert = new Kind(Insert_ID, 'I');
        public static final Kind Delete = new Kind(Delete_ID, 'T');
        public static final Kind Home = new Kind(Home_ID, 'H');
        public static final Kind End = new Kind(End_ID, 'E');
        public static final Kind PageUp = new Kind(PageUp_ID, 'P');
        public static final Kind PageDown = new Kind(PageDown_ID, 'O');
        public static final Kind Tab = new Kind(Tab_ID, '\t');
        public static final Kind ReverseTab = new Kind(ReverseTab_ID, '/');
        public static final Kind Enter = new Kind(Enter_ID, '\n');
        public static final Kind Unknown = new Kind(Unknown_ID, '!');
        public static final Kind CursorLocation = new Kind(CursorLocation_ID, '£');

        private int index;
        private char representationKey;

        private Kind(int index, char representationKey)
        {
            this.index = index;
            this.representationKey = representationKey;
        }

        public char getRepresentationKey()
        {
            return representationKey;
        }

        public int getIndex() {
            return index;
        }
    }

    public String toString()
    {
        return getKind().toString() + (getKind() == Kind.NormalKey ? ": " + character : "");
    }

    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(obj.getClass() != getClass())
            return false;
        return character == ((Key)(obj)).character;
    }

    public int hashCode()
    {
        int hash = 3;
        hash = 73 * hash + this.character;
        return hash;
    }




}
