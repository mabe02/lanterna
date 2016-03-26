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

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Some common code for the GUI tests to get a text system up and running on a separate thread
 * @author Martin
 */
public abstract class TestBase {
    void run(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        MultiWindowTextGUI textGUI = createTextGUI(screen);
        textGUI.setBlockingIO(false);
        textGUI.setEOFWhenNoWindows(true);
        textGUI.isEOFWhenNoWindows();   //No meaning, just to silence IntelliJ:s "is never used" alert

        try {
            init(textGUI);
            AsynchronousTextGUIThread guiThread = (AsynchronousTextGUIThread)textGUI.getGUIThread();
            guiThread.start();
            guiThread.waitForStop();
        }
        finally {
            screen.stopScreen();
        }
    }

    protected MultiWindowTextGUI createTextGUI(Screen screen) {
        return new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen);
    }

    public abstract void init(WindowBasedTextGUI textGUI);
}
