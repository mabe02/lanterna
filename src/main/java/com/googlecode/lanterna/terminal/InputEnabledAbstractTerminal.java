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

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.InputDecoder;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.KeyMappingProfile;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is an abstract terminal that can also read input events (keys), with a
 * default implementation of the methods from {@code InputProvider}.
 * @author Martin
 */
public abstract class InputEnabledAbstractTerminal extends AbstractTerminal implements InputProvider {
    private final InputDecoder inputDecoder;
    private final Queue<Key> keyQueue;
    private final Object readMutex;

    public InputEnabledAbstractTerminal(InputDecoder inputDecoder) {
        this.inputDecoder = inputDecoder;
        this.keyQueue = new LinkedList<Key>();
        this.readMutex = new Object();
    }
    
    @Override
    public void addInputProfile(KeyMappingProfile profile) {
        inputDecoder.addProfile(profile);
    }
    
    protected TerminalSize waitForTerminalSizeReport(int timeoutMs) {
        long startTime = System.currentTimeMillis();
        synchronized(readMutex) {
            while(System.currentTimeMillis() - startTime < timeoutMs) {
                Key key = inputDecoder.getNextCharacter();
                if(key == null) {
                    try {
                        Thread.sleep(1);
                    }
                    catch(InterruptedException e) {}
                    continue;
                }
                
                if(key.getKind() != Key.Kind.CursorLocation) {
                    keyQueue.add(key);
                }
                else {
                    TerminalPosition reportedTerminalPosition = inputDecoder.getLastReportedTerminalPosition();
                    if(reportedTerminalPosition != null)
                        onResized(reportedTerminalPosition.getColumn(), reportedTerminalPosition.getRow());
                    else
                        throw new LanternaException(new IOException("Unexpected: inputDecoder.getLastReportedTerminalPosition() "
                                + "returned null after position was reported"));
                    return new TerminalSize(reportedTerminalPosition.getColumn(), reportedTerminalPosition.getRow());
                }                    
            }
        }
        throw new LanternaException(
                new IOException(
                    "Timeout while waiting for terminal size report! "
                    + "Maybe your terminal doesn't support cursor position report, please "
                    + "consider using a custom size querier"));
    }

    @Override
    public Key readInput() {
        synchronized(readMutex) {
            if(!keyQueue.isEmpty())
                return keyQueue.poll();
            
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
}
