/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.input;

import com.googlecode.lanterna.TerminalPosition;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Pattern used to detect Xterm-protocol mouse events coming in on the standard input channel
 * Created by martin on 19/07/15.
 *
 * @author Martin, Andreas
 */
public class MouseCharacterPattern implements CharacterPattern {
    private static final char[] HEADER = { KeyDecodingProfile.ESC_CODE, '[', '<' };
    private static final Pattern pattern=Pattern.compile(".*\\<([0-9]+);([0-9]+);([0-9]+)([mM])");


    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // some terminals, for example XTerm, issue mouse down when it
    // should be mouse move, after first click then they correctly issues
    // mouse move, do some coercion here to force the correct action
    private boolean isMouseDown = false;
    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

    @Override
    public Matching match(List<Character> seq) {
        int size = seq.size();
        if (size > 15) {
            return null; // nope
        }

        // check first 3 chars:
        for (int i = 0; i < 3; i++) {
            if ( i >= (size-1) ) {
                return Matching.NOT_YET; // maybe later
            }
            if ( seq.get(i) != HEADER[i] ) {
                return null; // nope
            }
        }

        // Check if we have a number on the next position
        if(seq.get(3).hashCode() < 48 || seq.get(3).hashCode() > 57)
        {
            return null; // nope
        }

        // If the size is lower than 7 then we don't have the pattern yet for sure
        if (size < 7) {
            return Matching.NOT_YET; // maybe later
        }

        // converts the list of characters to a string
        String seqAString=seq.stream().map(e->e.toString()).collect(Collectors.joining());

        // Check if we match the regex
        Matcher matcher=pattern.matcher(seqAString);
        if(matcher.matches())
        {
            Boolean shiftDown=false;
            Boolean altDown=false;
            Boolean ctrlDown=false;

            // Get the button
            int item=Integer.valueOf(matcher.group(1));
            int button = 0;

            // if the 6th bit is set, then it's a wheel event then we check the 1st bit to know if it's up or down
            if((item & 0x40) != 0) {
                if((item & 0x1) == 0) {
                    button = 4;
                } else  {
                    button = 5;
                }
            } else if((item & 0x2) != 0) {
                button = 3;
            } else if((item & 0x1) != 0) {
                button = 1;
            } else if((item & 0x1) == 0) {
                button = 2;
            }

            // Get the modifier keys (it seems that they do not are always reported correctly depending on the terminal)
            if((item & 0x4) != 0)
            {
                shiftDown=true;
            }
            if((item & 0x8) != 0)
            {
                altDown=true;
            }
            if((item & 0x10) != 0)
            {
                ctrlDown=true;
            }

            // Get the action
            MouseActionType actionType = null;
            if(matcher.group(4).equals("M"))
            {
                actionType=MouseActionType.CLICK_DOWN;
            } else {
                actionType=MouseActionType.CLICK_RELEASE;
            }

            // Get the move and drag actions
            if((item & 0x20) != 0)
            {
                if((item & 0x3) != 0)
                {
                    // In move mode, the bits 0, 1 are set in addition to the 6th bit
                    actionType=MouseActionType.MOVE;
                    button=0;
                } else {
                    actionType=MouseActionType.DRAG;
                }
            } else {
                isMouseDown=(actionType==MouseActionType.CLICK_DOWN);
            }

            // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
            // coerce action types:
            // when in between CLICK_DOWN and CLICK_RELEASE coerce MOVE to DRAG
            // when not between CLICK_DOWN and CLICK_RELEASE coerce DRAG to MOVE
            if (isMouseDown)
            {
                if (actionType == MouseActionType.MOVE) {
                   actionType = MouseActionType.DRAG;
                }
            } else if (actionType == MouseActionType.DRAG) {
                actionType = MouseActionType.MOVE;
            }
            // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

            // Get the position
            TerminalPosition pos = new TerminalPosition( Integer.valueOf(matcher.group(2))-1, Integer.valueOf(matcher.group(3))-1 );

            MouseAction ma = new MouseAction(actionType, button, pos, ctrlDown, altDown, shiftDown);
            return new Matching( ma ); // yep
        } else {
            return Matching.NOT_YET; // maybe later
        }
    }
}
