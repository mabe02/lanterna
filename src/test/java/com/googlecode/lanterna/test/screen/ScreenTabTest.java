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
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author martin
 */
public class ScreenTabTest {
    public static void main(String[] args) throws InterruptedException
    {
        new ScreenTabTest(args);
    }

    private Screen screen;

    public ScreenTabTest(String[] args) throws InterruptedException
    {
        screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        screen.setCursorPosition(new TerminalPosition(0, 0));
        drawStrings("Trying out some tabs!");

        long now = System.currentTimeMillis();
        while(System.currentTimeMillis() - now < 20 * 1000) {
            Thread.yield();
        }
        screen.stopScreen();
    }

    private void drawStrings(String topTitle)
    {
        ScreenWriter writer = new ScreenWriter(screen);
        writer.setForegroundColor(Terminal.Color.DEFAULT);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.fillScreen(' ');

        writer.setForegroundColor(Terminal.Color.DEFAULT);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(0, 0, topTitle, ScreenCharacterStyle.Blinking);
        screen.setTabBehaviour(TabBehaviour.CONVERT_TO_ONE_SPACE);
        writer.drawString(10, 1, "TabBehaviour.CONVERT_TO_ONE_SPACE:    |\t|\t|\t|\t|");
        screen.setTabBehaviour(TabBehaviour.CONVERT_TO_FOUR_SPACES);
        writer.drawString(10, 2, "TabBehaviour.CONVERT_TO_FOUR_SPACES:  |\t|\t|\t|\t|");
        screen.setTabBehaviour(TabBehaviour.CONVERT_TO_EIGHT_SPACES);
        writer.drawString(10, 3, "TabBehaviour.CONVERT_TO_EIGHT_SPACES: |\t|\t|\t|\t|");
        screen.setTabBehaviour(TabBehaviour.ALIGN_TO_COLUMN_4);
        writer.drawString(10, 4, "TabBehaviour.ALIGN_TO_COLUMN_4:       |\t|\t|\t|\t|");
        screen.setTabBehaviour(TabBehaviour.ALIGN_TO_COLUMN_8);
        writer.drawString(10, 5, "TabBehaviour.ALIGN_TO_COLUMN_8:       |\t|\t|\t|\t|");

        screen.refresh();
    }
}
