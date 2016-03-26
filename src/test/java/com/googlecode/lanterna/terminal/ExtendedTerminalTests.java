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

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

import java.io.IOException;

/**
 * Test for the somewhat supported "CSI 8 ; rows ; columns; t" command
 */
public class ExtendedTerminalTests {
    public static void main(String[] args) throws IOException, InterruptedException {
        ExtendedTerminal extendedTerminal = new UnixTerminal();
        extendedTerminal.clearScreen();
        TextGraphics textGraphics = extendedTerminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(4, 4, "Please wait four seconds and the terminal will be resized", SGR.BLINK);
        extendedTerminal.flush();
        Thread.sleep(4 * 1000);
        extendedTerminal.setTerminalSize(80, 40);
        extendedTerminal.clearScreen();
        textGraphics.putString(4, 4, "There, did anything happen? Will set a title in 3 seconds...");
        extendedTerminal.flush();
        Thread.sleep(3 * 1000);
        extendedTerminal.setTitle("ExtendedTerminalTests");
        extendedTerminal.clearScreen();
        textGraphics.putString(4, 4, "Check the title, did it change?");
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.putString(0, textGraphics.getSize().getRows() - 1, "Will terminate in 3 seconds...", SGR.BLINK);
        extendedTerminal.setCursorPosition(0, 0);
        extendedTerminal.flush();
        Thread.sleep(3 * 1000);
        extendedTerminal.clearScreen();
        extendedTerminal.resetColorAndSGR();
        extendedTerminal.flush();

        //Drain the input queue (could hold the size reply)
        //noinspection StatementWithEmptyBody
        while(extendedTerminal.pollInput() != null);
    }
}
