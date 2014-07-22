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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class TerminalColorTest {

    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics writer = new ScreenTextGraphics(screen);
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.setPosition(10, 1).putString("Hello World");

        writer.setForegroundColor(TextColor.ANSI.BLACK);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.setPosition(11, 2).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.WHITE);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.setPosition(12, 3).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.BLACK);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.enableModifiers(SGR.BOLD);
        writer.setPosition(13, 4).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.WHITE);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.setPosition(14, 5).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.setPosition(15, 6).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.disableModifiers(SGR.BOLD);
        writer.setPosition(16, 7).putString("Hello World");

        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.setPosition(10, 10).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.setPosition(11, 11).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.setPosition(12, 12).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.MAGENTA);
        writer.setPosition(13, 13).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.setPosition(14, 14).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.setPosition(15, 15).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.setPosition(16, 16).putString("Hello World");
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.MAGENTA);
        writer.setPosition(17, 17).putString("Hello World");

        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.setPosition(10, 20).putString("Hello World", SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.setPosition(11, 21).putString("Hello World", SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.setPosition(12, 22).putString("Hello World", SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.MAGENTA);
        writer.setPosition(13, 23).putString("Hello World", SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.setPosition(14, 24).putString("Hello World", SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.setPosition(15, 25).putString("Hello World", SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.setPosition(16, 26).putString("Hello World", SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.CYAN);
        writer.setBackgroundColor(TextColor.ANSI.BLUE);
        writer.setPosition(17, 27).putString("Hello World", SGR.BOLD);
        
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.RED);
        writer.setPosition(0, 0);
        writer.drawRectangle(screen.getTerminalSize(), ' ');
        
        screen.refresh();

        try {
            Thread.sleep(5000);
        } catch(InterruptedException ignored) {
        }
        screen.stopScreen();
    }
}
