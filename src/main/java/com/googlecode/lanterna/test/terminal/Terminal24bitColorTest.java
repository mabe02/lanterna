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
import java.util.Random;

/**
 * This class will try using the 24-bit color extension supported by a few terminal emulators
 * @author Martin
 */
public class Terminal24bitColorTest {
    public static void main(String[] args)
    {
        final String string = "Hello!";
        Random random = new Random();
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        TerminalSize size = terminal.getTerminalSize();
        
        while(true) {
            if(terminal.readInput() != null) {
                terminal.exitPrivateMode();
                return;
            }
            
            terminal.applyForegroundColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            terminal.applyBackgroundColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            terminal.moveCursor(random.nextInt(size.getColumns() - string.length()), random.nextInt(size.getRows()));
            printString(terminal, string);
            
            try {
                Thread.sleep(200);
            }
            catch(InterruptedException e) {
            }
        }
    }
    
    private static void printString(Terminal terminal, String string) {
        for(int i = 0; i < string.length(); i++)
            terminal.putCharacter(string.charAt(i));
    }            
}
