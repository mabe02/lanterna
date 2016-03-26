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
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger;

import java.awt.*;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class PrivateModeTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        Terminal terminal = new TestTerminalFactory(args, TerminalEmulatorAutoCloseTrigger.DoNotAutoClose).createTerminal();
        boolean normalTerminal = true;
        printNormalTerminalText(terminal);
        KeyStroke keyStroke = null;
        while(keyStroke == null || keyStroke.getKeyType() != KeyType.Escape) {
            keyStroke = terminal.pollInput();
            if(keyStroke != null && keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ') {
                normalTerminal = !normalTerminal;
                if(normalTerminal) {
                    terminal.exitPrivateMode();
                    printNormalTerminalText(terminal);
                }
                else {
                    terminal.enterPrivateMode();
                    printPrivateModeTerminalText(terminal);
                }
            }
            else {
                Thread.sleep(1);
            }
        }
        if(!normalTerminal) {
            terminal.exitPrivateMode();
        }
        terminal.putCharacter('\n');
        if(terminal instanceof Window) {
            ((Window) terminal).dispose();
        }
    }

    private static void printNormalTerminalText(Terminal terminal) throws IOException {
        terminal.clearScreen();
        terminal.setCursorPosition(5, 3);
        String text = "Normal terminal, press space to switch";
        for(int i = 0; i < text.length(); i++) {
            terminal.putCharacter(text.charAt(i));
        }
        terminal.flush();
    }

    private static void printPrivateModeTerminalText(Terminal terminal) throws IOException {
        terminal.clearScreen();
        terminal.setCursorPosition(5, 3);
        String text = "Private mode terminal, press space to switch";
        for(int i = 0; i < text.length(); i++) {
            terminal.putCharacter(text.charAt(i));
        }
        terminal.flush();
    }
}
