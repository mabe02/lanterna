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

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

class IssueX {

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

        private int current_y = 1;
        private int default_x = 3;

        @SuppressWarnings("unused")
		private boolean conversionFinished = false;

        public LanternaTerminalWriter(String[] args) throws IOException {
            screen = new TestTerminalFactory(args).createScreen();
            screen.startScreen();

            screenWriter = screen.newTextGraphics();
        }

        public void close() throws IOException {
            screen.stopScreen();
            conversionFinished = true;
        }

        public void write(String string, SGR... styles) throws IOException {
            screenWriter.enableModifiers(styles);
            screenWriter.putString(default_x, current_y, string);
            screen.pollInput();
            screen.refresh();
        }

    }
}

