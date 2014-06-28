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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.screen.DefaultScreen;
import com.googlecode.lanterna.terminal.TextColor;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

/**
 *
 * @author Martin
 */
public class DialogsTextGUIBasicTest {
    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGUI textGUI = new DefaultWindowTextGUI(screen, null, new GUIElement() {
            @Override
            public void draw(TextGUIGraphics graphics) {
                graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                graphics.fill(' ');
            }

            @Override
            public boolean isInvalid() {
                return false;
            }
        });
        textGUI.start();
        try {
            textGUI.waitForStop();
        }
        catch(InterruptedException e) {}
        //This text GUI will terminate itself, so we don't need to

        screen.stopScreen();
    }
}
