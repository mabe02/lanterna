/*
 * Copyright (C) 2011 martin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.lanterna.test;

import com.googlecode.lanterna.LanternException;
import com.googlecode.lanterna.LanternTerminal;
import com.googlecode.lanterna.TerminalFactory.Common;
import com.googlecode.lanterna.terminal.Terminal;

/**
 *
 * @author martin
 */
public class ResetAllTest {
    public static void main(String[] args) throws LanternException, InterruptedException {
        Terminal terminal = new LanternTerminal().getUnderlyingTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.moveCursor(10, 5);
        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.applySGR(Terminal.SGR.ENTER_BOLD);
        terminal.putCharacter('l');
        terminal.applyForegroundColor(Terminal.Color.CYAN);
        terminal.putCharacter('l');
        terminal.applySGR(Terminal.SGR.ENTER_REVERSE);
        terminal.putCharacter('o');
        terminal.applySGR(Terminal.SGR.RESET_ALL);
        terminal.putCharacter('!');
        terminal.moveCursor(0, 0);

        Thread.sleep(5000);
        terminal.exitPrivateMode();
    }
}
