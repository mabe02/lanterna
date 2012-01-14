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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.input;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.lantern.LanternException;
import org.lantern.terminal.TerminalPosition;

/**
 *
 * @author mabe02
 */
public class InputDecoder
{
    private final Reader source;
    private final LinkedList inputBuffer;
    private final LinkedList leftOverQueue;
    private final Set bytePatterns;
    private final List currentMatching;
    private TerminalPosition lastReportedTerminalPosition;

    public InputDecoder(final Reader source)
    {
        this.source = source;
        this.inputBuffer = new LinkedList();
        this.leftOverQueue = new LinkedList();
        this.bytePatterns = new HashSet();
        this.currentMatching = new ArrayList();
        this.lastReportedTerminalPosition = null;
    }

    public InputDecoder(final Reader source, final KeyMappingProfile profile)
    {
        this(source);
        addProfile(profile);
    }

    public void addProfile(KeyMappingProfile profile)
    {
        Iterator iter = profile.getPatterns().iterator();
        while(iter.hasNext())
            bytePatterns.add(iter.next());
    }

    public Key getNextCharacter() throws LanternException
    {
        if(leftOverQueue.size() > 0) {
            Character first = (Character)leftOverQueue.removeFirst();
            //HACK!!!
            if(first.charValue() == 0x1b)
                return new Key(Key.Kind.Escape);
            
            return new Key(first.charValue());
        }

        try {
            while(source.ready()) {
                int readChar = source.read();
                if(readChar == -1)
                    return null;

                inputBuffer.add(new Character((char)readChar));
            }
        }
        catch(IOException e) {
            throw new LanternException(e);
        }

        if(inputBuffer.size() == 0)
                return null;

        while(true) {
            //Check all patterns
            Character nextChar = null;
            if(!inputBuffer.isEmpty())
                nextChar = (Character)inputBuffer.removeFirst();
            boolean canMatchWithOneMoreChar = false;

            if(nextChar != null) {
                currentMatching.add(nextChar);
                Iterator iter = bytePatterns.iterator();
                while(iter.hasNext()) {
                    CharacterPattern pattern = (CharacterPattern)iter.next();
                    if(pattern.matches(currentMatching)) {
                        if(pattern.isCompleteMatch(currentMatching)) {
                            Key result = pattern.getResult();
                            if(result.getKind() == Key.Kind.CursorLocation)
                                lastReportedTerminalPosition = ScreenInfoCharacterPattern.getCursorPosition(currentMatching);
                            currentMatching.clear();
                            return pattern.getResult();
                        }
                        else
                            canMatchWithOneMoreChar = true;
                    }
                }
            }
            if(!canMatchWithOneMoreChar) {
                for(int i = 0; i < currentMatching.size(); i++)
                    leftOverQueue.add(currentMatching.get(i));
                currentMatching.clear();
                Character first = (Character)leftOverQueue.removeFirst();
                
                //HACK!!!
                if(first.charValue() == 0x1b)
                    return new Key(Key.Kind.Escape);
                return new Key(first.charValue());
            }
        }
    }

    public TerminalPosition getLastReportedTerminalPosition()
    {
        return lastReportedTerminalPosition;
    }
}
