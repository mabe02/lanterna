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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.terminal.virtual;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by martin on 26/03/16.
 */
public interface VirtualTerminal extends IOSafeTerminal {
    void setTerminalSize(TerminalSize newSize);

    void addVirtualTerminalListener(VirtualTerminalListener listener);

    void removeVirtualTerminalListener(VirtualTerminalListener listener);

    void setBacklogSize(int backlogSize);

    boolean isCursorVisible();

    void addInput(KeyStroke keyStroke);

    TerminalPosition getCursorBufferPosition();

    TextCharacter getBufferCharacter(TerminalPosition position);

    TextCharacter getBufferCharacter(int column, int row);

    TextCharacter getCharacter(TerminalPosition position);

    TextCharacter getCharacter(int column, int row);

    int getBufferLineCount();

    void forEachLine(int startRow, int endRow, BufferWalker bufferWalker);

    interface BufferLine {
        TextCharacter getCharacterAt(int column);
    }

    interface BufferWalker {
        void drawLine(int rowNumber, BufferLine bufferLine);
    }
}
