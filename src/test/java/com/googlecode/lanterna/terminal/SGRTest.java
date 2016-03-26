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
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

/**
 *
 * @author Martin
 */
public class SGRTest {
    public static void main(String[] args) throws IOException {
        Terminal rawTerminal = new TestTerminalFactory(args).createTerminal();
        rawTerminal.enterPrivateMode();
        rawTerminal.clearScreen();
        
        String string = "Hello!";
        
        TextGraphics textGraphics = rawTerminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.enableModifiers(SGR.BLINK);
        textGraphics.putString(10, 2, string);
        textGraphics.disableModifiers(SGR.BLINK);
        textGraphics.enableModifiers(SGR.BOLD);
        textGraphics.putString(10, 4, string);
        textGraphics.disableModifiers(SGR.BOLD);
        textGraphics.enableModifiers(SGR.BORDERED);
        textGraphics.putString(10, 6, string);
        textGraphics.disableModifiers(SGR.BORDERED);
        textGraphics.enableModifiers(SGR.CIRCLED);
        textGraphics.putString(10, 8, string);
        textGraphics.disableModifiers(SGR.CIRCLED);
        textGraphics.enableModifiers(SGR.CROSSED_OUT);
        textGraphics.putString(10, 10, string);
        textGraphics.disableModifiers(SGR.CROSSED_OUT);
        textGraphics.enableModifiers(SGR.UNDERLINE);
        textGraphics.putString(10, 12, string);
        textGraphics.disableModifiers(SGR.UNDERLINE);
        textGraphics.enableModifiers(SGR.FRAKTUR);
        textGraphics.putString(10, 14, string);
        textGraphics.disableModifiers(SGR.FRAKTUR);
        textGraphics.enableModifiers(SGR.REVERSE);
        textGraphics.putString(10, 16, string);
        textGraphics.disableModifiers(SGR.REVERSE);
        rawTerminal.setCursorPosition(0, 0);
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
