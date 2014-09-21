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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.input;

import com.googlecode.lanterna.TerminalPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Used to read the input stream character by character and generate {@code Key} objects to be put in the input queue.
 *
 * @author Martin
 */
public class InputDecoder {
    private final Reader source;
    private final Set<CharacterPattern> bytePatterns;
    private final List<Character> currentMatching;
    private TerminalPosition lastReportedTerminalPosition;
    private boolean seenEOF;

    public InputDecoder(final Reader source) {
        this.source = new BufferedReader(source);
        this.bytePatterns = new HashSet<CharacterPattern>();
        this.currentMatching = new ArrayList<Character>();
        this.lastReportedTerminalPosition = null;
        this.seenEOF = false;
    }

    public void addProfile(KeyDecodingProfile profile) {
        for (CharacterPattern pattern : profile.getPatterns()) {
            bytePatterns.add(pattern);
        }
    }

    /**
     * Reads and decodes the next key stroke from the input stream
     * @return Key stroke read from the input stream, or {@code null} if none
     * @throws IOException If there was an I/O error when reading from the input stream
     */
    public KeyStroke getNextCharacter(boolean blockingIO) throws IOException {
        while ((blockingIO && currentMatching.isEmpty()) || source.ready()) {
            int readChar = source.read();
            if (readChar == -1) {
                seenEOF = true;
                if(currentMatching.isEmpty()) {
                    return new KeyStroke(KeyType.EOF);
                }
                break;
            }
            currentMatching.add((char) readChar);
        }

        //Return null if we don't have anything from the input buffer (nothing was pressed?)
        if (currentMatching.isEmpty()) {
            return null;
        }

        KeyStroke bestMatch = null;
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
            if(seenEOF) {
                return new KeyStroke(KeyType.EOF);
            }
            return null;
        }

        if (bestMatch.getKeyType() == KeyType.CursorLocation) {
            TerminalPosition cursorPosition = ScreenInfoCharacterPattern.getCursorPosition(currentMatching.subList(0, nrOfCharactersMatched));
            if(cursorPosition.getColumn() == 5 && cursorPosition.getRow() == 1) {
                //Special case for CTRL + F3
                bestMatch = new KeyStroke(KeyType.F3, true, false);
            }
            else {
                lastReportedTerminalPosition = cursorPosition;
            }
        }

        currentMatching.subList(0, nrOfCharactersMatched).clear();
        return bestMatch;
    }

    public TerminalPosition getLastReportedTerminalPosition() {
        return lastReportedTerminalPosition;
    }

    private Matching getBestMatch(List<Character> characterSequence) {
        boolean partialMatch = false;
        KeyStroke bestMatch = null;
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
        final boolean partialMatch;
        final KeyStroke fullMatch;

        public Matching(boolean partialMatch, KeyStroke fullMatch) {
            this.partialMatch = partialMatch;
            this.fullMatch = fullMatch;
        }

        @Override
        public String toString() {
            return "Matching{" + "partialMatch=" + partialMatch + ", fullMatch=" + fullMatch + '}';
        }
    }
}
