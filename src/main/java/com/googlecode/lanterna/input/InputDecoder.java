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

import com.googlecode.lanterna.terminal.TerminalPosition;
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

    public InputDecoder(final Reader source) {
        this.source = source;
        this.bytePatterns = new HashSet<CharacterPattern>();
        this.currentMatching = new ArrayList<Character>();
        this.lastReportedTerminalPosition = null;
    }

    public InputDecoder(final Reader source, final KeyMappingProfile profile) {
        this(source);
        addProfile(profile);
    }

    public void addProfile(KeyMappingProfile profile) {
        for (CharacterPattern pattern : profile.getPatterns()) {
            bytePatterns.add(pattern);
        }
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public Key getNextCharacter() throws IOException {
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

        Key bestMatch = null;
        int nrOfCharactersMatched = 0;

        //Slowly iterate by adding characters, until either the buffer is empty or no pattern matches
        for(int i = 0; i < currentMatching.size(); i++) {
            List<Character> subList = currentMatching.subList(0, i + 1);
            Matching matching = getBestMatch(subList);
            if(bestMatch != null && matching.fullMatch == null) {
                break;
            }
            else if(bestMatch == null && matching.fullMatch != null) {
                bestMatch = matching.fullMatch;
                nrOfCharactersMatched = i + 1;
            }
            else if(bestMatch == null && !matching.partialMatch) {
                break;
            }
        }

        //Did we find anything? Otherwise return null
        if(bestMatch == null) {
            return null;
        }

        if (bestMatch.getKind() == Key.Kind.CursorLocation) {
            lastReportedTerminalPosition = ScreenInfoCharacterPattern.getCursorPosition(currentMatching.subList(0, nrOfCharactersMatched));
        }

        currentMatching.subList(0, nrOfCharactersMatched).clear();
        return bestMatch;
    }

    public TerminalPosition getLastReportedTerminalPosition() {
        return lastReportedTerminalPosition;
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
    }
}
