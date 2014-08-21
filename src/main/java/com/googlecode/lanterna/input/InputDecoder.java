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
    private boolean seenEOF;

    public InputDecoder(final Reader source)
    {
        this.source = source;
        this.inputBuffer = new LinkedList<Character>();
        this.leftOverQueue = new LinkedList<Character>();
        this.bytePatterns = new HashSet<CharacterPattern>();
        this.currentMatching = new ArrayList<Character>();
        this.lastReportedTerminalPosition = null;
        this.seenEOF = false;
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
        if(System.getProperty("com.googlecode.lanterna.input.enable-new-decoder", "false").equals("true")) {
            try {
                return getNextCharacter2();
            }
            catch(IOException e) {
                throw new LanternaException(e);
            }
        }
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
                if(readChar == -1) {
                    seenEOF = true;
                    break;
                }

                inputBuffer.add((char)readChar);
            }
        }
        catch(IOException e) {
            throw new LanternaException(e);
        }

        if(inputBuffer.size() == 0) {
            if(seenEOF && System.getProperty("com.googlecode.lanterna.input.enable-eof", "false").equals("true")) {
                return new Key(Key.Kind.EOF);
            }
            return null;
        }

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

    private Key getNextCharacter2() throws IOException {
        while (source.ready()) {
            int readChar = source.read();
            if (readChar == -1) {
                return null;
            }
            currentMatching.add((char) readChar);
        }

        //Return null if we don't have anything from the input buffer (nothing was pressed?)
        if (currentMatching.isEmpty()) {
            return null;
        }
        else if(currentMatching.size() == 1 && currentMatching.get(0) == 0x1b) {
            currentMatching.clear();
            return new Key(Key.Kind.Escape);
        }

        Key bestMatch = null;
        int nrOfCharactersMatched = 0;

        //Slowly iterate by adding characters, until either the buffer is empty or no pattern matches
        for(int i = 0; i < currentMatching.size(); i++) {
            List<Character> subList = currentMatching.subList(0, i + 1);
            Matching matching = getBestMatch(subList);
            if(bestMatch != null && matching.fullMatch == null && !matching.partialMatch) {
                break;
            }
            else if(matching.fullMatch != null) {
                bestMatch = matching.fullMatch;
                nrOfCharactersMatched = i + 1;
            }
            else if(bestMatch == null && !matching.partialMatch) {
                //No match, not even a partial match, then clear the input buffer, otherwise we'll never ever match anything
                subList.clear();
                break;
            }
        }

        //Did we find anything? Otherwise return null
        if(bestMatch == null) {
            return null;
        }

        if (bestMatch.getKind() == Key.Kind.CursorLocation) {
            TerminalPosition cursorPosition = ScreenInfoCharacterPattern.getCursorPosition(currentMatching.subList(0, nrOfCharactersMatched));
            if(cursorPosition.getColumn() == 5 && cursorPosition.getRow() == 1) {
                //Special case for CTRL + F3
                bestMatch = new Key(Key.Kind.F3, true, false);
            }
            else {
                lastReportedTerminalPosition = cursorPosition;
            }
        }

        currentMatching.subList(0, nrOfCharactersMatched).clear();
        return bestMatch;
    }
    
    private Matching getBestMatch(List<Character> characterSequence) {
        boolean partialMatch = false;
        Key bestMatch = null;
        LinkedList<CharacterPattern> candidates = new LinkedList<CharacterPattern>(bytePatterns);
        for(int i = 0; i < characterSequence.size(); i++) {
            Iterator<CharacterPattern> iterator = candidates.iterator();
            while (iterator.hasNext()) {
                CharacterPattern pattern = iterator.next();
                if (!pattern.matches(characterSequence)) {
                    iterator.remove();
                    continue;
                }
                partialMatch = true;
                if (pattern.isCompleteMatch(characterSequence)) {
                    bestMatch = pattern.getResult(characterSequence);
                }
            }
        }
        return new Matching(partialMatch, bestMatch);
    }
    
    private static class Matching {
        boolean partialMatch;
        Key fullMatch;

        public Matching(boolean partialMatch, Key fullMatch) {
            this.partialMatch = partialMatch;
            this.fullMatch = fullMatch;
        }

        @Override
        public String toString() {
            return "Matching{" + "partialMatch=" + partialMatch + ", fullMatch=" + fullMatch + '}';
        }
    }
}
