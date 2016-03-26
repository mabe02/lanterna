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
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue155 {
    public static void main(String... args) throws IOException {
        Terminal term = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(term);
        WindowManager windowManager = new DefaultWindowManager();
        Component background = new EmptySpace(TextColor.ANSI.DEFAULT);
        final WindowBasedTextGUI gui = new MultiWindowTextGUI(screen, windowManager, background);
        screen.startScreen();
        gui.addWindowAndWait(new BasicWindow("Issue155") {{
            setComponent(createUi(gui, this));
        }});
        screen.stopScreen();
    }


    private static Panel createUi(WindowBasedTextGUI gui, BasicWindow window) {
        return createUi(gui, window, 1);
    }

    private static Panel createUi(WindowBasedTextGUI gui, final BasicWindow window, final int counter) {
        final int nextCounter = counter + 3;
        return Panels.vertical(
                new Button("Open Dialog (and crush stuff)", openDialog(gui, window, nextCounter)),
                new CheckBoxList<String>() {{
                    for (int i = counter; i < nextCounter; ++i) {
                        addItem(String.valueOf(i));
                    }
                }},
                new Button("Quit", new Runnable() {
                    @Override public void run() { window.close(); }
                })
        );
    }

    private static Runnable openDialog(final WindowBasedTextGUI gui, final BasicWindow window, final int counter) {
        return new Runnable() {
            @Override public void run() {
                new ActionListDialogBuilder().
                        setCanCancel(true).
                        addAction("Reinstall UI (this crashes everything)", setupUI(gui, window, counter)).
                        build().
                        showDialog(gui);
            }
        };
    }

    private static Runnable setupUI(final WindowBasedTextGUI gui, final BasicWindow window, final int counter) {
        return new Runnable() {
            @Override public void run() {
                window.setComponent(createUi(gui, window, counter));
            }
        };
    }
}
