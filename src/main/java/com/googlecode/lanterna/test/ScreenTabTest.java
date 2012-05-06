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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.test;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.LanternTerminal;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;
import com.googlecode.lanterna.terminal.TerminalPosition;

/**
 *
 * @author martin
 */
public class ScreenTabTest {
    public static void main(String[] args) throws LanternaException, InterruptedException
    {
        new ScreenTabTest();
    }

    private LanternTerminal lanternTerminal;
    private Screen screen;

    public ScreenTabTest() throws LanternaException, InterruptedException
    {
        this.lanternTerminal = new LanternTerminal(new TerminalFactory.PureUnixTextEnvironment());
        if (lanternTerminal == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }
        lanternTerminal.start();
        screen = lanternTerminal.getScreen();
        screen.setCursorPosition(new TerminalPosition(0, 0));
        drawStrings("Trying out some tabs!");

        long now = System.currentTimeMillis();
        while(System.currentTimeMillis() - now < 20 * 1000) {
            Thread.yield();
        }
        lanternTerminal.stopAndRestoreTerminal();
    }

    private void drawStrings(String topTitle) throws LanternaException
    {
        ScreenWriter writer = new ScreenWriter(screen);
        writer.setForegroundColor(Terminal.Color.DEFAULT);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.fillScreen(' ');

        writer.setForegroundColor(Terminal.Color.DEFAULT);
        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.drawString(0, 0, topTitle, ScreenCharacterStyle.Blinking);
        screen.setTabBehaviour(TabBehaviour.CONVERT_TO_ONE_SPACE);
        writer.drawString(10, 1, "Four tabs: |\t|\t|\t|\t|");
        screen.setTabBehaviour(TabBehaviour.CONVERT_TO_FOUR_SPACES);
        writer.drawString(10, 2, "Four tabs: |\t|\t|\t|\t|");
        screen.setTabBehaviour(TabBehaviour.CONVERT_TO_EIGHT_SPACES);
        writer.drawString(10, 3, "Four tabs: |\t|\t|\t|\t|");
        screen.setTabBehaviour(TabBehaviour.ALIGN_TO_COLUMN_4);
        writer.drawString(10, 4, "Four tabs: |\t|\t|\t|\t|");
        screen.setTabBehaviour(TabBehaviour.ALIGN_TO_COLUMN_8);
        writer.drawString(10, 5, "Four tabs: |\t|\t|\t|\t|");

        screen.refresh();
    }
}
