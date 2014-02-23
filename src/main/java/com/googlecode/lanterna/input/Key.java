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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a key pressed. Use getKind() to see if it's a normal alpha-numeric
 * key or any special key. Sorry if the special keys are sort of european-language centered, that's
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
        F10,
        F11,
        F12,
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
    
    /**
 	 * Creates a Key from a string representation in Vim's key notation.
     * @param keyStr the string representation of this key
     * @return the created {@link Key}
     */
    public static Key fromString(String keyStr) {
    	String keyStrLC = keyStr.toLowerCase();
    	Key k;
    	if (keyStr.length() == 1) {
    		k = new Key(keyStr.charAt(0), false, false);
    	} else if (keyStr.startsWith("<") && keyStr.endsWith(">")) {
    		if (keyStrLC.equals("<s-tab>")) {
    			k = new Key(Key.Kind.ReverseTab);
    		} else if (keyStr.contains("-")) {
    			ArrayList<String> segments = new ArrayList<String>(Arrays.asList(keyStr.substring(1, keyStr.length()-1).split("-")));
    			if (segments.size() < 2) throw new IllegalArgumentException("Invalid vim notation: "  + keyStr);
    			String characterStr = segments.remove(segments.size() - 1);
    			boolean altPressed = false;
    			boolean ctrlPressed = false;
    			for (String modifier : segments) {
    				if ("c".equals(modifier.toLowerCase())) ctrlPressed = true;
    				else if ("a".equals(modifier.toLowerCase())) altPressed = true;
    				else if ("s".equals(modifier.toLowerCase())) characterStr = characterStr.toUpperCase();
    			}
    			k = new Key(characterStr.charAt(0), ctrlPressed, altPressed);
    		} else {
				if (keyStrLC.startsWith("<esc")) k = new Key(Key.Kind.Escape);
				else if (keyStrLC.equals("<cr>") || keyStrLC.equals("<enter>") || keyStrLC.equals("<return>"))  k = new Key(Key.Kind.Enter);
				else if (keyStrLC.equals("<bs>")) k = new Key(Key.Kind.Backspace);
				else if (keyStrLC.equals("<tab>"))  k = new Key(Key.Kind.Tab);
				else if (keyStrLC.equals("<space>")) k = new Key(' ', false, false);
				else if (keyStrLC.equals("<up>"))	k = new Key(Key.Kind.ArrowUp);
				else if (keyStrLC.equals("<down>"))	k = new Key(Key.Kind.ArrowDown);
				else if (keyStrLC.equals("<left>"))	k = new Key(Key.Kind.ArrowLeft);
				else if (keyStrLC.equals("<right>")) k = new Key(Key.Kind.ArrowRight);
				else if (keyStrLC.equals("<insert>")) k = new Key(Key.Kind.Insert);
				else if (keyStrLC.equals("<del>"))	k = new Key(Key.Kind.Delete);
				else if (keyStrLC.equals("<home>"))	k = new Key(Key.Kind.Home);
				else if (keyStrLC.equals("<end>"))	k = new Key(Key.Kind.End);
				else if (keyStrLC.equals("<pageup>"))  k = new Key(Key.Kind.PageUp);
				else if (keyStrLC.equals("<pagedown>")) k = new Key(Key.Kind.PageDown);
				else if (keyStrLC.equals("<f1>"))	k = new Key(Key.Kind.F1);
				else if (keyStrLC.equals("<f2>"))	k = new Key(Key.Kind.F2);
				else if (keyStrLC.equals("<f3>"))	k = new Key(Key.Kind.F3);
				else if (keyStrLC.equals("<f4>"))	k = new Key(Key.Kind.F4);
				else if (keyStrLC.equals("<f5>"))	k = new Key(Key.Kind.F5);
				else if (keyStrLC.equals("<f6>"))	k = new Key(Key.Kind.F6);
				else if (keyStrLC.equals("<f7>"))	k = new Key(Key.Kind.F7);
				else if (keyStrLC.equals("<f8>"))	k = new Key(Key.Kind.F8);
				else if (keyStrLC.equals("<f9>"))	k = new Key(Key.Kind.F9);
				else if (keyStrLC.equals("<f10>"))  k = new Key(Key.Kind.F10);
				else if (keyStrLC.equals("<f11>"))  k = new Key(Key.Kind.F11);
				else if (keyStrLC.equals("<f12>"))  k = new Key(Key.Kind.F12);
				else throw new IllegalArgumentException("Invalid vim notation: "  + keyStr);
			}
    	} else throw new IllegalArgumentException("Invalid vim notation: "  + keyStr);
    	return k;
    }
    
    public boolean equalsString(String... keyStrs) {
    	boolean match = false;
		for (String keyStr : keyStrs) {
    		if(equals(Key.fromString(keyStr))) {
    			match =true;
    			break;
    		}
    	}
		return match;
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
