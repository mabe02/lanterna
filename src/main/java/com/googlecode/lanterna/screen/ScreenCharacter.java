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

import java.util.EnumSet;
import java.util.Set;

import com.googlecode.lanterna.terminal.TextColor;

/**
 *
 * @author martin
 */
public class ScreenCharacter
{
    public static final ScreenCharacter CJK_PADDING_CHARACTER = new ScreenCharacter('\0');
    
    private final char character;
    private final TextColor foregroundColor;
    private final TextColor backgroundColor;
    private final boolean bold;
    private final boolean underline;
    private final boolean reverse;
    private final boolean blinking;
    private final boolean bordered;
    
    public ScreenCharacter(char character) {
        this(character, TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
    }
    
    public ScreenCharacter(
            char character, 
            TextColor foregroundColor, 
            TextColor backgroundColor) {
        this(character, foregroundColor, backgroundColor, EnumSet.noneOf(ScreenCharacterStyle.class));
    }
    
    /**
     * Warning, this class has another independent constructor too! If you change 
     * this constructor, please check the other one to make sure you're not missing 
     * anything!
     */
    public ScreenCharacter(
            char character, 
            TextColor foregroundColor, 
            TextColor backgroundColor,
            Set<ScreenCharacterStyle> style) {
    	if(foregroundColor == null) {
                    foregroundColor = TextColor.ANSI.DEFAULT;
                }
        if(backgroundColor == null) {
                    backgroundColor = TextColor.ANSI.DEFAULT;
                }
        
        this.character = character;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.bold = style.contains(ScreenCharacterStyle.Bold);
        this.underline = style.contains(ScreenCharacterStyle.Underline);
        this.reverse = style.contains(ScreenCharacterStyle.Reverse);
        this.blinking = style.contains(ScreenCharacterStyle.Blinking);
        this.bordered = style.contains(ScreenCharacterStyle.Bordered);
    }

    /**
     * Warning, this class has another independent constructor too! If you change 
     * this constructor, please check the other one to make sure you're not missing 
     * anything!
     */
    public ScreenCharacter(final ScreenCharacter character) {
        this.character = character.character;
        this.foregroundColor = character.foregroundColor;
        this.backgroundColor = character.backgroundColor;
        this.bold = character.bold;
        this.underline = character.underline;
        this.reverse = character.reverse;
        this.blinking = character.blinking;
        this.bordered = character.bordered;
    }

    public char getCharacter() {
        return character;
    }

    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isBold() {
        return bold;
    }

    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    public boolean isNegative() {
        return reverse;
    }

    public boolean isUnderline() {
        return underline;
    }
    
    public boolean isBlinking() {
    	return blinking;
    }
    
    public boolean isBordered() {
    	return bordered;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj instanceof ScreenCharacter == false) {
            return false;
        }

        ScreenCharacter other = ((ScreenCharacter)(obj));
        return character == other.getCharacter() &&
                getForegroundColor() == other.getForegroundColor() &&
                getBackgroundColor() == other.getBackgroundColor() &&
                isBold() == other.isBold() &&
                isNegative() == other.isNegative() &&
                isUnderline() == other.isUnderline() &&
                isBlinking() == other.isBlinking() &&
                isBordered() == other.isBordered();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.character;
        return hash;
    }

    @Override
    public String toString()
    {
        return Character.toString(character);
    }
}
