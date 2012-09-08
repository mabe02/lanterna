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

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author martin
 */
public class TerminalInputTest
{
    public static void main(String[] args) throws InterruptedException
    {
        final Terminal rawTerminal = new TestTerminalFactory(args).createTerminal();
        rawTerminal.enterPrivateMode();

        int currentRow = 0;
        rawTerminal.moveCursor(0, 0);
        while(true) {
            Key key = rawTerminal.readInput();
            if(key == null) {
                Thread.sleep(1);
                continue;
            }
            
            if(key.getKind() == Key.Kind.Escape)
                break;
            
            if(currentRow == 0)
                rawTerminal.clearScreen();
            
            rawTerminal.moveCursor(0, currentRow++);
            putString(rawTerminal, key.toString());
            
            if(currentRow >= rawTerminal.getTerminalSize().getRows())
                currentRow = 0;
        }
        
        rawTerminal.exitPrivateMode();
    }

    private static void putString(Terminal rawTerminal, String string) {
        for(int i = 0; i < string.length(); i++)
            rawTerminal.putCharacter(string.charAt(i));
    }
}
