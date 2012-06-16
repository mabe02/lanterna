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

package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.input.InputDecoder;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.KeyMappingProfile;

/**
 *
 * @author Martin
 */
public abstract class InputEnabledAbstractTerminal extends AbstractTerminal implements InputProvider {
    protected final InputDecoder inputDecoder;

    public InputEnabledAbstractTerminal(InputDecoder inputDecoder) {
        this.inputDecoder = inputDecoder;
    }
    
    public void addInputProfile(KeyMappingProfile profile) {
        inputDecoder.addProfile(profile);
    }

    public Key readInput() {
        Key key = inputDecoder.getNextCharacter();
        if (key != null && key.getKind() == Key.Kind.CursorLocation) {
            TerminalPosition reportedTerminalPosition = inputDecoder.getLastReportedTerminalPosition();
            if (reportedTerminalPosition != null)
                onResized(reportedTerminalPosition.getColumn(), reportedTerminalPosition.getRow());
            
            return readInput();
        } else {
            return key;
        }
    }    
}
