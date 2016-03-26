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
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue150 {
    public static void main(String... args) throws IOException {
        Terminal term = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(term);
        WindowManager windowManager = new DefaultWindowManager();
        Component background = new EmptySpace(TextColor.ANSI.DEFAULT);
        final WindowBasedTextGUI gui = new MultiWindowTextGUI(screen, windowManager, background);
        screen.startScreen();
        gui.addWindowAndWait(new BasicWindow("Issue150") {{
            setComponent(createUi());
        }});
        screen.stopScreen();
    }

    private static Component createUi() {
        ActionListBox actions = new ActionListBox();
        actions.addItem("Enter terminal in a strange state", new Runnable() {
            @Override public void run() {
                stub();
            }
        });
        return actions;
    }

    private static <T> T stub() {
        throw new UnsupportedOperationException("What a terrible failure!");
    }
}
