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
    private final KeyType keyType;
    private final Character character;
    private final boolean ctrlDown;
    private final boolean altDown;

    public KeyStroke(KeyType keyType) {
        this(keyType, false, false);
    }

    public KeyStroke(KeyType keyType, boolean ctrlDown, boolean altDown) {
        this(keyType, null, ctrlDown, altDown);
    }
    
    public KeyStroke(Character character, boolean ctrlDown, boolean altDown) {
        this(KeyType.Character, character, ctrlDown, altDown);
    }
    
    public KeyStroke(KeyType keyType, Character character, boolean ctrlDown, boolean altDown) {
        if(keyType == KeyType.Character && character == null) {
            throw new IllegalArgumentException("Cannot construct a KeyStroke with type KeyType.Character but no character information");
        }
        this.keyType = keyType;
        this.character = character;
        this.ctrlDown = ctrlDown;
        this.altDown = altDown;
    }

    public KeyType getKey() {
        return keyType;
    }

    public Character getCharacter() {
        return character;
    }

    public boolean isCtrlDown() {
        return ctrlDown;
    }

    public boolean isAltDown() {
        return altDown;
    }

    @Override
    public String toString() {
        return "KeyStroke{" + "keyType=" + keyType + ", character=" + character + ", ctrlDown=" + ctrlDown + ", altDown=" + altDown + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.keyType != null ? this.keyType.hashCode() : 0);
        hash = 41 * hash + (this.character != null ? this.character.hashCode() : 0);
        hash = 41 * hash + (this.ctrlDown ? 1 : 0);
        hash = 41 * hash + (this.altDown ? 1 : 0);
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
        if (this.keyType != other.keyType) {
            return false;
        }
        if (this.character != other.character && (this.character == null || !this.character.equals(other.character))) {
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
    
    /**
     * Creates a Key from a string representation in Vim's key notation.
     *
     * @param keyStr the string representation of this key
     * @return the created {@link KeyType}
     */
    public static KeyStroke fromString(String keyStr) {
        String keyStrLC = keyStr.toLowerCase();
        KeyStroke k;
        if (keyStr.length() == 1) {
            k = new KeyStroke(KeyType.Character, keyStr.charAt(0), false, false);
        } else if (keyStr.startsWith("<") && keyStr.endsWith(">")) {
            if (keyStrLC.equals("<s-tab>")) {
                k = new KeyStroke(KeyType.ReverseTab);
            } else if (keyStr.contains("-")) {
                ArrayList<String> segments = new ArrayList<String>(Arrays.asList(keyStr.substring(1, keyStr.length() - 1).split("-")));
                if (segments.size() < 2) {
                    throw new IllegalArgumentException("Invalid vim notation: " + keyStr);
                }
                String characterStr = segments.remove(segments.size() - 1);
                boolean altPressed = false;
                boolean ctrlPressed = false;
                for (String modifier : segments) {
                    if ("c".equals(modifier.toLowerCase())) {
                        ctrlPressed = true;
                    } else if ("a".equals(modifier.toLowerCase())) {
                        altPressed = true;
                    } else if ("s".equals(modifier.toLowerCase())) {
                        characterStr = characterStr.toUpperCase();
                    }
                }
                k = new KeyStroke(characterStr.charAt(0), ctrlPressed, altPressed);
            } else {
                if (keyStrLC.startsWith("<esc")) {
                    k = new KeyStroke(KeyType.Escape);
                } else if (keyStrLC.equals("<cr>") || keyStrLC.equals("<enter>") || keyStrLC.equals("<return>")) {
                    k = new KeyStroke(KeyType.Enter);
                } else if (keyStrLC.equals("<bs>")) {
                    k = new KeyStroke(KeyType.Backspace);
                } else if (keyStrLC.equals("<tab>")) {
                    k = new KeyStroke(KeyType.Tab);
                } else if (keyStrLC.equals("<space>")) {
                    k = new KeyStroke(' ', false, false);
                } else if (keyStrLC.equals("<up>")) {
                    k = new KeyStroke(KeyType.ArrowUp);
                } else if (keyStrLC.equals("<down>")) {
                    k = new KeyStroke(KeyType.ArrowDown);
                } else if (keyStrLC.equals("<left>")) {
                    k = new KeyStroke(KeyType.ArrowLeft);
                } else if (keyStrLC.equals("<right>")) {
                    k = new KeyStroke(KeyType.ArrowRight);
                } else if (keyStrLC.equals("<insert>")) {
                    k = new KeyStroke(KeyType.Insert);
                } else if (keyStrLC.equals("<del>")) {
                    k = new KeyStroke(KeyType.Delete);
                } else if (keyStrLC.equals("<home>")) {
                    k = new KeyStroke(KeyType.Home);
                } else if (keyStrLC.equals("<end>")) {
                    k = new KeyStroke(KeyType.End);
                } else if (keyStrLC.equals("<pageup>")) {
                    k = new KeyStroke(KeyType.PageUp);
                } else if (keyStrLC.equals("<pagedown>")) {
                    k = new KeyStroke(KeyType.PageDown);
                } else if (keyStrLC.equals("<f1>")) {
                    k = new KeyStroke(KeyType.F1);
                } else if (keyStrLC.equals("<f2>")) {
                    k = new KeyStroke(KeyType.F2);
                } else if (keyStrLC.equals("<f3>")) {
                    k = new KeyStroke(KeyType.F3);
                } else if (keyStrLC.equals("<f4>")) {
                    k = new KeyStroke(KeyType.F4);
                } else if (keyStrLC.equals("<f5>")) {
                    k = new KeyStroke(KeyType.F5);
                } else if (keyStrLC.equals("<f6>")) {
                    k = new KeyStroke(KeyType.F6);
                } else if (keyStrLC.equals("<f7>")) {
                    k = new KeyStroke(KeyType.F7);
                } else if (keyStrLC.equals("<f8>")) {
                    k = new KeyStroke(KeyType.F8);
                } else if (keyStrLC.equals("<f9>")) {
                    k = new KeyStroke(KeyType.F9);
                } else if (keyStrLC.equals("<f10>")) {
                    k = new KeyStroke(KeyType.F10);
                } else if (keyStrLC.equals("<f11>")) {
                    k = new KeyStroke(KeyType.F11);
                } else if (keyStrLC.equals("<f12>")) {
                    k = new KeyStroke(KeyType.F12);
                } else {
                    throw new IllegalArgumentException("Invalid vim notation: " + keyStr);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid vim notation: " + keyStr);
        }
        return k;
    }
}
