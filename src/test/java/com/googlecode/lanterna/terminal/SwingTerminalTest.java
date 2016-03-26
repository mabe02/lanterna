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

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import javax.swing.*;
import java.io.IOException;

/**
 *
 * @author Martin
 */
public class SwingTerminalTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        SwingTerminalFrame terminal = new TestTerminalFactory(args).createSwingTerminal();
        terminal.setVisible(true);
        terminal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.setCursorPosition(10, 5);
        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.putCharacter('l');
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.putCharacter('!');
        terminal.putCharacter(' ');
        terminal.putCharacter(Symbols.HEART);
        terminal.putCharacter(Symbols.SPADES);
        terminal.putCharacter(Symbols.CLUB);
        terminal.putCharacter(Symbols.DIAMOND);
        terminal.putCharacter(Symbols.DOUBLE_LINE_CROSS);
        terminal.putCharacter(Symbols.SINGLE_LINE_CROSS);
        terminal.putCharacter(Symbols.DOUBLE_LINE_T_DOWN);
        terminal.putCharacter(Symbols.SINGLE_LINE_VERTICAL);
        terminal.putCharacter(Symbols.SINGLE_LINE_HORIZONTAL);
        terminal.setCursorPosition(10, 7);
        terminal.enableSGR(SGR.BOLD);
        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.putCharacter('l');
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.putCharacter('!');
        terminal.putCharacter(' ');
        terminal.putCharacter(Symbols.HEART);
        terminal.putCharacter(Symbols.SPADES);
        terminal.putCharacter(Symbols.CLUB);
        terminal.putCharacter(Symbols.DIAMOND);
        terminal.putCharacter(Symbols.DOUBLE_LINE_CROSS);
        terminal.putCharacter(Symbols.SINGLE_LINE_CROSS);
        terminal.putCharacter(Symbols.DOUBLE_LINE_T_DOWN);
        terminal.putCharacter(Symbols.SINGLE_LINE_VERTICAL);
        terminal.putCharacter(Symbols.SINGLE_LINE_HORIZONTAL);
        terminal.setCursorPosition(10, 9);
        terminal.enableSGR(SGR.UNDERLINE);
        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.enableSGR(SGR.BOLD);
        terminal.putCharacter('l');
        terminal.enableSGR(SGR.UNDERLINE);
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.enableSGR(SGR.UNDERLINE);
        terminal.putCharacter('!');
        terminal.putCharacter(' ');
        terminal.putCharacter(Symbols.HEART);
        terminal.putCharacter(Symbols.SPADES);
        terminal.putCharacter(Symbols.CLUB);
        terminal.putCharacter(Symbols.DIAMOND);
        terminal.putCharacter(Symbols.DOUBLE_LINE_CROSS);
        terminal.putCharacter(Symbols.SINGLE_LINE_CROSS);
        terminal.putCharacter(Symbols.DOUBLE_LINE_T_DOWN);
        terminal.putCharacter(Symbols.SINGLE_LINE_VERTICAL);
        terminal.putCharacter(Symbols.SINGLE_LINE_HORIZONTAL);
        terminal.setCursorPosition(10, 11);
        terminal.enableSGR(SGR.BORDERED);
        terminal.putCharacter('!');
        terminal.putCharacter(' ');
        terminal.putCharacter(Symbols.HEART);
        terminal.putCharacter(Symbols.SPADES);
        terminal.putCharacter(Symbols.CLUB);
        terminal.putCharacter(Symbols.DIAMOND);
        terminal.putCharacter(Symbols.DOUBLE_LINE_CROSS);
        terminal.putCharacter(Symbols.SINGLE_LINE_CROSS);
        terminal.putCharacter(Symbols.DOUBLE_LINE_T_DOWN);
        terminal.putCharacter(Symbols.SINGLE_LINE_VERTICAL);
        terminal.putCharacter(Symbols.SINGLE_LINE_HORIZONTAL);
        terminal.resetColorAndSGR();
        terminal.setCursorPosition(0, 0);
        terminal.flush();

        Thread.sleep(5000);
        terminal.exitPrivateMode();
    }
}
