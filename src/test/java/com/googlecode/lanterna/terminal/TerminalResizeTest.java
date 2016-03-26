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

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

/**
 *
 * @author Martin
 */
public class TerminalResizeTest implements TerminalResizeListener {

    public static void main(String[] args) throws InterruptedException, IOException {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.setCursorPosition(10, 5);
        terminal.putCharacter('H');
        terminal.putCharacter('e');
        terminal.putCharacter('l');
        terminal.putCharacter('l');
        terminal.putCharacter('o');
        terminal.putCharacter('!');
        terminal.setCursorPosition(0, 0);
        terminal.flush();
        terminal.addResizeListener(new TerminalResizeTest());

        while(true) {
            KeyStroke key = terminal.pollInput();
            if(key == null || key.getCharacter() != 'q') {
                Thread.sleep(1);
            }
            else {
                break;
            }
        }
        terminal.exitPrivateMode();
    }

    @Override
    public void onResized(Terminal terminal, TerminalSize newSize) {
        try {
            terminal.setCursorPosition(0, 0);
            String string = newSize.getColumns() + "x" + newSize.getRows() + "                     ";
            char[] chars = string.toCharArray();
            for(char c : chars) {
                terminal.putCharacter(c);
            }
            terminal.flush();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
