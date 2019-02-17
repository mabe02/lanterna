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

/**
 * This class recognizes character combinations which are actually a cursor position report. See
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia</a>'s article on ANSI escape codes for more
 * information about how cursor position reporting works ("DSR – Device Status Report").
 *
 * @author Martin, Andreas
 */
public class ScreenInfoCharacterPattern extends EscapeSequenceCharacterPattern {
    public ScreenInfoCharacterPattern() {
        useEscEsc = false; // stdMap and finMap don't matter here.
    }
    protected KeyStroke getKeyStrokeRaw(char first,int num1,int num2,char last,boolean bEsc) {
        if (first != '[' || last != 'R' || num1 == 0 || num2 == 0 || bEsc) {
            return null; // nope
        }
        if (num1 == 1 && num2 <= 8) {
            return null; // nope: much more likely it's an F3 with modifiers
        }
        TerminalPosition pos = new TerminalPosition(num2, num1);
        return new ScreenInfoAction(pos); // yep
    }

    public static ScreenInfoAction tryToAdopt(KeyStroke ks) {
        if(ks == null) {
            return null;
        }
        switch (ks.getKeyType()) {
        case CursorLocation: return (ScreenInfoAction)ks;
        case F3: // reconstruct position from F3's modifiers.
            int col = 1 + (ks.isAltDown()  ? ALT  : 0)
                        + (ks.isCtrlDown() ? CTRL : 0)
                        + (ks.isShiftDown()? SHIFT: 0);
            TerminalPosition pos = new TerminalPosition(col,1);
            return new ScreenInfoAction(pos);
        default:  return null;
        }
    }


}
