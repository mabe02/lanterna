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
    private final Character character;
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

    public Key(Kind kind, boolean ctrlPressed, boolean altPressed) {
        this.kind = kind;
        this.character = kind.getRepresentationKey();
        this.altPressed = altPressed;
        this.ctrlPressed = ctrlPressed;
    }

    /**
     * What kind of key was pressed? This is a bit misleading as we consider 'normal' keys one kind
     * but then have one kind per special keys. So, "Page Up" is one kind, "Insert" is one kind but
     * "G" is a normal key. If getKind() returns Kind.NormalKey, you can use getCharacter() to see
     * exactly which character was typed in.
     */
    public Kind getKind() {
        return kind;
    }

    public Character getCharacter() {
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
        NormalKey,
        Escape,
        Backspace,
        ArrowLeft,
        ArrowRight,
        ArrowUp,
        ArrowDown,
        Insert,
        Delete,
        Home,
        End,
        PageUp,
        PageDown,
        Tab('\t'),
        ReverseTab,
        Enter('\n'),
        F1,
        F2,
        F3,
        F4,
        F5,
        F6,
        F7,
        F8,
        F9,
        F10,   //No idea what to pick here, but it doesn't really matter
        F11,   //No idea what to pick here, but it doesn't really matter
        F12,   //No idea what to pick here, but it doesn't really matter
        Unknown,
        CursorLocation,
        ;

        private Character representationKey;

        private Kind() {
            this(null);
        }
        
        private Kind(Character representationKey) {
            this.representationKey = representationKey;
        }

        public Character getRepresentationKey() {
            return representationKey;
        }
    }

    @Override
    public String toString() {
        return getKind().toString() + (getKind() == Kind.NormalKey ? ": " + character : "") +
                (ctrlPressed ? " (ctrl)" : "") + (altPressed ? " (alt)" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Key other = (Key) obj;
        if (this.kind != other.kind) {
            return false;
        }
        if (this.character != other.character) {
            return false;
        }
        if (this.altPressed != other.altPressed) {
            return false;
        }
        if (this.ctrlPressed != other.ctrlPressed) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.kind != null ? this.kind.hashCode() : 0);
        hash = 19 * hash + this.character;
        hash = 19 * hash + (this.altPressed ? 1 : 0);
        hash = 19 * hash + (this.ctrlPressed ? 1 : 0);
        return hash;
    }
}
