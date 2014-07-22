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

package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

/**
 *
 * @author Martin
 */
public class SGRTest {
    public static void main(String[] args) throws IOException {
        Terminal rawTerminal = new TestTerminalFactory(args).createTerminal();
        rawTerminal.enterPrivateMode();
        rawTerminal.clearScreen();
        rawTerminal.setForegroundColor(TextColor.ANSI.RED);
        rawTerminal.enableSGR(Terminal.SGR.BLINK);
        rawTerminal.setCursorPosition(10, 2);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.disableSGR(Terminal.SGR.BLINK);
        rawTerminal.enableSGR(Terminal.SGR.BOLD);
        rawTerminal.setCursorPosition(10, 4);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.disableSGR(Terminal.SGR.BOLD);
        rawTerminal.enableSGR(Terminal.SGR.BORDERED);
        rawTerminal.setCursorPosition(10, 6);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.disableSGR(Terminal.SGR.BORDERED);
        rawTerminal.enableSGR(Terminal.SGR.CIRCLED);
        rawTerminal.setCursorPosition(10, 8);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.disableSGR(Terminal.SGR.CIRCLED);
        rawTerminal.enableSGR(Terminal.SGR.CROSSEDOUT);
        rawTerminal.setCursorPosition(10, 10);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.disableSGR(Terminal.SGR.CROSSEDOUT);
        rawTerminal.enableSGR(Terminal.SGR.UNDERLINE);
        rawTerminal.setCursorPosition(10, 12);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.disableSGR(Terminal.SGR.UNDERLINE);
        rawTerminal.enableSGR(Terminal.SGR.FRAKTUR);
        rawTerminal.setCursorPosition(10, 14);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.disableSGR(Terminal.SGR.FRAKTUR);
        rawTerminal.enableSGR(Terminal.SGR.REVERSE);
        rawTerminal.setCursorPosition(10, 16);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.disableSGR(Terminal.SGR.REVERSE);
        rawTerminal.setCursorPosition(0, 0);
        rawTerminal.flush();
        try {
            while(rawTerminal.readInput() == null) {
                Thread.sleep(1);
            }
        }
        catch(InterruptedException e) {}
        rawTerminal.exitPrivateMode();
    }
}
