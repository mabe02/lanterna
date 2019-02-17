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

import com.googlecode.lanterna.TerminalPosition;

import java.util.List;

/**
 * Pattern used to detect Xterm-protocol mouse events coming in on the standard input channel
 * Created by martin on 19/07/15.
 * 
 * @author Martin, Andreas
 */
public class MouseCharacterPattern implements CharacterPattern {
    private static final char[] PATTERN = { KeyDecodingProfile.ESC_CODE, '[', 'M' };

    @Override
    public Matching match(List<Character> seq) {
        int size = seq.size();
        if (size > 6) {
            return null; // nope
        }
        // check first 3 chars:
        for (int i = 0; i < 3; i++) {
            if ( i >= size ) {
                return Matching.NOT_YET; // maybe later
            }
            if ( seq.get(i) != PATTERN[i] ) {
                return null; // nope
            }
        }
        if (size < 6) {
            return Matching.NOT_YET; // maybe later
        }
        MouseActionType actionType = null;
        int button = (seq.get(3) & 0x3) + 1;
        if(button == 4) {
            //If last two bits are both set, it means button click release
            button = 0;
        }
        int actionCode = (seq.get(3) & 0x60) >> 5;
        switch(actionCode) {
            case(1):
                if(button > 0) {
                    actionType = MouseActionType.CLICK_DOWN;
                }
                else {
                    actionType = MouseActionType.CLICK_RELEASE;
                }
                break;
            case(2): case(0):
                if(button == 0) {
                    actionType = MouseActionType.MOVE;
                }
                else {
                    actionType = MouseActionType.DRAG;
                }
                break;
            case(3):
                if(button == 1) {
                    actionType = MouseActionType.SCROLL_UP;
                    button = 4;
                }
                else {
                    actionType = MouseActionType.SCROLL_DOWN;
                    button = 5;
                }
                break;
        }
        TerminalPosition pos = new TerminalPosition( seq.get(4) - 33, seq.get(5) - 33 );

        MouseAction ma = new MouseAction(actionType, button, pos );
        return new Matching( ma ); // yep
    }
}
