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

package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

/**
 *
 * @author Martin
 */
public class BlinkTest {
    public static void main(String[] args) throws IOException {
        Terminal rawTerminal = new TestTerminalFactory(args).createTerminal();
        rawTerminal.enterPrivateMode();
        rawTerminal.clearScreen();
        rawTerminal.setForegroundColor(TextColor.ANSI.RED);
        rawTerminal.enableSGR(SGR.BLINK);
        rawTerminal.setCursorPosition(10, 10);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.setCursorPosition(0, 0);
        rawTerminal.flush();
        try {
            Thread.sleep(5000);
        }
        catch(InterruptedException e) {}
        rawTerminal.exitPrivateMode();
    }
}
