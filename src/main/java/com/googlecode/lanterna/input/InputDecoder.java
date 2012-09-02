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

package com.googlecode.lanterna.input;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.terminal.TerminalPosition;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Used to read the input stream character by character and generate {@code Key}
 * objects to be put in the input queue.
 * @author Martin
 */
public class InputDecoder
{
    private final Reader source;
    private final Queue<Character> inputBuffer;
    private final Queue<Character> leftOverQueue;
    private final Set<CharacterPattern> bytePatterns;
    private final List<Character> currentMatching;
    private TerminalPosition lastReportedTerminalPosition;

    public InputDecoder(final Reader source)
    {
        this.source = source;
        this.inputBuffer = new LinkedList<Character>();
        this.leftOverQueue = new LinkedList<Character>();
        this.bytePatterns = new HashSet<CharacterPattern>();
        this.currentMatching = new ArrayList<Character>();
        this.lastReportedTerminalPosition = null;
    }

    public InputDecoder(final Reader source, final KeyMappingProfile profile)
    {
        this(source);
        addProfile(profile);
    }

    public void addProfile(KeyMappingProfile profile)
    {
        for(CharacterPattern pattern: profile.getPatterns())
            bytePatterns.add(pattern);
    }

    public Key getNextCharacter()
    {
        if(leftOverQueue.size() > 0) {
            Character first = leftOverQueue.poll();
            //HACK!!!
            if(first == 0x1b)
                return new Key(Key.Kind.Escape);
            
            return new Key(first.charValue());
        }

        try {
            while(source.ready()) {
                int readChar = source.read();
                if(readChar == -1)
                    return null;

                inputBuffer.add((char)readChar);
            }
        }
        catch(IOException e) {
            throw new LanternaException(e);
        }

        if(inputBuffer.size() == 0)
                return null;

        while(true) {
            //Check all patterns
            Character nextChar = inputBuffer.poll();
            boolean canMatchWithOneMoreChar = false;

            if(nextChar != null) {
                currentMatching.add(nextChar);
                for(CharacterPattern pattern: bytePatterns) {
                    if(pattern.matches(currentMatching)) {
                        if(pattern.isCompleteMatch(currentMatching)) {
                            Key result = pattern.getResult(currentMatching);
                            if(result.getKind() == Key.Kind.CursorLocation)
                                lastReportedTerminalPosition = ScreenInfoCharacterPattern.getCursorPosition(currentMatching);
                            currentMatching.clear();
                            return result;
                        }
                        else
                            canMatchWithOneMoreChar = true;
                    }
                }
            }
            if(!canMatchWithOneMoreChar) {
                for(Character c: currentMatching)
                    leftOverQueue.add(c);
                currentMatching.clear();
                Character first = leftOverQueue.poll();
                
                //HACK!!!
                if(first == 0x1b)
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
