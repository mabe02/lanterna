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

import java.util.List;

/**
 * Used to compare a list of character if they match a particular pattern, and in that case, return the kind of 
 * keystroke this pattern represents
 *
 * @author Martin, Andreas
 */
@SuppressWarnings("WeakerAccess")
public interface CharacterPattern {

    /**
     * Given a list of characters, determine whether it exactly matches
     * any known KeyStroke, and whether a longer sequence can possibly match.
     * @param seq of characters to check
     * @return see {@code Matching}
     */
    Matching match(List<Character> seq);

    /**
     * This immutable class describes a matching result. It wraps two items,
     * partialMatch and fullMatch.
     * <dl>
     * <dt>fullMatch</dt><dd>
     *   The resulting KeyStroke if the pattern matched, otherwise null.<br>
     *     Example: if the tested sequence is {@code Esc [ A}, and if the
     *      pattern recognized this as {@code ArrowUp}, then this field has
     *      a value like {@code new KeyStroke(KeyType.ArrowUp)}</dd>
     * <dt>partialMatch</dt><dd>
     *   {@code true}, if appending appropriate characters at the end of the 
     *      sequence <i>can</i> produce a match.<br>
     *     Example: if the tested sequence is "Esc [", and the Pattern would match
     *      "Esc [ A", then this field would be set to {@code true}.</dd>
     * </dl>
     * In principle, a sequence can match one KeyStroke, but also say that if 
     * another character is available, then a different KeyStroke might result.
     * This can happen, if (e.g.) a single CharacterPattern-instance matches
     * both the Escape key and a longer Escape-sequence.
     */
    class Matching {
        public final KeyStroke fullMatch;
        public final boolean partialMatch;
        
        /**
         * Re-usable result for "not yet" half-matches
         */
        public static final Matching NOT_YET = new Matching( true, null );

        /**
         * Convenience constructor for exact matches
         * 
         * @param fullMatch  the KeyStroke that matched the sequence
         */
        public Matching(KeyStroke fullMatch) {
            this(false,fullMatch);
        }
        /**
         * General constructor<p>
         * For mismatches rather use {@code null} and for "not yet" matches use NOT_YET.
         * Use this constructor, where a sequence may yield both fullMatch and
         * partialMatch or for merging result Matchings of multiple patterns.
         * 
         * @param partialMatch  true if further characters could lead to a match
         * @param fullMatch     The perfectly matching KeyStroke
         */
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
