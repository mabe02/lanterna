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

/**
 * Represents the user pressing a key on the keyboard. If the user held down ctrl and/or alt before pressing the key, 
 * this may be recorded in this class, depending on the terminal implementation and if such information in available.
 * KeyStroke objects are normally constructed by a KeyDecodingProfile, which works off a character stream that likely
 * coming from the system's standard input. Because of this, the class can only represent what can be read and 
 * interpreted from the input stream; for example, certain key-combinations like ctrl+i is indistiguishable from a tab
 * key press.
 * @author martin
 */
public class KeyStroke {
    private final Key key;
    private final boolean ctrlDown;
    private final boolean altDown;

    public KeyStroke(Key key, boolean ctrlDown, boolean altDown) {
        this.key = key;
        this.ctrlDown = ctrlDown;
        this.altDown = altDown;
    }

    public Key getKey() {
        return key;
    }

    public boolean isCtrlDown() {
        return ctrlDown;
    }

    public boolean isAltDown() {
        return altDown;
    }

    @Override
    public String toString() {
        return "KeyStroke{" + "key=" + key + ", ctrlDown=" + ctrlDown + ", altDown=" + altDown + '}';
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 19 * hash + (this.ctrlDown ? 1 : 0);
        hash = 19 * hash + (this.altDown ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KeyStroke other = (KeyStroke) obj;
        if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
            return false;
        }
        if (this.ctrlDown != other.ctrlDown) {
            return false;
        }
        if (this.altDown != other.altDown) {
            return false;
        }
        return true;
    }
}
