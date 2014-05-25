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
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TextColor;
import java.util.EnumSet;

/**
 *
 * @author martin
 */
class TerminalCharacter {
    static final TerminalCharacter DEFAULT_CHARACTER = new TerminalCharacter(' ', TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT, EnumSet.noneOf(Terminal.SGR.class));

    private final char character;
    private final TextColor foregroundColor;
    private final TextColor backgroundColor;
    private final EnumSet<Terminal.SGR> styles;

    public TerminalCharacter(char character, TextColor foregroundColor, TextColor backgroundColor, EnumSet<Terminal.SGR> styles) {
        this.character = character;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.styles = EnumSet.copyOf(styles);
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

    boolean isBold() {
        return styles.contains(Terminal.SGR.BOLD);
    }

    boolean isReverse() {
        return styles.contains(Terminal.SGR.REVERSE);
    }

    boolean isBlink() {
        return styles.contains(Terminal.SGR.BLINK);
    }

}
