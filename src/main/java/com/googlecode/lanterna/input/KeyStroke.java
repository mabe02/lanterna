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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.input;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents the user pressing a key on the keyboard. If the user held down ctrl and/or alt before pressing the key, 
 * this may be recorded in this class, depending on the terminal implementation and if such information in available.
 * KeyStroke objects are normally constructed by a KeyDecodingProfile, which works off a character stream that likely
 * coming from the system's standard input. Because of this, the class can only represent what can be read and 
 * interpreted from the input stream; for example, certain key-combinations like ctrl+i is indistinguishable from a tab
 * key press.
 * <p>
 * Use the <tt>keyType</tt> field to determine what kind of key was pressed. For ordinary letters, numbers and symbols, the 
 * <tt>keyType</tt> will be <tt>KeyType.Character</tt> and the actual character value of the key is in the 
 * <tt>character</tt> field. Please note that return (\n) and tab (\t) are not sorted under type <tt>KeyType.Character</tt>
 * but <tt>KeyType.Enter</tt> and <tt>KeyType.Tab</tt> instead.
 * @author martin
 */
public class KeyStroke {
    private final KeyType keyType;
    private final Character character;
    private final boolean ctrlDown;
    private final boolean altDown;
    private final boolean shiftDown;
    private final long eventTime;

    /**
     * Constructs a KeyStroke based on a supplied keyType; character will be null and both ctrl and alt will be 
     * considered not pressed. If you try to construct a KeyStroke with type KeyType.Character with this constructor, it
     * will always throw an exception; use another overload that allows you to specify the character value instead.
     * @param keyType Type of the key pressed by this keystroke
     */
    public KeyStroke(KeyType keyType) {
        this(keyType, false, false);
    }
    
    /**
     * Constructs a KeyStroke based on a supplied keyType; character will be null.
     * If you try to construct a KeyStroke with type KeyType.Character with this constructor, it
     * will always throw an exception; use another overload that allows you to specify the character value instead.
     * @param keyType Type of the key pressed by this keystroke
     * @param ctrlDown Was ctrl held down when the main key was pressed?
     * @param altDown Was alt held down when the main key was pressed?
     */
    public KeyStroke(KeyType keyType, boolean ctrlDown, boolean altDown) {
        this(keyType, null, ctrlDown, altDown, false);
    }
    
    /**
     * Constructs a KeyStroke based on a supplied keyType; character will be null.
     * If you try to construct a KeyStroke with type KeyType.Character with this constructor, it
     * will always throw an exception; use another overload that allows you to specify the character value instead.
     * @param keyType Type of the key pressed by this keystroke
     * @param ctrlDown Was ctrl held down when the main key was pressed?
     * @param altDown Was alt held down when the main key was pressed?
     * @param shiftDown Was shift held down when the main key was pressed?
     */
    public KeyStroke(KeyType keyType, boolean ctrlDown, boolean altDown, boolean shiftDown) {
        this(keyType, null, ctrlDown, altDown, shiftDown);
    }
    
    /**
     * Constructs a KeyStroke based on a supplied character, keyType is implicitly KeyType.Character.
     * <p>
     * A character-based KeyStroke does not support the shiftDown flag, as the shift state has
     * already been accounted for in the character itself, depending on user's keyboard layout.
     * @param character Character that was typed on the keyboard
     * @param ctrlDown Was ctrl held down when the main key was pressed?
     * @param altDown Was alt held down when the main key was pressed?
     */
    public KeyStroke(Character character, boolean ctrlDown, boolean altDown) {
        this(KeyType.Character, character, ctrlDown, altDown, false);
    }

    /**
     * Constructs a KeyStroke based on a supplied character, keyType is implicitly KeyType.Character.
     * <p>
     * A character-based KeyStroke does not support the shiftDown flag, as the shift state has
     * already been accounted for in the character itself, depending on user's keyboard layout.
     * @param character Character that was typed on the keyboard
     * @param ctrlDown Was ctrl held down when the main key was pressed?
     * @param altDown Was alt held down when the main key was pressed?
     * @param shiftDown Was shift held down when the main key was pressed?
     */
    public KeyStroke(Character character, boolean ctrlDown, boolean altDown, boolean shiftDown) {
        this(KeyType.Character, character, ctrlDown, altDown, shiftDown);
    }
    
