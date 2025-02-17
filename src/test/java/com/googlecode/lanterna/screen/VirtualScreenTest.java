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
 * Copyright (C) 2010-2024 Martin Berglund
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

/**
 * Test for VirtualScreen class
 * @author Martin
 */
public class VirtualScreenTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        new VirtualScreenTest(args);
    }

    public VirtualScreenTest(String[] args) throws InterruptedException, IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen = new VirtualScreen(screen);
        screen.startScreen();

        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
        textGraphics.fillTriangle(TerminalPosition.of(40, 0), TerminalPosition.of(25,19), TerminalPosition.of(65, 19), ' ');
        textGraphics.setBackgroundColor(TextColor.ANSI.RED);
        textGraphics.drawRectangle(TerminalPosition.OF_0x0, screen.getTerminalSize(), ' ');
        screen.refresh();

        while(true) {
            KeyStroke keyStroke = screen.pollInput();
            if(keyStroke != null) {
                if(keyStroke.getKeyType() == KeyType.ESCAPE) {
                    break;
                }
            }
            else if(screen.doResizeIfNecessary() != null) {
                screen.refresh();
            }
            else {
                Thread.sleep(1);
            }
        }
        screen.stopScreen();
    }
}
