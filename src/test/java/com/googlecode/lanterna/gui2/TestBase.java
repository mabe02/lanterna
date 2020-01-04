/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.bundle.LanternaThemes;
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
        String theme = extractTheme(args);
        if(theme != null) {
            textGUI.setTheme(LanternaThemes.getRegisteredTheme(theme));
        }
        textGUI.setBlockingIO(false);
        textGUI.setEOFWhenNoWindows(true);
        //noinspection ResultOfMethodCallIgnored
        textGUI.isEOFWhenNoWindows();   //No meaning, just to silence IntelliJ:s "is never used" alert

        try {
            init(textGUI);
            AsynchronousTextGUIThread guiThread = (AsynchronousTextGUIThread)textGUI.getGUIThread();
            guiThread.start();
            afterGUIThreadStarted(textGUI);
            guiThread.waitForStop();
        }
        finally {
            screen.stopScreen();
        }
    }

    private String extractTheme(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("--theme") && i + 1 < args.length) {
                return args[i+1];
            }
        }
        return null;
    }

    protected MultiWindowTextGUI createTextGUI(Screen screen) {
        return new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen);
    }

    public abstract void init(WindowBasedTextGUI textGUI);
    public void afterGUIThreadStarted(WindowBasedTextGUI textGUI) {
        // By default do nothing
    }
}
