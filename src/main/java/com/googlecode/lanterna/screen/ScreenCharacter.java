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

package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.terminal.Terminal;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author martin
 */
class ScreenCharacter
{
    private final char character;
    private final Terminal.Color foregroundColor;
    private final Terminal.Color backgroundColor;
    private final boolean bold;
    private final boolean underline;
    private final boolean reverse;
    private final boolean blinking;
    
    ScreenCharacter(char character) {
        this(character, Terminal.Color.DEFAULT, Terminal.Color.DEFAULT);
    }
    
    ScreenCharacter(
            char character, 
            Terminal.Color foregroundColor, 
            Terminal.Color backgroundColor) {
        this(character, foregroundColor, backgroundColor, EnumSet.noneOf(ScreenCharacterStyle.class));
    }
    
    /**
     * Warning, this class has another independent constructor too! If you change 
     * this constructor, please check the other one to make sure you're not missing 
     * anything!
     */
    ScreenCharacter(
            char character, 
            Terminal.Color foregroundColor, 
            Terminal.Color backgroundColor,
            Set<ScreenCharacterStyle> style) {
    	if(foregroundColor == null)
            foregroundColor = Terminal.Color.DEFAULT;
        if(backgroundColor == null)
            backgroundColor = Terminal.Color.DEFAULT;
        
        this.character = character;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.bold = style.contains(ScreenCharacterStyle.Bold);
        this.underline = style.contains(ScreenCharacterStyle.Underline);
        this.reverse = style.contains(ScreenCharacterStyle.Reverse);
        this.blinking = style.contains(ScreenCharacterStyle.Blinking);
    }

    /**
     * Warning, this class has another independent constructor too! If you change 
     * this constructor, please check the other one to make sure you're not missing 
     * anything!
     */
    ScreenCharacter(final ScreenCharacter character) {
        this.character = character.character;
        this.foregroundColor = character.foregroundColor;
        this.backgroundColor = character.backgroundColor;
        this.bold = character.bold;
        this.underline = character.underline;
        this.reverse = character.reverse;
        this.blinking = character.blinking;
    }

    char getCharacter() {
        return character;
    }

    Terminal.Color getBackgroundColor() {
        return backgroundColor;
    }

    boolean isBold() {
        return bold;
    }

    Terminal.Color getForegroundColor() {
        return foregroundColor;
    }

    boolean isNegative() {
        return reverse;
    }

    boolean isUnderline() {
        return underline;
    }
    
    boolean isBlinking() {
    	return blinking;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ScreenCharacter == false)
            return false;

        ScreenCharacter other = ((ScreenCharacter)(obj));
        return character == other.getCharacter() &&
                getForegroundColor() == other.getForegroundColor() &&
                getBackgroundColor() == other.getBackgroundColor() &&
                isBold() == other.isBold() &&
                isNegative() == other.isNegative() &&
                isUnderline() == other.isUnderline() &&
                isBlinking() == other.isBlinking();
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
