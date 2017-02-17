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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.input;

import static com.googlecode.lanterna.input.KeyDecodingProfile.ESC_CODE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This implementation of CharacterPattern matches two similar patterns
 * of Escape sequences, that many terminals produce for special keys.<p>
 * 
 * These sequences all start with Escape, followed by either an open bracket
 * or a capital letter O (these two are treated as equivalent).<p>
 * 
 * Then follows a list of zero or up to two decimals separated by a 
 * semicolon, and a non-digit last character.<p>
 * 
 * If the last character is a tilde (~) then the first number defines
 * the key (through stdMap), otherwise the last character itself defines
 * the key (through finMap).<p>
 * 
 * The second number, if provided by the terminal, specifies the modifier
 * state (shift,alt,ctrl). The value is 1 + sum(modifiers), where shift is 1,
 * alt is 2 and ctrl is 4.<p>
 * 
 * The two maps stdMap and finMap can be customized in subclasses to add,
 * remove or replace keys - to support non-standard Terminals.<p>
 * 
 * Examples: (on a gnome terminal)<br>
 * ArrowUp is "Esc [ A"; Alt-ArrowUp is "Esc [ 1 ; 3 A"<br>
 * both are handled by finMap mapping 'A' to ArrowUp <br><br>
 * F6 is "Esc [ 1 7 ~"; Ctrl-Shift-F6 is "Esc [ 1 7 ; 6 R"<br>
 * both are handled by stdMap mapping 17 to F6 <br><br>
 * 
 * @author Andreas
 *
 */
public class EscapeSequenceCharacterPattern implements CharacterPattern {
    // state machine used to match key sequence:
    private enum State {
        START, INTRO, NUM1, NUM2, DONE
    }
    // bit-values for modifier keys: only used internally
    public static final int SHIFT = 1, ALT = 2, CTRL = 4;

    /**
     *  Map of recognized "standard pattern" sequences:<br>
     *   e.g.: 24 -&gt; F12 : "Esc [ <b>24</b> ~"
     */
    protected final Map<Integer, KeyType>   stdMap = new HashMap<Integer, KeyType>();
    /**
     *  Map of recognized "finish pattern" sequences:<br>
     *   e.g.: 'A' -&gt; ArrowUp : "Esc [ <b>A</b>"
     */
    protected final Map<Character, KeyType> finMap = new HashMap<Character, KeyType>();
    /**
     *  A flag to control, whether an Esc-prefix for an Esc-sequence is to be treated
     *  as Alt-pressed. Some Terminals (e.g. putty) report the Alt-modifier like that.<p>
     *  If the application is e.g. more interested in seeing separate Escape and plain
     *  Arrow keys, then it should replace this class by a subclass that sets this flag
     *  to false. (It might then also want to remove the CtrlAltAndCharacterPattern.)
     */
    protected boolean useEscEsc = true;
    
    /**
     * Create an instance with a standard set of mappings.
     */
    public EscapeSequenceCharacterPattern() {
        finMap.put('A', KeyType.ArrowUp);
        finMap.put('B', KeyType.ArrowDown);
        finMap.put('C', KeyType.ArrowRight);
        finMap.put('D', KeyType.ArrowLeft);
        finMap.put('E', KeyType.Unknown); // gnome-terminal center key on numpad
        finMap.put('G', KeyType.Unknown); // putty center key on numpad
        finMap.put('H', KeyType.Home);
        finMap.put('F', KeyType.End);
        finMap.put('P', KeyType.F1);
        finMap.put('Q', KeyType.F2);
        finMap.put('R', KeyType.F3);
        finMap.put('S', KeyType.F4);
        finMap.put('Z', KeyType.ReverseTab);

        stdMap.put(1,  KeyType.Home);
        stdMap.put(2,  KeyType.Insert);
        stdMap.put(3,  KeyType.Delete);
        stdMap.put(4,  KeyType.End);
        stdMap.put(5,  KeyType.PageUp);
        stdMap.put(6,  KeyType.PageDown);
        stdMap.put(11, KeyType.F1);
        stdMap.put(12, KeyType.F2);
        stdMap.put(13, KeyType.F3);
        stdMap.put(14, KeyType.F4);
        stdMap.put(15, KeyType.F5);
        stdMap.put(16, KeyType.F5);
        stdMap.put(17, KeyType.F6);
        stdMap.put(18, KeyType.F7);
        stdMap.put(19, KeyType.F8);
        stdMap.put(20, KeyType.F9);
        stdMap.put(21, KeyType.F10);
        stdMap.put(23, KeyType.F11);
        stdMap.put(24, KeyType.F12);
        stdMap.put(25, KeyType.F13);
        stdMap.put(26, KeyType.F14);
        stdMap.put(28, KeyType.F15);
        stdMap.put(29, KeyType.F16);
        stdMap.put(31, KeyType.F17);
        stdMap.put(32, KeyType.F18);
        stdMap.put(33, KeyType.F19);
    }

