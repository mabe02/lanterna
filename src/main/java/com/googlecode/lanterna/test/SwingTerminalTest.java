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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.test;

import com.googlecode.lanterna.LanternException;
import com.googlecode.lanterna.LanternTerminal;
import com.googlecode.lanterna.terminal.TerminalFactory;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.Terminal;

/**
 *
 * @author mabe02
 */
public class SwingTerminalTest {
    public static void main(String[] args) throws LanternException, InterruptedException
    {
        Terminal terminal = new LanternTerminal(new TerminalFactory.Swing()).getUnderlyingTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.moveCursor(10, 5);
        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.putCharacter('l');
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.putCharacter('!');
        terminal.putCharacter(' ');
        terminal.putCharacter(ACS.HEART);
        terminal.putCharacter(ACS.SPADES);
        terminal.putCharacter(ACS.CLUB);
        terminal.putCharacter(ACS.DIAMOND);
        terminal.putCharacter(ACS.DOUBLE_LINE_CROSS);
        terminal.putCharacter(ACS.SINGLE_LINE_CROSS);
        terminal.putCharacter(ACS.DOUBLE_LINE_T_DOWN);
        terminal.putCharacter(ACS.SINGLE_LINE_VERTICAL);
        terminal.putCharacter(ACS.SINGLE_LINE_HORIZONTAL);
        terminal.moveCursor(0, 0);

        Thread.sleep(5000);
        terminal.exitPrivateMode();
    }
}
