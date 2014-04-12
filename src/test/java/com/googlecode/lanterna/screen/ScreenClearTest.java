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
package com.googlecode.lanterna.test.screen;

import com.googlecode.lanterna.screen.DefaultScreen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TextColor;
import com.googlecode.lanterna.test.TestTerminalFactory;
import java.io.IOException;

/**
 *
 * @author Martin
 */
public class ScreenClearTest {
    public static void main(String[] args) throws IOException {
        DefaultScreen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        for(int i = 0; i < 10; i++) {
            drawText(screen);
            screen.refresh();
            sleep(1000);
            screen.clear();
            screen.refresh();
            sleep(300);
        }
        screen.stopScreen();
    }

    private static void drawText(DefaultScreen screen) {
        ScreenWriter writer = new ScreenWriter(screen);
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(10, 1, "Hello World");

        writer.setForegroundColor(TextColor.ANSI.BLACK);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.putString(11, 2, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.WHITE);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.putString(12, 3, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.BLACK);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.putString(13, 4, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.WHITE);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.putString(14, 5, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(15, 6, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(16, 7, "Hello World");

        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(10, 10, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.putString(11, 11, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.putString(12, 12, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.MAGENTA);
        writer.putString(13, 13, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(14, 14, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.putString(15, 15, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.putString(16, 16, "Hello World");
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.MAGENTA);
        writer.putString(17, 17, "Hello World");

        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(10, 20, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.putString(11, 21, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.putString(12, 22, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.BLUE);
        writer.setBackgroundColor(TextColor.ANSI.MAGENTA);
        writer.putString(13, 23, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(14, 24, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.WHITE);
        writer.putString(15, 25, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.GREEN);
        writer.setBackgroundColor(TextColor.ANSI.BLACK);
        writer.putString(16, 26, "Hello World", Terminal.SGR.BOLD);
        writer.setForegroundColor(TextColor.ANSI.CYAN);
        writer.setBackgroundColor(TextColor.ANSI.BLUE);
        writer.putString(17, 27, "Hello World", Terminal.SGR.BOLD);
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        }
        catch(InterruptedException e) {}
    }
}
