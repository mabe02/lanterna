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
 * @author martin
 */
public class ResetAllTest {
    public static void main(String[] args) throws InterruptedException, IOException {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.setCursorPosition(10, 5);
        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.enableSGR(SGR.BOLD);
        terminal.putCharacter('l');
        terminal.setForegroundColor(TextColor.ANSI.CYAN);
        terminal.putCharacter('l');
        terminal.enableSGR(SGR.REVERSE);
        terminal.putCharacter('o');
        terminal.resetColorAndSGR();
        terminal.putCharacter('!');
        terminal.setCursorPosition(0, 0);
        terminal.flush();

        Thread.sleep(5000);
        terminal.exitPrivateMode();
    }
}