    private KeyStroke(KeyType keyType, Character character, boolean ctrlDown, boolean altDown, boolean shiftDown) {
        if(keyType == KeyType.Character && character == null) {
            throw new IllegalArgumentException("Cannot construct a KeyStroke with type KeyType.Character but no character information");
        }
        //Enforce character for some key types
        switch(keyType) {
            case Backspace:
                character = '\b';
                break;
            case Enter:
                character = '\n';
                break;
            case Tab:
                character = '\t';
                break;
            default:
        }
        this.keyType = keyType;
        this.character = character;
        this.shiftDown = shiftDown;
        this.ctrlDown = ctrlDown;
        this.altDown = altDown;
        this.eventTime = System.currentTimeMillis();
    }

    /**
     * Type of key that was pressed on the keyboard, as represented by the KeyType enum. If the value if 
     * KeyType.Character, you need to call getCharacter() to find out which letter, number or symbol that was actually
     * pressed.
     * @return Type of key on the keyboard that was pressed
     */
    public KeyType getKeyType() {
        return keyType;
    }

    /**
     * For keystrokes of ordinary keys (letters, digits, symbols), this method returns the actual character value of the
     * key. For all other key types, it returns null.
     * @return Character value of the key pressed, or null if it was a special key
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * @return Returns true if ctrl was help down while the key was typed (depending on terminal implementation)
     */
    public boolean isCtrlDown() {
        return ctrlDown;
    }

    /**
     * @return Returns true if alt was help down while the key was typed (depending on terminal implementation)
     */
    public boolean isAltDown() {
        return altDown;
    }

    /**
     * @return Returns true if shift was help down while the key was typed (depending on terminal implementation)
     */
    public boolean isShiftDown() {
        return shiftDown;
    }

    /**
     * Gets the time when the keystroke was recorded. This isn't necessarily the time the keystroke happened, but when
     * Lanterna received the event, so it may not be accurate down to the millisecond.
     * @return The unix time of when the keystroke happened, in milliseconds
     */
    public long getEventTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KeyStroke{keytype=").append(keyType);
        if (character != null) {
            char ch = character;
            sb.append(", character='");
            switch (ch) {
            // many of these cases can only happen through user code:
            case 0x00: sb.append("^@"); break;
            case 0x08: sb.append("\\b"); break;
            case 0x09: sb.append("\\t"); break;
            case 0x0a: sb.append("\\n"); break;
            case 0x0d: sb.append("\\r"); break;
            case 0x1b: sb.append("^["); break;
            case 0x1c: sb.append("^\\"); break;
            case 0x1d: sb.append("^]"); break;
            case 0x1e: sb.append("^^"); break;
            case 0x1f: sb.append("^_"); break;
            default:
                if (ch <= 26) {
                    sb.append('^').append((char)(ch+64));
                } else { sb.append(ch); }
            }
            sb.append('\'');
        }
        if (ctrlDown || altDown || shiftDown) {
            String sep=""; sb.append(", modifiers=[");
            if (ctrlDown) {  sb.append(sep).append("ctrl"); sep=","; }
            if (altDown) {   sb.append(sep).append("alt"); sep=","; }
            if (shiftDown) { sb.append(sep).append("shift"); sep=","; }
            sb.append("]");
        }
        return sb.append('}').toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.keyType != null ? this.keyType.hashCode() : 0);
        hash = 41 * hash + (this.character != null ? this.character.hashCode() : 0);
        hash = 41 * hash + (this.ctrlDown ? 1 : 0);
        hash = 41 * hash + (this.altDown ? 1 : 0);
        hash = 41 * hash + (this.shiftDown ? 1 : 0);
        return hash;
    }

    @SuppressWarnings("SimplifiableIfStatement")
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
        return this.ctrlDown == other.ctrlDown && 
               this.altDown == other.altDown &&
               this.shiftDown == other.shiftDown;
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
            k = new KeyStroke(KeyType.Character, keyStr.charAt(0), false, false, false);
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
