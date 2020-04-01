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
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.graphics.DefaultMutableThemeStyle;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

class Issue453 {

    public static void main(String[] args) throws InterruptedException, IOException {
        LanternaTerminalWriter writer = new LanternaTerminalWriter(args);
        for (int i = 0; i < 1000; i++) {
            writer.write(String.valueOf(i), SGR.BOLD);
            Thread.sleep(100);
        }
        writer.close();
    }

    public static class LanternaTerminalWriter {

        private Screen screen;
        private TextGraphics screenWriter;

        public LanternaTerminalWriter(String[] args) throws IOException {
            screen = new TestTerminalFactory(args).createScreen();
            screen.startScreen();
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
    				new EmptySpace(TextColor.ANSI.BLACK));
            
            assertNotNull(new DefaultMutableThemeStyle(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK, new SGR[] {}));
            screenWriter = screen.newTextGraphics();
        }

        public void close() throws IOException {
            screen.stopScreen();
        }

        public void write(String string, SGR... styles) throws IOException {
            screenWriter.enableModifiers(styles);
            int current_y = 1;
            int default_x = 3;
            screenWriter.putString(default_x, current_y, string);
            screen.pollInput();
            screen.refresh();
        }

    }
}

