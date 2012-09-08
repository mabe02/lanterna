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
package com.googlecode.lanterna.test.screen;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author Martin
 */
public class ScreenClearTest {
    public static void main(String[] args) {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        drawText(screen);
        screen.refresh();
        sleep(1000);
        screen.clear();
        screen.refresh();
        sleep(300);
        drawText(screen);
        screen.refresh();
        sleep(1000);
        screen.clear();
        screen.refresh();
        sleep(300);
        drawText(screen);
        screen.refresh();
        sleep(1000);
        screen.clear();
        screen.refresh();
        screen.stopScreen();
    }

    private static void drawText(Screen screen) {
        ScreenWriter writer = new ScreenWriter(screen);
        writer.setForegroundColor(Terminal.Color.DEFAULT);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(10, 1, "Hello World");

        writer.setForegroundColor(Terminal.Color.BLACK);
        writer.setBackgroundColor(Terminal.Color.WHITE);
        writer.drawString(11, 2, "Hello World");
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.drawString(12, 3, "Hello World");
        writer.setForegroundColor(Terminal.Color.BLACK);
        writer.setBackgroundColor(Terminal.Color.WHITE);
        writer.drawString(13, 4, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.drawString(14, 5, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.DEFAULT);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(15, 6, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.DEFAULT);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(16, 7, "Hello World");

        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(10, 10, "Hello World");
        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.WHITE);
        writer.drawString(11, 11, "Hello World");
        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.drawString(12, 12, "Hello World");
        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.MAGENTA);
        writer.drawString(13, 13, "Hello World");
        writer.setForegroundColor(Terminal.Color.GREEN);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(14, 14, "Hello World");
        writer.setForegroundColor(Terminal.Color.GREEN);
        writer.setBackgroundColor(Terminal.Color.WHITE);
        writer.drawString(15, 15, "Hello World");
        writer.setForegroundColor(Terminal.Color.GREEN);
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.drawString(16, 16, "Hello World");
        writer.setForegroundColor(Terminal.Color.GREEN);
        writer.setBackgroundColor(Terminal.Color.MAGENTA);
        writer.drawString(17, 17, "Hello World");

        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(10, 20, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.WHITE);
        writer.drawString(11, 21, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.drawString(12, 22, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.MAGENTA);
        writer.drawString(13, 23, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.GREEN);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(14, 24, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.GREEN);
        writer.setBackgroundColor(Terminal.Color.WHITE);
        writer.drawString(15, 25, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.GREEN);
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.drawString(16, 26, "Hello World", ScreenCharacterStyle.Bold);
        writer.setForegroundColor(Terminal.Color.CYAN);
        writer.setBackgroundColor(Terminal.Color.BLUE);
        writer.drawString(17, 27, "Hello World", ScreenCharacterStyle.Bold);
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        }
        catch(InterruptedException e) {}
    }
}
