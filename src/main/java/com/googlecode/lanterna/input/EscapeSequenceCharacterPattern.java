package com.googlecode.lanterna.input;

import static com.googlecode.lanterna.input.KeyDecodingProfile.ESC_CODE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This implementation of CharacterPattern matches two similar patterns
 * of Escape sequences, that many terminals produce for special keys.<p>
 * 
 * These sequences start with Escape, followed by either an open bracket
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
 * @author Andreas L.
 *
 */
public class EscapeSequenceCharacterPattern implements CharacterPattern {
    // state machine used to match key sequence:
    private enum State {
        START, INTRO, NUM1, NUM2, DONE;
    }
    // bit-values for modifier keys: only used internally
    private static final int SHIFT = 1, ALT = 2, CTRL = 4;

    // Retain information from successful call to matches():
    // overridden methods can access them for customization.
    protected List<Character> cacheSeq = null;
    protected KeyType cacheKey; protected int cacheMods;

    /**
     *  Map of recognized "standard pattern" sequences:<br>
     *   e.g.: 24 -> F12 : "Esc [ <b>24</b> ~"
     */
    protected final Map<Integer, KeyType>   stdMap = new HashMap<Integer, KeyType>();
    /**
     *  Map of recognized "finish pattern" sequences:<br>
     *   e.g.: 'A' -> ArrowUp : "Esc [ <b>A</b>"
     */
    protected final Map<Character, KeyType> finMap = new HashMap<Character, KeyType>();

    
    /**
     * Create an instance with a standard set of mappings.
     */
    public EscapeSequenceCharacterPattern() {
        finMap.put('A', KeyType.ArrowUp);
        finMap.put('B', KeyType.ArrowDown);
        finMap.put('C', KeyType.ArrowRight);
        finMap.put('D', KeyType.ArrowLeft);
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

    @Override
    public KeyStroke getResult(List<Character> cur) {
        if (cacheSeq != cur) {
            return null;
        }
        boolean bShift = false, bCtrl = false, bAlt = false;
        if (cacheMods > 0) {
            int mods = cacheMods - 1;
            bShift = (mods & SHIFT) != 0;
            bAlt   = (mods & ALT)   != 0;
            bCtrl  = (mods & CTRL)  != 0;
        }
        return new KeyStroke( cacheKey , bCtrl, bAlt, bShift);
    }

    @Override
    public boolean isCompleteMatch(List<Character> cur) {
        return cacheSeq == cur; // assume that matches() was checked before
    }

    @Override
    public boolean matches(List<Character> cur) {
        State state = State.START; cacheSeq = null;
        int num1 = 0, num2 = 0; char last = '\0';

        for (char ch : cur) {
            switch (state) {
            case START:
                if (ch != ESC_CODE) {
                    return false;
                }
                state = State.INTRO;
                continue;
            case INTRO:
                // Accept a second Escape to mean "Alt is pressed"?
                // according to local terminfo database, no terminal
                // seems to send it that way, but just in case...
                //if (ch == ESC_CODE && num2 == 0) {
                //    num2 = 1 + ALT; continue;
                //}

                // Key sequences supported by this class must
                //    start either with Esc-[ or Esc-O
                if (ch != '[' && ch != 'O') {
                    return false;
                }
                state = State.NUM1;
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
                return false;
            }
        }
        if (state == State.DONE) {
            if (last == '~' && stdMap.containsKey(num1)) {
                cacheSeq = cur; cacheMods = num2;
                cacheKey = stdMap.get(num1);
            } else if (finMap.containsKey(last)) {
                cacheSeq = cur; cacheMods = num2;
                cacheKey = finMap.get(last);
            } else {
                return false; // unknown key.
            }
        }
        return true;
    }
}
