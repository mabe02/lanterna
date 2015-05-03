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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TerminalSize;

/**
 * This TextGraphics implementation wraps another TextGraphics and forwards all operations to it, but with a few
 * differences. First of all, each individual character being printed is printed twice. Secondly, if you call
 * {@code getSize()}, it will return a size that has half the width of the underlying TextGraphics. This presents the
 * writable view as somewhat squared, since normally terminal characters are twice as tall as wide. You can see some
 * examples of how this looks by running the Triangle test in {@code com.googlecode.lanterna.screen.ScreenTriangleTest}
 * and compare it when running with the --square parameter and without.
 */
public class DoublePrintingTextGraphics extends AbstractTextGraphics {
    private final AbstractTextGraphics underlyingTextGraphics;

    public DoublePrintingTextGraphics(AbstractTextGraphics underlyingTextGraphics) {
        this.underlyingTextGraphics = underlyingTextGraphics;
    }

    @Override
    public TextGraphics setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter) {
        columnIndex = columnIndex * 2;
        underlyingTextGraphics.setCharacter(columnIndex, rowIndex, textCharacter);
        underlyingTextGraphics.setCharacter(columnIndex + 1, rowIndex, textCharacter);
        return this;
    }

    @Override
    public TerminalSize getSize() {
        TerminalSize size = underlyingTextGraphics.getSize();
        return size.withColumns(size.getColumns() / 2);
    }
}
