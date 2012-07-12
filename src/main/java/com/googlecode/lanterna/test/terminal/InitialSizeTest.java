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
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.test.terminal;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author Martin
 */
public class InitialSizeTest {
    public static void main(String[] args) {
        final Terminal rawTerminal = new TestTerminalFactory(args).createTerminal();
        rawTerminal.enterPrivateMode();
        rawTerminal.clearScreen();
        
        rawTerminal.moveCursor(5, 5);
        printString(rawTerminal, "Waiting for initial size...");
        rawTerminal.flush();
        
        TerminalSize initialSize = rawTerminal.getTerminalSize();
        rawTerminal.clearScreen();
        rawTerminal.moveCursor(5, 5);
        printString(rawTerminal, "Initial size: ");
        rawTerminal.applySGR(Terminal.SGR.ENTER_BOLD);
        printString(rawTerminal, initialSize.toString());
        rawTerminal.applySGR(Terminal.SGR.RESET_ALL);
        rawTerminal.flush();        
        
        try {
            Thread.sleep(5000);
        }
        catch(InterruptedException e) {}
        rawTerminal.exitPrivateMode();
    }

    private static void printString(Terminal rawTerminal, String string) {
        for(int i = 0; i < string.length(); i++) {
            rawTerminal.putCharacter(string.charAt(i));
        }
    }
}
