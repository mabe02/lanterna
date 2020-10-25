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
package com.googlecode.lanterna;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single character with additional metadata such as colors and modifiers. This class is immutable and
 * cannot be modified after creation.
 * @author Martin
 */
class ComplexTextCharacter extends AbstractTextCharacter {

    public static TextCharacter[] fromCharacter(char c) {
        return fromString(Character.toString(c));
    }

    public static TextCharacter[] fromString(String string) {
        return fromString(string, TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
    }

    public static TextCharacter[] fromCharacter(char c, TextColor foregroundColor, TextColor backgroundColor, SGR... modifiers) {
        return fromString(Character.toString(c), foregroundColor, backgroundColor, modifiers);
    }

    public static TextCharacter[] fromString(
            String string,
            TextColor foregroundColor,
            TextColor backgroundColor,
            SGR... modifiers) {
        return fromString(string, foregroundColor, backgroundColor, toEnumSet(modifiers));
    }

    public static TextCharacter[] fromString(
            String string,
            TextColor foregroundColor,
            TextColor backgroundColor,
            EnumSet<SGR> modifiers) {

        BreakIterator breakIterator = BreakIterator.getCharacterInstance();
        breakIterator.setText(string);
        List<TextCharacter> result = new ArrayList<>();
        for (int begin = 0, end = 0; (end = breakIterator.next()) != BreakIterator.DONE; begin = breakIterator.current()) {
            result.add(new ComplexTextCharacter(string.substring(begin, end), foregroundColor, backgroundColor, modifiers));
        }
        return result.toArray(new TextCharacter[0]);
    }

    private final String character;


    /**
     * Creates a new {@code ScreenCharacter} based on a physical character, color information and a set of modifiers.
     * @param character Physical character to refer to
     * @param foregroundColor Foreground color the character has
     * @param backgroundColor Background color the character has
     * @param modifiers Set of modifiers to apply when drawing the character
     */
    ComplexTextCharacter(
            String character,
            TextColor foregroundColor,
            TextColor backgroundColor,
            EnumSet<SGR> modifiers) {
        super(foregroundColor, backgroundColor, modifiers);

        if (character.isEmpty()) {
            throw new IllegalArgumentException("Cannot create TextCharacter from an empty string");
        }
        validateSingleCharacter(character);

        this.character = character;
        char firstCharacter = character.charAt(0);

        // Don't allow creating a TextCharacter containing a control character
        // For backward-compatibility, do allow tab for now
        if(TerminalTextUtils.isControlCharacter(firstCharacter) && firstCharacter != '\t') {
            throw new IllegalArgumentException("Cannot create a TextCharacter from a control character (0x" + Integer.toHexString(firstCharacter) + ")");
        }
    }

    private void validateSingleCharacter(String character) {
        BreakIterator breakIterator = BreakIterator.getCharacterInstance();
        breakIterator.setText(character);
        String firstCharacter = null;
        for (int begin = 0, end = 0; (end = breakIterator.next()) != BreakIterator.DONE; begin = breakIterator.current()) {
            if (firstCharacter == null) {
                firstCharacter = character.substring(begin, end);
            }
            else {
                throw new IllegalArgumentException("Invalid String for TextCharacter, can only have one logical character");
            }
        }
    }

    @Override
    public boolean is(char otherCharacter) {
        return otherCharacter == character.charAt(0) && character.length() == 1;
    }

    /**
     * The actual character this TextCharacter represents
     * @return character of the TextCharacter
     * @deprecated This won't work with advanced characters like emoji
     */
    @Deprecated
    public char getCharacter() {
        return character.charAt(0);
    }

    /**
     * Returns the character this TextCharacter represents as a String. This is not returning a char
     * @return
     */
    public String getCharacterString() {
        return character;
    }

    @Override
    @SuppressWarnings("SameParameterValue")
    public TextCharacter withCharacter(char character) {
        if(this.character.equals(Character.toString(character))) {
            return this;
        }
        return new TextCharacter(character, foregroundColor, backgroundColor, modifiers);
    }

    @Override
    public TextCharacter withForegroundColor(TextColor foregroundColor) {
        if(this.foregroundColor == foregroundColor || this.foregroundColor.equals(foregroundColor)) {
            return this;
        }
        return new TextCharacter(character, foregroundColor, backgroundColor, modifiers);
    }

    @Override
    public TextCharacter withBackgroundColor(TextColor backgroundColor) {
        if(this.backgroundColor == backgroundColor || this.backgroundColor.equals(backgroundColor)) {
            return this;
        }
        return new TextCharacter(character, foregroundColor, backgroundColor, modifiers);
    }

    @Override
    public TextCharacter withModifiers(Collection<SGR> modifiers) {
        EnumSet<SGR> newSet = EnumSet.copyOf(modifiers);
        if(modifiers.equals(newSet)) {
            return this;
        }
        return new TextCharacter(character, foregroundColor, backgroundColor, newSet);
    }

    @Override
    public TextCharacter withModifier(SGR modifier) {
        if(modifiers.contains(modifier)) {
            return this;
        }
        EnumSet<SGR> newSet = EnumSet.copyOf(this.modifiers);
        newSet.add(modifier);
        return new TextCharacter(character, foregroundColor, backgroundColor, newSet);
    }

    @Override
    public TextCharacter withoutModifier(SGR modifier) {
        if(!modifiers.contains(modifier)) {
            return this;
        }
        EnumSet<SGR> newSet = EnumSet.copyOf(this.modifiers);
        newSet.remove(modifier);
        return new TextCharacter(character, foregroundColor, backgroundColor, newSet);
    }

    @Override
    public boolean isDoubleWidth() {
        // TODO: make this better to work properly with emoji and other complicated "characters"
        return TerminalTextUtils.isCharDoubleWidth(character.charAt(0)) ||
                // If the character takes up more than one char, assume it's double width (unless thai)
                (character.length() > 1 && !TerminalTextUtils.isCharThai(character.charAt(0)));
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final ComplexTextCharacter other = (ComplexTextCharacter) obj;
        if(!Objects.equals(this.character, other.character)) {
            return false;
        }
        if(!Objects.equals(this.getForegroundColor(), other.getForegroundColor())) {
            return false;
        }
        if(!Objects.equals(this.getBackgroundColor(), other.getBackgroundColor())) {
            return false;
        }
        return Objects.equals(this.getModifiers(), other.getModifiers());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.character.hashCode();
        hash = 37 * hash + (this.getForegroundColor() != null ? this.getForegroundColor().hashCode() : 0);
        hash = 37 * hash + (this.getBackgroundColor() != null ? this.getBackgroundColor().hashCode() : 0);
        hash = 37 * hash + (this.getModifiers() != null ? this.getModifiers().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "TextCharacter{" + "character=" + character + ", foregroundColor=" + getForegroundColor() + ", backgroundColor=" + getBackgroundColor() + ", modifiers=" + getModifiers() + '}';
    }
}
