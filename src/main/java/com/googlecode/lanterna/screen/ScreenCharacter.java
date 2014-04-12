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
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TextColor;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Represents a character inside a {@code Screen}, along with various modifier and color information. This class is 
 * typically used when manipulating the screen's contentor when reading out from one of the buffers in the screen.
 * @author martin
 */
public class ScreenCharacter {    
    private static EnumSet<Terminal.SGR> toEnumSet(Terminal.SGR... modifiers) {
        EnumSet<Terminal.SGR> set = EnumSet.noneOf(Terminal.SGR.class);
        //Now assign the modifiers; we can't pass them in using EnumSet.copyOf(..) since that throws is the list is empty
        set.addAll(Arrays.asList(modifiers));
        return set;
    }

    public static final ScreenCharacter CJK_PADDING_CHARACTER = new ScreenCharacter('\0');

    private final char character;
    private final TextColor foregroundColor;
    private final TextColor backgroundColor;
    private final EnumSet<Terminal.SGR> modifiers;  //This isn't immutable, but we should treat it as such!

    /**
     * Creates a {@code ScreenCharacter} based on a supplied character, with default colors and no extra modifiers.
     * @param character Physical character to use
     */
    public ScreenCharacter(char character) {
        this(character, TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
    }
    
    /**
     * Copies another {@code ScreenCharacter}
     * @param character screenCharacter to copy from
     */
    public ScreenCharacter(ScreenCharacter character) {
        this(character.getCharacter(), 
                character.getForegroundColor(), 
                character.getBackgroundColor(), 
                character.getModifiers().toArray(new Terminal.SGR[0]));
    }

    /**
     * Creates a new {@code ScreenCharacter} based on a physical character, color information and optional modifiers.
     * @param character Physical character to refer to
     * @param foregroundColor Foreground color the character has
     * @param backgroundColor Background color the character has
     * @param styles Optional list of modifiers to apply when drawing the character
     */
    public ScreenCharacter(
            char character,
            TextColor foregroundColor,
            TextColor backgroundColor,
            Terminal.SGR... styles) {
        
        this(character, 
                foregroundColor, 
                backgroundColor, 
                toEnumSet(styles));
    }
        
    ScreenCharacter(
            char character,
            TextColor foregroundColor,
            TextColor backgroundColor,
            EnumSet<Terminal.SGR> modifiers) {
        
        if(foregroundColor == null) {
            foregroundColor = TextColor.ANSI.DEFAULT;
        }
        if(backgroundColor == null) {
            backgroundColor = TextColor.ANSI.DEFAULT;
        }

        this.character = character;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.modifiers = modifiers;
    }

    public char getCharacter() {
        return character;
    }

    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    public EnumSet<Terminal.SGR> getModifiers() {
        return EnumSet.copyOf(modifiers);
    }

    public boolean isBold() {
        return modifiers.contains(Terminal.SGR.BOLD);
    }

    public boolean isReversed() {
        return modifiers.contains(Terminal.SGR.REVERSE);
    }

    public boolean isUnderlined() {
        return modifiers.contains(Terminal.SGR.UNDERLINE);
    }

    public boolean isBlinking() {
        return modifiers.contains(Terminal.SGR.BLINK);
    }

    public boolean isBordered() {
        return modifiers.contains(Terminal.SGR.BORDERED);
    }
    
    public ScreenCharacter withCharacter(char character) {
        if(this.character == character) {
            return this;
        }
        return new ScreenCharacter(character, foregroundColor, backgroundColor, modifiers);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final ScreenCharacter other = (ScreenCharacter) obj;
        if(this.character != other.character) {
            return false;
        }
        if(this.foregroundColor != other.foregroundColor && (this.foregroundColor == null || !this.foregroundColor.equals(other.foregroundColor))) {
            return false;
        }
        if(this.backgroundColor != other.backgroundColor && (this.backgroundColor == null || !this.backgroundColor.equals(other.backgroundColor))) {
            return false;
        }
        if(this.modifiers != other.modifiers && (this.modifiers == null || !this.modifiers.equals(other.modifiers))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.character;
        hash = 37 * hash + (this.foregroundColor != null ? this.foregroundColor.hashCode() : 0);
        hash = 37 * hash + (this.backgroundColor != null ? this.backgroundColor.hashCode() : 0);
        hash = 37 * hash + (this.modifiers != null ? this.modifiers.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ScreenCharacter{" + "character=" + character + ", foregroundColor=" + foregroundColor + ", backgroundColor=" + backgroundColor + ", modifiers=" + modifiers + '}';
    }
}
