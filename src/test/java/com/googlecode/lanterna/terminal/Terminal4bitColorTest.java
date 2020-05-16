/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */

package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.io.IOException;
import java.util.Random;

public class Terminal4bitColorTest {
    public static void main(String[] args) throws IOException {
        Terminal rawTerminal = new TestTerminalFactory(args).createTerminal();
        if (rawTerminal instanceof SwingTerminalFrame) {
            SwingTerminalFrame extendedTerminal = (SwingTerminalFrame) rawTerminal;
            extendedTerminal.setSize(1500, 600);
        }
        rawTerminal.enterPrivateMode();
        rawTerminal.clearScreen();

        for (TextColor.ANSI fg : TextColor.ANSI.values()) {
            rawTerminal.resetColorAndSGR();
            char[] charArray = fg.name().toCharArray();
            for (int i = 0; i < 14; i++) {
                char c = i < charArray.length ? charArray[i] : ' ';
                rawTerminal.putCharacter(c);
            }
            rawTerminal.putCharacter(' ');
            rawTerminal.setForegroundColor(fg);
            for (TextColor.ANSI bg : TextColor.ANSI.values()) {
                rawTerminal.setBackgroundColor(bg);
                rawTerminal.putCharacter(' ');
                rawTerminal.putCharacter('x');
                rawTerminal.putCharacter('Y');
                rawTerminal.putCharacter('z');
                rawTerminal.putCharacter(' ');
            }
            rawTerminal.putCharacter('\n');
        }

        rawTerminal.flush();
        try {
            while(rawTerminal.pollInput() == null) {
                Thread.sleep(1);
            }
        }
        catch(InterruptedException e) {}
        rawTerminal.exitPrivateMode();
    }
}
