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

import com.googlecode.lanterna.TerminalFacade;
import java.io.IOException;

/**
 *
 * @author Martin
 */
public class SwingTerminalTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        Terminal terminal = TerminalFacade.createSwingTerminal();
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
        terminal.moveCursor(10, 7);
        terminal.enableSGR(Terminal.SGR.BOLD);
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
        terminal.moveCursor(10, 9);
        terminal.enableSGR(Terminal.SGR.UNDERLINE);
        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.enableSGR(Terminal.SGR.BOLD);
        terminal.putCharacter('l');
        terminal.enableSGR(Terminal.SGR.UNDERLINE);
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.enableSGR(Terminal.SGR.UNDERLINE);
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
        terminal.moveCursor(10, 11);
        terminal.enableSGR(Terminal.SGR.BORDERED);
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
        terminal.resetAllSGR();
        terminal.moveCursor(0, 0);

        Thread.sleep(5000);
        terminal.exitPrivateMode();
    }
}
