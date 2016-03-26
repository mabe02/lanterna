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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Created by martin on 17/09/14.
 */
public class MultiButtonTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen);
        textGUI.setEOFWhenNoWindows(true);
        try {
            final BasicWindow window = new BasicWindow("Button test");
            Panel contentArea = new Panel();
            contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            contentArea.addComponent(new Button(""));
            contentArea.addComponent(new Button("TRE"));
            contentArea.addComponent(new Button("Button"));
            contentArea.addComponent(new Button("Another button"));
            contentArea.addComponent(new EmptySpace(new TerminalSize(5, 1)));
            //contentArea.addComponent(new Button("Here is a\nmulti-line\ntext segment that is using \\n"));
            contentArea.addComponent(new Button("OK", new Runnable() {
                @Override
                public void run() {
                    window.close();
                }
            }));

            window.setComponent(contentArea);
            textGUI.addWindowAndWait(window);
        }
        finally {
            screen.stopScreen();
        }
    }
}
