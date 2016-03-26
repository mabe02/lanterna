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

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

/**
 *
 * @author Martin
 */
public class CJKTerminalTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        for(int i = 1; i < 5; i++) {
            terminal.setCursorPosition(i, i);
            printString(terminal, "あ い う え お");
        }
        int pos = 0;
        int line = 1;
        for(int i = 0; i < 50; i++) {
            if(terminal.pollInput() != null) {
                break;
            }
            terminal.setCursorPosition(0, 0);
            printString(terminal, pos + "x" + line);            
            terminal.setCursorPosition(pos++, line);
            if(pos == 10) {
                pos = 0;
                line++;
            }
            terminal.flush();
            Thread.sleep(2000);
        }
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
        terminal.setBackgroundColor(TextColor.ANSI.RED);
        for(int i = 0; i < 5; i++) {
            if(terminal.pollInput() != null) {
                break;
            }
            terminal.setCursorPosition(5, i + 1);
            printString(terminal, "X");
            terminal.setCursorPosition(0, 0);
            terminal.flush();
            Thread.sleep(2000);
        }
        terminal.exitPrivateMode();
    }

    private static void printString(Terminal rawTerminal, String string) throws IOException {
        for(int i = 0; i < string.length(); i++) {
            rawTerminal.putCharacter(string.charAt(i));
        }
    }
}