    /**
     * combines a KeyType and modifiers into a KeyStroke.
     * Subclasses can override this for customization purposes.
     * 
     * @param key the KeyType as determined by parsing the sequence.
     *   It will be null, if the pattern looked like a key sequence but wasn't
     *   identified.
     * @param mods the bitmask of the modifer keys pressed along with the key.
     * @return either null (to report mis-match), or a valid KeyStroke.
     */
    protected KeyStroke getKeyStroke(KeyType key, int mods) {
        boolean bShift = false, bCtrl = false, bAlt = false;
        if (key == null) { return null; } // alternative: key = KeyType.Unknown;
        if (mods >= 0) { // only use when non-negative!
            bShift = (mods & SHIFT) != 0;
            bAlt   = (mods & ALT)   != 0;
            bCtrl  = (mods & CTRL)  != 0;
        }
        return new KeyStroke( key , bCtrl, bAlt, bShift);
    }

    /**
     * combines the raw parts of the sequence into a KeyStroke.
     * This method does not check the first char, but overrides may do so.
     * 
     * @param first  the char following after Esc in the sequence (either [ or O)
     * @param num1   the first decimal, or 0 if not in the sequence
     * @param num2   the second decimal, or 0 if not in the sequence
     * @param last   the terminating char.
     * @param bEsc   whether an extra Escape-prefix was found.
     * @return either null (to report mis-match), or a valid KeyStroke.
     */
    protected KeyStroke getKeyStrokeRaw(char first,int num1,int num2,char last,boolean bEsc) {
        KeyType kt;
        boolean bPuttyCtrl = false;
        if (last == '~' && stdMap.containsKey(num1)) {
            kt = stdMap.get(num1);
        } else if (finMap.containsKey(last)) {
            kt = finMap.get(last);
            // Putty sends ^[OA for ctrl arrow-up, ^[[A for plain arrow-up:
            // but only for A-D -- other ^[O... sequences are just plain keys
            if (first == 'O' && last >= 'A' && last <= 'D') { bPuttyCtrl = true; }
            // if we ever stumble into "keypad-mode", then it will end up inverted.
        } else {
            kt = null; // unknown key.
        }
        int mods = num2 - 1;
        if (bEsc) {
            if (mods >= 0) { mods |= ALT; }
            else { mods = ALT; }
        }
        if (bPuttyCtrl) {
            if (mods >= 0) { mods |= CTRL; }
            else { mods = CTRL; }
        }
        return getKeyStroke( kt, mods );
    }

    @Override
    public Matching match(List<Character> cur) {
        State state = State.START;
        int num1 = 0, num2 = 0;
        char first = '\0', last = '\0';
        boolean bEsc = false;

        for (char ch : cur) {
            switch (state) {
            case START:
                if (ch != ESC_CODE) {
                    return null; // nope
                }
                state = State.INTRO;
                continue;
            case INTRO:
                // Recognize a second Escape to mean "Alt is pressed".
                // (at least putty sends it that way)
                if (useEscEsc && ch == ESC_CODE && ! bEsc) {
                    bEsc = true; continue;
                }

                // Key sequences supported by this class must
                //    start either with Esc-[ or Esc-O
                if (ch != '[' && ch != 'O') {
                    return null; // nope
                }
                first = ch; state = State.NUM1;
                continue;
            case NUM1:
                if (ch == ';') {
                    state = State.NUM2;
                } else if (Character.isDigit(ch)) {
                    num1 = num1 * 10 + Character.digit(ch, 10);
                } else {
                    last = ch; state = State.DONE;
                }
                continue;
            case NUM2:
                if (Character.isDigit(ch)) {
                    num2 = num2 * 10 + Character.digit(ch, 10);
                } else {
                    last = ch; state = State.DONE;
                }
                continue;
            case DONE: // once done, extra characters spoil it
                return null; // nope
            }
        }
        if (state == State.DONE) {
            KeyStroke ks = getKeyStrokeRaw(first,num1,num2,last,bEsc);
            return ks != null ? new Matching( ks ) : null; // depends
        } else {
            return Matching.NOT_YET; // maybe later
        }
    }
}
