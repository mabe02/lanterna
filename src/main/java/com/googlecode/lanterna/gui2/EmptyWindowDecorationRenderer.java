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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * Implementation of WindowDecorationRenderer that is doesn't render any window decorations
 * @author Martin
 */
public class EmptyWindowDecorationRenderer implements WindowDecorationRenderer {
    @Override
    public TextGUIGraphics draw(WindowBasedTextGUI textGUI, TextGUIGraphics graphics, Window window) {
        return graphics;
    }

    @Override
    public TerminalSize getDecoratedSize(Window window, TerminalSize contentAreaSize) {
        return contentAreaSize;
    }

    @Override
    public TerminalPosition getOffset(Window window) {
        return TerminalPosition.TOP_LEFT_CORNER;
    }
}
