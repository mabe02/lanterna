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

package org.lantern.screen;

import org.lantern.terminal.Terminal;

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
    private final boolean negative;

    ScreenCharacter(final char character, final Terminal.Color foregroundColor, final Terminal.Color backgroundColor, boolean bold) {
        this(character, foregroundColor, backgroundColor, bold, false, false);
    }

    ScreenCharacter(final char character, Terminal.Color foregroundColor, Terminal.Color backgroundColor,
            final boolean bold, final boolean underline, final boolean negative) {
        if(foregroundColor == null)
            foregroundColor = Terminal.Color.DEFAULT;
        if(backgroundColor == null)
            backgroundColor = Terminal.Color.DEFAULT;
        
        this.character = character;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.bold = bold;
        this.underline = underline;
        this.negative = negative;
    }

    ScreenCharacter(final ScreenCharacter character) {
        this(character.getCharacter(), character.getForegroundColor(), character.getBackgroundColor(),
                character.isBold(), character.isUnderline(), character.isNegative());
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
        return negative;
    }

    boolean isUnderline() {
        return underline;
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
                isUnderline() == other.isUnderline();
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
